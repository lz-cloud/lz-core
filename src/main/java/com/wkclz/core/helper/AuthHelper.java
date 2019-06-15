package com.wkclz.core.helper;

import com.alibaba.fastjson.JSONObject;
import com.wkclz.core.base.Result;
import com.wkclz.core.pojo.dto.User;
import com.wkclz.core.pojo.enums.SystemConfig;
import com.wkclz.core.util.RegularUtil;
import com.wkclz.core.util.SecretUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Description:
 * Created: wangkaicun @ 2019-02-13 20:55:11
 */
@Component
public class AuthHelper extends BaseHelper {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired(required = false)
    private JedisHelper jedisHelper;
    @Autowired
    private OrgDomainHelper orgDomainHelper;
    @Autowired
    private SystemConfigHelper systemConfigHelper;

    /**
     * 设置session
     * @param req
     * @return
     */

    public Map<String,String> setSession(HttpServletRequest req, User user){
        return setSession(req, null, user);
    }
    public Map<String,String> setSession(HttpServletRequest req, HttpServletResponse rep, User user){

        if (user.getAuthId()==null){
            throw new RuntimeException("authId can not be null to setSession");
        }

        Map<String,String> tokenMap = new HashMap<>();

        // 已经登录的情况
        Object session = getSession(req);
        if(session!=null){
            user = (User)session;
            if (user.getToken()!=null){
                tokenMap.put("token",user.getToken());
                return tokenMap;
            }
        }

        // session 对象
        // 集群情况下, 经过检测后的 token 是正确的，将用来恢复 session，此时 log 为空，token 一定正确
        if (user.getToken() == null){
            String token = SecretUtil.getKey();
            user.setToken(token);
        }

        // 设置 redis
        jedisHelper.STRINGS.set(user.getToken(), JSONObject.toJSONString(user));
        jedisHelper.expire(user.getToken(),getSessionLiveTime());

        // 登录成功日志【仅在登录的时候需要，漫游时不需要】
        if (session == null){
            // 登录请求，漫游
            logger.info("漫游请求，重建，uri: {}",req.getRequestURI());
        } else {
            // 登录请求，漫游
            logger.info("正常登录请求，建立，uri: {}",req.getRequestURI());
        }

        tokenMap.put("token",user.getToken());

        // rep 传入支持 cookie 设置
        if (rep != null){
            addCookie(req, rep, "token", user.getToken(), getSessionLiveTime());
        }

        return tokenMap;
    }


    /**
     * 设置session
     * @param req
     * @return
     */
    public Map<String,String> setTempSession(HttpServletRequest req){
        User user = getSession(req);
        String token = "temp_" + SecretUtil.getKey();
        jedisHelper.STRINGS.set(token,JSONObject.toJSONString(user));
        jedisHelper.expire(user.getToken(),getSessionLiveTime());

        Map<String,String> tokenMap = new HashMap<>();
        tokenMap.put("token",token);
        return tokenMap;
    }


    /**
     * 获取 session
     * @param req
     * @return
     */
    public User getSession(HttpServletRequest req){
        if (req == null){
            return null;
        }
        String token = BaseHelper.getTokenFromRequest(req);
        if (StringUtils.isBlank(token)){
            token = BaseHelper.getTokenFromCookies(req);
        }
        return getSession(token);
    }

    /**
     * 获取 session
     * @param token
     * @return
     */
    public User getSession(String token){
        if (StringUtils.isBlank(token)){
            return null;
        }
        String userStr = jedisHelper.STRINGS.get(token);
        if (userStr == null){
            return null;
        }
        // 延期 redis
        jedisHelper.expire(token, getSessionLiveTime());
        User user = JSONObject.parseObject(userStr, User.class);
        return user;
    }




    /**
     * 退出登录
     * @param req
     */
    public void invalidateSession(HttpServletRequest req){
        User user;
        // 已经登录的情况
        Object session = getSession(req);
        if(session!=null){
            user = (User)session;
            if (user.getToken()!=null){
                jedisHelper.KEYS.expired(user.getToken(), 0);
            }
        }
        req.getSession().invalidate();
    }

    /**
     * 仅注销指定的 token
     */
    public void invalidateSession(String token){
        jedisHelper.KEYS.expired(token, 0);
    }


    public Long getOrgId(HttpServletRequest req){
        if (req == null){
            throw new RuntimeException("Request is null, can not get any information of the organization!");
        }
        Long orgId = orgDomainHelper.getOrgId(req);
        if (orgId == null || orgId < 1){
            throw new RuntimeException("Can not get any information of the organization, domain is not definition!, url is " +
                OrgDomainHelper.getDomain(req) + req.getRequestURI()
            );
        }
        return orgId;
    }

    public Long getOrgIdIfNotNull(HttpServletRequest req){
        return orgDomainHelper.getOrgId(req);
    }

    /**
     * 添加 cookie
     * @param rep
     * @param name
     * @param value
     * @param maxAge
     */
    private void addCookie(HttpServletRequest req, HttpServletResponse rep,String name,String value, Integer maxAge){

        String cookieDomain = systemConfigHelper.getSystemConfig(SystemConfig.COOKIE_DOMAIN.getKey());

        if (StringUtils.isBlank(cookieDomain)){
            cookieDomain = OrgDomainHelper.getDomain(req);
            if (!RegularUtil.isIp(cookieDomain)){
                int indexOf = cookieDomain.indexOf(".");
                cookieDomain = cookieDomain.substring(indexOf+1);
            }
        }

        /*
        try {
            value = URLEncoder.encode(value,"UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        */

        Cookie cookie = new Cookie(name, value);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setDomain(cookieDomain);

        if(maxAge!=null&&maxAge>0) {
            cookie.setMaxAge(maxAge);
        }
        rep.addCookie(cookie);
    }

    /**
     * 根据名字获取cookie
     * @param req
     * @param name
     * @return
     */
    private static Cookie getCookieByName(HttpServletRequest req,String name){
        Map<String,Cookie> cookieMap = readCookieMap(req);
        if(cookieMap.containsKey(name)){
            Cookie cookie = cookieMap.get(name);
            return cookie;
        }else{
            return null;
        }
    }

    /**
     * 将cookie封装到Map里面
     * @param req
     * @return
     */
    private static Map<String,Cookie> readCookieMap(HttpServletRequest req){
        Map<String,Cookie> cookieMap = new HashMap<String,Cookie>();
        Cookie[] cookies = req.getCookies();
        if(null!=cookies){
            for(Cookie cookie : cookies){
                cookieMap.put(cookie.getName(), cookie);
            }
        }
        return cookieMap;
    }




    /**
     * 用户 token 检测
     * @param req
     * @param rep
     * @return
     */
    public boolean checkUserToken(HttpServletRequest req, HttpServletResponse rep) {

        String uri = req.getRequestURI();

        // 严格限制 token 传递的位置
        String heaterToken = req.getHeader("token");
        String parameterToken = req.getParameter("token");

        if (StringUtils.isNotBlank(heaterToken) && heaterToken.length() > 63){
            logger.info("heaterToken is not allow, heaterToken: {}", heaterToken);
            Result result = new Result();
            result.setMoreError(Result.TOKEN_ILLEGAL_LENGTH);
            return Result.responseError(rep,result);
        }

        if (StringUtils.isNotBlank(parameterToken) && parameterToken.length() > 63){
            logger.info("parameterToken is not allow, parameterToken: {}", parameterToken);
            Result result = new Result();
            result.setMoreError(Result.TOKEN_ILLEGAL_LENGTH);
            return Result.responseError(rep,result);
        }

        if ( (!StringUtils.isBlank(heaterToken) && heaterToken.startsWith("temp_")) || !StringUtils.isBlank(parameterToken) && !parameterToken.startsWith("temp_") ){
            logger.info("token illegal transfer, uri : {}, ip: {}", uri, IpHelper.getIpAddr(req));
            Result result = new Result();
            result.setMoreError(Result.TOKEN_ILLEGAL_TRANSFER);
            return Result.responseError(rep,result);
        }

        // token 检测，需要检测权限的uri，没有 token均不放过
        String token = BaseHelper.getTokenFromRequest(req);

        if (StringUtils.isBlank(token)){
            logger.info("token is null, uri : {}", uri, IpHelper.getIpAddr(req));
            Result result = new Result();
            result.setMoreError(Result.TOKEN_UNLL);
            return Result.responseError(rep,result);
        }

        // 到 redis 去查找，找不到，不放过
        User user = getSession(req);
        if (user == null){

            Result result = new Result();
            invalidateSession(req);
            logger.info("token is error, uri : {}, ip: {}", uri, IpHelper.getIpAddr(req));
            result.setMoreError(Result.TOKEN_ERROR);
            return Result.responseError(rep,result);
            /*
            // 对开发环境直接赋值登录
            if (EnvType.DEV == EnvType.CURRENT_ENV){
                userDto = new User();
                userDto.setAuthId(1);
                logger.info("token不正确，启用开发者模式，将拟用用户 "+userDto.getAuthId()+" 进行自动登录。如需以其他用户进行测试，请先请求登录接口拿到正确 token");
            } else {
                authHelper.invalidateSession(req);
                logger.info("token is error, uri : {}",uri);
                result.setMoreError(Result.TOKEN_ERROR);
                return Result.responseError(rep,result);
            }
            */
        }


        // IP有变化的，不放过
        /*
        String ip = IpHelper.getIpAddr(req);
        if ( userDto != null && !StringUtils.isBlank(userDto.getIp()) && !ip.equalsIgnoreCase(userDto.getIp()) ){
            logger.info("client is changed, uri : {}",uri);
            Result result = new Result();
            result.setError(ErrorBase.CLIENT_CHANGE);
            return ErrorBase.responseError(rep,result);
        }
        */

        // 管理后台
        String origin = req.getHeader("Origin");
        Long orgId = getOrgIdIfNotNull(req);
        if (StringUtils.isNotBlank(origin) && orgId != null && origin.contains("admin.")){
            List<Integer> adminIds = user.getAdminIds();
            if (adminIds == null || adminIds.size() == 0 || !adminIds.contains(orgId)){
                logger.info("user is not admin, uri : {}",uri);
                Result result = new Result();
                result.setError("非法访问管理后台");
                return Result.responseError(rep,result);
            }
        }


        // session检测，已经有session的， token 对的，放过
        if ( user != null && !StringUtils.isBlank(user.getToken()) && token.equalsIgnoreCase(user.getToken())){
            // session 没有的情况，重新赋值
            User userSession = (User) req.getSession().getAttribute("user");
            if(userSession == null){
                req.getSession().setAttribute("user",user);
            }
            return true;
        }

        // 只有返回true才会继续向下执行，返回false取消当前请求
        return false;
    }

    /**
     * 如果 session 不存在于内存，需要创建
     * @param req
     */
    public void checkUserSession(HttpServletRequest req){
        User user = (User) req.getSession().getAttribute("user");
        if(user == null){
            user = getSession(req);
            if (user != null){
                req.getSession().setAttribute("user",user);
            }
        }

    }
}

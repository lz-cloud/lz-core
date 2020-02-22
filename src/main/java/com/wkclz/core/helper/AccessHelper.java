package com.wkclz.core.helper;

import cn.hutool.http.useragent.UserAgent;
import cn.hutool.http.useragent.UserAgentUtil;
import com.alibaba.fastjson.JSONArray;
import com.wkclz.core.base.Sys;
import com.wkclz.core.exception.BizException;
import com.wkclz.core.pojo.dto.User;
import com.wkclz.core.pojo.entity.AccessLog;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.CollectionUtils;
import org.springframework.util.PathMatcher;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * Description:
 * Created: wangkaicun @ 2019-02-13 20:55:11
 */
@Component
public class AccessHelper extends BaseHelper {

    @Autowired
    private AuthHelper authHelper;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    private static final PathMatcher MATCHER = new AntPathMatcher();
    private static final String NAME_SPACE = "_ACCESS_URI";

    /**
     * redis 的缓存主动更新，java 的缓存被动更新
     */
    private static Long JAVA_LAST_ACTIVE_TIME = null;
    private static List<String> ACCESS_URI = null;

    /**
     * 初始化 accessUris
     *
     * @param accessUris
     */
    public void setAccessUris(List<String> accessUris) {
        if (CollectionUtils.isEmpty(accessUris)) {
            throw BizException.error("accessUris can not be null or empty!");
        }
        stringRedisTemplate.opsForValue().set(Sys.APPLICATION_GROUP + NAME_SPACE, JSONArray.toJSONString(accessUris));
        ACCESS_URI = accessUris;
    }

    private synchronized List<String> getAccessUri() {
        Integer liveTime = getJavaCacheLiveTime();
        // java 缓存
        if (JAVA_LAST_ACTIVE_TIME != null && ACCESS_URI != null) {
            Long ttl = Long.valueOf(System.currentTimeMillis() - JAVA_LAST_ACTIVE_TIME);
            if (ttl.compareTo(Long.valueOf(liveTime) * 1000) < 0) {
                return ACCESS_URI;
            }
        }
        JAVA_LAST_ACTIVE_TIME = System.currentTimeMillis();

        // redis 拉取
        String accessUrisStr = stringRedisTemplate.opsForValue().get(Sys.APPLICATION_GROUP + NAME_SPACE);
        List<String> accessUris = JSONArray.parseArray(accessUrisStr, String.class);
        ACCESS_URI = accessUris;
        return ACCESS_URI;
    }

    public boolean checkAccessUri(HttpServletRequest req) {
        // 防止重复检测
        boolean access = isAccess(req);
        if (access){
            return true;
        }

        List<String> accessUris = getAccessUri();

        if (CollectionUtils.isEmpty(accessUris)) {
            throw BizException.error("accessUris must be init after system start up!");
        }

        String uri = req.getRequestURI();
        if (accessUris.contains(uri)) {
            req.setAttribute("access","true");
            return true;
        }
        // PathMatcher 匹配
        for (String accessUri : accessUris) {
            if (accessUri.contains("**")) {
                boolean match = MATCHER.match(accessUri, uri);
                if (match) {
                    req.setAttribute("access","true");
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 检测通过标识
     * @param req
     * @return
     */
    public static boolean isAccess(HttpServletRequest req){
        String access = req.getHeader("access");
        if (access != null && "true".equals(access)){
            return true;
        }
        Object accessObj = req.getAttribute("access");
        if (accessObj != null && "true".equals(accessObj)){
            return true;
        }
        return false;
    }

    public AccessLog getAccessLog(HttpServletRequest req) {

        // 此处为全部检测跳过标识。
        String uri = req.getRequestURI();
        if (uri.endsWith(".html") || uri.endsWith(".js") || uri.endsWith(".jpg") || uri.endsWith(".png") || uri.endsWith(".css") || uri.endsWith(".ico")) {
            return null;
        }
        if ("/error".equals(uri) || uri.startsWith("/websocket")) {
            return null;
        }

        AccessLog log = new AccessLog();

        String userAgentHeader = req.getHeader("User-Agent");

        if (userAgentHeader != null) {
            UserAgent ua = UserAgentUtil.parse(userAgentHeader);
            log.setUserAgent(userAgentHeader);
            log.setBrowserName(ua.getBrowser() == null? null:ua.getBrowser().toString());
            log.setBrowserVersion(ua.getVersion());
            log.setEngineName(ua.getEngine() == null ? null:ua.getEngine().toString());
            log.setEngineVersion(ua.getEngineVersion());
            log.setUserOs(ua.getOs() == null ? null:ua.getOs().toString());
            log.setPlatform(ua.getPlatform() == null ? null:ua.getPlatform().toString());
        }

        log.setOsName(System.getProperty("os.name"));
        log.setOsVersion(System.getProperty("os.version"));
        log.setOsArch(System.getProperty("os.arch"));

        log.setHttpProtocol(req.getProtocol());
        log.setCharacterEncoding(req.getCharacterEncoding());
        log.setAccept(req.getHeader("Accept"));
        log.setAcceptLanguage(req.getHeader("Accept-Language"));
        log.setAcceptEncoding(req.getHeader("Accept-Encoding"));
        log.setConnection(req.getHeader("Connection"));
        log.setCookie(req.getHeader("Cookie"));
        log.setOrigin(req.getHeader("Origin"));
        log.setReferer(req.getHeader("Referer"));

        log.setRequestUrl(req.getRequestURL().toString());
        log.setRequestUri(req.getRequestURI());
        log.setQueryString(req.getQueryString());
        log.setRemoteAddr(IpHelper.getOriginIp(req));
        log.setRemotePort(req.getRemotePort());
        log.setLocalAddr(req.getLocalAddr());
        log.setLocalName(req.getLocalName());
        log.setMethod(req.getMethod());
        log.setServerName(req.getServerName());
        log.setToken(BaseHelper.getToken(req));

        if (!StringUtils.isBlank(log.getToken())) {
            User user = authHelper.getUser();
            if (user != null) {
                log.setAuthId(user.getAuthId());
                log.setUserId(user.getUserId());
                log.setNickName(user.getUsername());
            }
        }

        // 访问的租户
        Long tenantId = authHelper.getTenantId();
        log.setTenantId(tenantId);


        // 防止太长
        if (log.getUserAgent() != null && log.getUserAgent().length() > 1000) {
            log.setUserAgent(log.getUserAgent().substring(0, 1000));
        }
        if (log.getCookie() != null && log.getCookie().length() > 4000) {
            log.setCookie(log.getCookie().substring(0, 4000));
        }
        if (log.getQueryString() != null && log.getQueryString().length() > 1000) {
            log.setQueryString(log.getQueryString().substring(0, 1000));
        }

        return log;
    }


}












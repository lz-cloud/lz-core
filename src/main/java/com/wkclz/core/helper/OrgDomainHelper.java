package com.wkclz.core.helper;

import com.alibaba.fastjson.JSONObject;
import com.wkclz.core.base.Result;
import com.wkclz.core.base.Sys;
import com.wkclz.core.pojo.enums.EnvType;
import com.wkclz.core.util.RegularUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * Description:
 * Created: wangkaicun @ 2019-02-13 20:55:11
 */
@Component
public class OrgDomainHelper extends BaseHelper {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired(required = false)
    private JedisHelper jedisHelper;

    private static final String NAME_SPACE = "_ORG_DOMAINS";

    /**
     * redis 的缓存主动更新，java 的缓存被动更新
     */
    private static Long JAVA_LAST_ACTIVE_TIME = null;
    private static Map<String, Object> ORG_DOMAINS = null;

    /**
     * 初始化 ORG_DOMAINS
     * @param orgDomains
     */
    public void setOrgDomains(Map<String, Object> orgDomains){
        if (orgDomains == null || orgDomains.size() == 0){
            throw new RuntimeException("orgDomains can not be null or empty!");
        }
        jedisHelper.STRINGS.set(Sys.APPLICATION_GROUP + NAME_SPACE, JSONObject.toJSONString(orgDomains));
        ORG_DOMAINS = orgDomains;
    }

    private synchronized Map<String, Object> getOrgDomains(){
        Integer liveTime = getJavaCacheLiveTime();
        // java 缓存
        if (JAVA_LAST_ACTIVE_TIME != null && ORG_DOMAINS != null ){
            Long ttl = Long.valueOf(System.currentTimeMillis() - JAVA_LAST_ACTIVE_TIME);
            if (ttl.compareTo(Long.valueOf(liveTime) * 1000) == -1){
                return ORG_DOMAINS;
            }
        }
        JAVA_LAST_ACTIVE_TIME = System.currentTimeMillis();

        // redis 拉取
        String orgDomainsStr = jedisHelper.STRINGS.get(Sys.APPLICATION_GROUP + NAME_SPACE);
        Map orgDomains = JSONObject.parseObject(orgDomainsStr, Map.class);
        ORG_DOMAINS = orgDomains;
        return ORG_DOMAINS;
    }

    public Long getOrgId(HttpServletRequest req){
        if (req == null){
            return null;
        }
        String origin = getOrigin(req);

        if (StringUtils.isBlank(origin)){
            throw new RuntimeException("Can not get Origin from the request！");
        }

        Map<String, Object> orgDomains = getOrgDomains();
        Object orgIdObj = orgDomains.get(origin);
        if (orgIdObj == null){
            return null;
        }
        Long orgId = Long.valueOf(orgIdObj.toString());
        return orgId;

    }

    public boolean checkOrgDomains(HttpServletRequest req, HttpServletResponse rep) {
        Map<String, Object> orgDomains = getOrgDomains();
        if (orgDomains == null || orgDomains.size() == 0) {
            throw new RuntimeException("orgDomains must be init after system start up!");
        }

        // 此处只检查 header
        String url = req.getHeader("Origin");
        // postman 请求，不会有 Origin
        if (url==null) {
            return true;
        }
        url = getDomainFronUrl(url);

        // 特殊情况，不检查跨域
        if (RegularUtil.isIp(url) && Sys.CURRENT_ENV != EnvType.PROD ){
            return true;
        }
        if ("localhost".equals(url) && Sys.CURRENT_ENV != EnvType.PROD ){
            return true;
        }

        if (orgDomains.get(url) != null) {
            return true;
        }

        logger.info("origin url can not be cors, url : {}, ip: {}", url, IpHelper.getIpAddr(req));
        Result result = new Result();
        result.setMoreError(Result.ORIGIN_CORS);
        return Result.responseError(rep,result);
    }

    public static String getOrigin(HttpServletRequest req){
        String origin = req.getHeader("Origin");
        if (origin == null){
            origin = req.getParameter("Origin");
        }
        // 非前后分离的情况，为当前域名
        if (origin == null){
            origin = req.getRequestURL().toString();
            origin = getDomainFronUrl(origin);
        }
        return origin;
    }

}

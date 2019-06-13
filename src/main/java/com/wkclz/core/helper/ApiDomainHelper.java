package com.wkclz.core.helper;

import com.alibaba.fastjson.JSONArray;
import com.wkclz.core.base.Result;
import com.wkclz.core.base.Sys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * Description:
 * Created: wangkaicun @ 2019-02-13 20:55:11
 */
@Component
public class ApiDomainHelper extends BaseHelper {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired(required = false)
    private JedisHelper jedisHelper;

    private static final String NAME_SPACE = "_API_DOMAINS";

    /**
     * redis 的缓存主动更新，java 的缓存被动更新
     */
    private static Long JAVA_LAST_ACTIVE_TIME = null;
    private static List<String> API_DOMAINS = null;

    /**
     * 初始化 API_DOMAINS
     * @param apiDomains
     */
    public void setApiDomains(List<String> apiDomains){
        if (apiDomains == null || apiDomains.size() == 0){
            throw new RuntimeException("apiDomains can not be null or empty!");
        }
        jedisHelper.STRINGS.set(Sys.APPLICATION_GROUP + NAME_SPACE, JSONArray.toJSONString(apiDomains));
        API_DOMAINS = apiDomains;
    }

    private synchronized List<String> getApiDomains(){
        Integer liveTime = getJavaCacheLiveTime();
        // java 缓存
        if (JAVA_LAST_ACTIVE_TIME != null && API_DOMAINS != null ){
            Long ttl = Long.valueOf(System.currentTimeMillis() - JAVA_LAST_ACTIVE_TIME);
            if (ttl.compareTo(Long.valueOf(liveTime) * 1000) == -1){
                return API_DOMAINS;
            }
        }
        JAVA_LAST_ACTIVE_TIME = System.currentTimeMillis();

        // redis 拉取
        String apiDomainsStr = jedisHelper.STRINGS.get(Sys.APPLICATION_GROUP + NAME_SPACE);
        List<String> apiDomains = JSONArray.parseArray(apiDomainsStr, String.class);
        API_DOMAINS = apiDomains;
        return API_DOMAINS;
    }

    public boolean checkApiDomains(HttpServletRequest req, HttpServletResponse rep) {
        List<String> apiDomains = getApiDomains();
        if (apiDomains == null || apiDomains.size() == 0) {
            throw new RuntimeException("apiDomains must be init after system start up!");
        }

        String url = req.getRequestURL().toString();
        url = getDomainFronUrl(url);

        if (apiDomains.contains(url)) {
            return true;
        }

        logger.info("api url can not be cors, url : {}, ip: {}", url, IpHelper.getIpAddr(req));
        Result result = new Result();
        result.setMoreError(Result.API_CORS);
        return Result.responseError(rep,result);
    }

}

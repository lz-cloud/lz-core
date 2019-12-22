package com.wkclz.core.helper;

import com.alibaba.fastjson.JSONArray;
import com.wkclz.core.base.Result;
import com.wkclz.core.base.Sys;
import com.wkclz.core.exception.BizException;
import com.wkclz.core.pojo.enums.ResultStatus;
import com.wkclz.core.util.UrlUtil;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
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

    private final static Logger logger = LoggerFactory.getLogger(ApiDomainHelper.class);
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    private static final String NAME_SPACE = "_API_DOMAINS";

    /**
     * redis 的缓存主动更新，java 的缓存被动更新
     */
    private static Long JAVA_LAST_ACTIVE_TIME = null;
    private static List<String> API_DOMAINS = null;

    /**
     * 初始化 API_DOMAINS
     *
     * @param apiDomains
     */
    public synchronized void setApiDomains(List<String> apiDomains) {
        if (CollectionUtils.isEmpty(apiDomains)) {
            throw new BizException("apiDomains can not be null or empty!");
        }
        stringRedisTemplate.opsForValue().set(Sys.APPLICATION_GROUP + NAME_SPACE, JSONArray.toJSONString(apiDomains));
        API_DOMAINS = apiDomains;
    }

    private synchronized List<String> getApiDomains() {
        Integer liveTime = getJavaCacheLiveTime();
        // java 缓存
        if (JAVA_LAST_ACTIVE_TIME != null && API_DOMAINS != null) {
            Long ttl = Long.valueOf(System.currentTimeMillis() - JAVA_LAST_ACTIVE_TIME);
            if (ttl.compareTo(Long.valueOf(liveTime) * 1000) < 0) {
                return API_DOMAINS;
            }
        }
        JAVA_LAST_ACTIVE_TIME = System.currentTimeMillis();

        // redis 拉取
        String apiDomainsStr = stringRedisTemplate.opsForValue().get(Sys.APPLICATION_GROUP + NAME_SPACE);
        List<String> apiDomains = JSONArray.parseArray(apiDomainsStr, String.class);
        API_DOMAINS = apiDomains;
        return API_DOMAINS;
    }

    public Result checkApiDomains(HttpServletRequest req, HttpServletResponse rep) {
        List<String> apiDomains = getApiDomains();
        if (CollectionUtils.isEmpty(apiDomains)) {
            throw new BizException("apiDomains must be init after system start up!");
        }

        String url = req.getRequestURL().toString();
        url = UrlUtil.getDomainFronUrl(url);

        if (apiDomains.contains(url)) {
            return null;
        }

        logger.error("api url can not be cors, url : {}, ip: {}", url, IpHelper.getOriginIp(req));
        Result result = new Result();
        result.setMoreError(ResultStatus.API_CORS);
        return result;
    }

}

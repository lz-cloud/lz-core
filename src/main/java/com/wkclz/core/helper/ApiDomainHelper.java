package com.wkclz.core.helper;

import com.alibaba.fastjson.JSONArray;
import com.wkclz.core.base.Result;
import com.wkclz.core.base.Sys;
import com.wkclz.core.exception.BizException;
import com.wkclz.core.helper.redis.bean.RedisMsgBody;
import com.wkclz.core.helper.redis.topic.RedisTopicConfig;
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
public class ApiDomainHelper extends BaseHelper {

    private final static Logger logger = LoggerFactory.getLogger(ApiDomainHelper.class);


    /**
     * redis 的缓存主动更新，java 的缓存被动更新
     */
    private static List<String> API_DOMAINS = null;

    /**
     * 初始化 API_DOMAINS
     */
    public static boolean reflash() {
        return reflash(API_DOMAINS);
    }
    public static boolean reflash(List<String> apiDomains) {
        if (CollectionUtils.isEmpty(apiDomains)) {
            throw new BizException("apiDomains can not be null or empty!");
        }

        RedisMsgBody body = new RedisMsgBody();
        body.setTag(ApiDomainHelper.class.getName());
        body.setMsg(apiDomains);

        String msg = JSONArray.toJSONString(body);
        StringRedisTemplate stringRedisTemplate = Sys.getBean(StringRedisTemplate.class);
        stringRedisTemplate.convertAndSend(Sys.getBean(RedisTopicConfig.class).getCacheTopic(), msg);
        return true;
    }

    /**
     * 初始化 apiDomains 【仅给队列调用，不允许直接调用】
     */
    public static boolean setLocal(Object msg) {
        if (msg == null) {
            throw new BizException("apiDomains can not be null or empty!");
        }
        List<String> apiDomains = JSONArray.parseArray(msg.toString(), String.class);
        return setLocal(apiDomains);
    }
    public static boolean setLocal(List<String> apiDomains) {
        if (CollectionUtils.isEmpty(apiDomains)) {
            throw new BizException("apiDomains can not be null or empty!");
        }
        API_DOMAINS = apiDomains;
        return true;
    }

    public static List<String> getLocal() {
        return API_DOMAINS;
    }

    public static Result checkApiDomains(HttpServletRequest req, HttpServletResponse rep) {
        List<String> apiDomains = getLocal();
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

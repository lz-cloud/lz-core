package com.wkclz.core.helper;

import com.alibaba.fastjson.JSONObject;
import com.wkclz.core.base.Sys;
import com.wkclz.core.exception.BizException;
import com.wkclz.core.helper.redis.bean.RedisMsgBody;
import com.wkclz.core.helper.redis.topic.RedisTopicConfig;
import com.wkclz.core.util.RegularUtil;
import com.wkclz.core.util.UrlUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.MDC;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.util.CollectionUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * Description: 获取我租户信息。不公开使用。统一 AuthHelper 公开使用
 * @date : wangkaicun @ 2019-02-13 20:55:11
 */
public class TenantDomainHelper extends BaseHelper {

    private static Map<String, Object> TENANT_DOMAINS = null;

    /**
     * 初始化 TENANT_DOMAINS
     */
    public static boolean reflash() {
        return reflash(TENANT_DOMAINS);
    }
    public static boolean reflash(Map<String, Object> tenantDomains) {
        if (tenantDomains == null || tenantDomains.size() == 0) {
            throw new BizException("tenantDomains can not be null or empty!");
        }

        RedisMsgBody body = new RedisMsgBody();
        body.setTag(TenantDomainHelper.class.getName());
        body.setMsg(tenantDomains);

        String msg = JSONObject.toJSONString(body);
        StringRedisTemplate stringRedisTemplate = Sys.getBean(StringRedisTemplate.class);
        stringRedisTemplate.convertAndSend(RedisTopicConfig.CACHE_CONFIG_TOPIC, msg);
        return true;

    }
    public static boolean setLocal(Object msg) {
        if (msg == null) {
            throw BizException.error("tenantDomains can not be null or empty!");
        }
        Map<String, Object> tenantDomains = JSONObject.parseObject(msg.toString(), Map.class);
        return setLocal(tenantDomains);
    }
    public static boolean setLocal(Map<String, Object> tenantDomains) {
        if (CollectionUtils.isEmpty(tenantDomains)) {
            throw BizException.error("tenantDomains can not be null or empty!");
        }
        TENANT_DOMAINS = tenantDomains;
        return true;
    }

    public static Map<String, Object> getLocal() {
        return TENANT_DOMAINS;
    }


    /**
     * 获取 tenantId。成功获取之后，MDC 一定会有值
     * @return
     */
    public static Long getTenantId() {
        String tenantIdStr = MDC.get(LogTraceHelper.TENANT_ID);
        if (tenantIdStr != null){
            return Long.valueOf(tenantIdStr);
        }

        HttpServletRequest req = RequestHelper.getRequest();
        if (req == null) {
            MDC.put(LogTraceHelper.TENANT_ID, "-1");
            return -1L;
        }

        tenantIdStr = req.getHeader(LogTraceHelper.TENANT_ID);
        if (tenantIdStr != null){
            MDC.put(LogTraceHelper.TENANT_ID, tenantIdStr);
            return Long.valueOf(tenantIdStr);
        }

        String domain = UrlUtil.getDomain(req);

        if (StringUtils.isBlank(domain)) {
            throw BizException.error("can not get domain from the request: {}", RequestHelper.getRequestUrl());
        }

        Map<String, Object> tenantDomains = getLocal();
        if (tenantDomains == null || tenantDomains.size() == 0) {
            throw BizException.error("tenantDomains must be init after system start up: {}", RequestHelper.getRequestUrl());
        }

        Object tenantId = tenantDomains.get(domain);
        if (tenantId != null) {
            MDC.put(LogTraceHelper.TENANT_ID, tenantId.toString() + "");
            return Long.valueOf(tenantId.toString());
        }

        if (RegularUtil.isIp(domain)){
            tenantId = -1L;
            MDC.put(LogTraceHelper.TENANT_ID, tenantId + "");
            return -1L;
        }

        throw BizException.error("domain {} is undefined, can not get tenantId!", domain);
    }

}

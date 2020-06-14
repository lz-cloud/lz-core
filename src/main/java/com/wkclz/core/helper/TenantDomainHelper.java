package com.wkclz.core.helper;

import com.alibaba.fastjson.JSONObject;
import com.wkclz.core.base.Sys;
import com.wkclz.core.exception.BizException;
import com.wkclz.core.util.RegularUtil;
import com.wkclz.core.util.UrlUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * Description: 获取我租户信息。不公开使用。统一 AuthHelper 公开使用
 * @date : wangkaicun @ 2019-02-13 20:55:11
 */
@Component
public class TenantDomainHelper extends BaseHelper {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    private static final String NAME_SPACE = "_TENANT_DOMAINS";

    /**
     * redis 的缓存主动更新，java 的缓存被动更新
     */
    private static Long JAVA_LAST_ACTIVE_TIME = null;
    private static Map<String, Long> tenant_DOMAINS = null;

    /**
     * 初始化 TENANT_DOMAINS
     *
     * @param tenantDomains
     */
    public synchronized void setTenantDomains(Map<String, Long> tenantDomains) {
        if (tenantDomains == null || tenantDomains.size() == 0) {
            throw new BizException("tenantDomains can not be null or empty!");
        }
        stringRedisTemplate.opsForValue().set(Sys.APPLICATION_GROUP + NAME_SPACE, JSONObject.toJSONString(tenantDomains));
        tenant_DOMAINS = tenantDomains;
    }

    private synchronized Map<String, Long> getTenantDomains() {
        Integer liveTime = getJavaCacheLiveTime();
        // java 缓存
        if (JAVA_LAST_ACTIVE_TIME != null && tenant_DOMAINS != null) {
            Long ttl = Long.valueOf(System.currentTimeMillis() - JAVA_LAST_ACTIVE_TIME);
            if (ttl.compareTo(Long.valueOf(liveTime) * 1000) < 0) {
                return tenant_DOMAINS;
            }
        }
        JAVA_LAST_ACTIVE_TIME = System.currentTimeMillis();

        // redis 拉取
        String tenantDomainsStr = stringRedisTemplate.opsForValue().get(Sys.APPLICATION_GROUP + NAME_SPACE);
        Map tenantDomains = JSONObject.parseObject(tenantDomainsStr, Map.class);
        tenant_DOMAINS = tenantDomains;
        return tenant_DOMAINS;
    }



    /**
     * 获取 tenantId。成功获取之后，MDC 一定会有值
     * @return
     */
    public Long getTenantId() {
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

        Map<String, Long> tenantDomains = getTenantDomains();
        if (tenantDomains == null || tenantDomains.size() == 0) {
            throw BizException.error("tenantDomains must be init after system start up: {}", RequestHelper.getRequestUrl());
        }

        Long tenantId = tenantDomains.get(domain);
        if (tenantId != null) {
            MDC.put(LogTraceHelper.TENANT_ID, tenantId + "");
            return tenantId;
        }

        if (RegularUtil.isIp(domain)){
            tenantId = -1L;
            MDC.put(LogTraceHelper.TENANT_ID, tenantId + "");
            return -1L;
        }

        throw BizException.error("domain {} is undefined, can not get tenantId!", domain);
    }

}

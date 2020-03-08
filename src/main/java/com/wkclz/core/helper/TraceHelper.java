package com.wkclz.core.helper;

import com.wkclz.core.base.Sys;
import com.wkclz.core.pojo.dto.User;
import com.wkclz.core.pojo.entity.TraceInfo;
import com.wkclz.core.pojo.enums.EnvType;
import com.wkclz.core.util.UniqueCodeUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.stereotype.Component;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

/**
 * 仅在应用入口处可用此功能
 */
@Component
public class TraceHelper {

    public final static String SERVICE_ID = "serviceId";
    public final static String INSTANCE_ID = "instanceId";
    public final static String UPSTREAM_SERVICE_ID = "upstreamServiceId";
    public final static String UPSTREAM_INSTANCE_ID = "upstreamInstanceId";

    public final static Map<String, TraceInfo> SERVICE_TRACES = new HashMap<>();

    @Autowired
    private AuthHelper authHelper;
    @Autowired(required = false)
    private ServiceInstance serviceInstance;

    public TraceInfo checkTraceInfo(HttpServletRequest req, HttpServletResponse rep){

        // 如果已经存在，不再请求第二次，直接返回结果
        String name = MDC.get("serviceId");
        if (StringUtils.isNotBlank(name)){
            TraceInfo traceInfo = new TraceInfo();
            traceInfo.setTraceId(MDC.get("traceId"));
            traceInfo.setOriginIp(MDC.get("originIp"));
            traceInfo.setUpstreamIp(MDC.get("upstreamIp"));

            String spanId = MDC.get("spanId");
            if (StringUtils.isNotBlank(spanId)){
                Integer integer = Integer.valueOf(spanId);
                traceInfo.setSpanId(integer);
            }

            String tenantId = MDC.get("tenantId");
            if (StringUtils.isNotBlank(tenantId)){
                Long l = Long.valueOf(tenantId);
                traceInfo.setTenantId(l);
            }

            String authId = MDC.get("authId");
            if (StringUtils.isNotBlank(authId)){
                Long l = Long.valueOf(authId);
                traceInfo.setAuthId(l);
            }

            String userId = MDC.get("userId");
            if (StringUtils.isNotBlank(userId)){
                Long l = Long.valueOf(userId);
                traceInfo.setUserId(l);
            }
            return traceInfo;
        }

        TraceInfo traceInfo = new TraceInfo();
        traceInfo.setAuthId(-1L);
        traceInfo.setUserId(-1L);

        Long tenantId = authHelper.getTenantId();
        User user = authHelper.checkUserSession(req);
        traceInfo.setTenantId(tenantId);
        if (user != null){
            traceInfo.setAuthId(user.getAuthId());
            traceInfo.setUserId(user.getUserId());
        }


        // group
        String group = MDC.get("group");
        if (group == null){ group = req.getHeader("group"); }
        if (group == null) { group = Sys.APPLICATION_GROUP; }
        if (TraceInfo.getApplicationGroup() == null || !group.equals(TraceInfo.getApplicationGroup())){
            TraceInfo.setApplicationGroup(group);
        }

        // serviceId
        if (TraceInfo.getServiceId() == null && serviceInstance != null){
            TraceInfo.setServiceId(serviceInstance.getServiceId());
        }

        // instanceId
        if (TraceInfo.getInstanceId() == null && serviceInstance != null){
            String instanceId = serviceInstance.getInstanceId();
            TraceInfo.setInstanceId(instanceId);
        }

        // serverIp
        String serverIp = MDC.get("serverIp");
        if (serverIp == null){ serverIp = req.getHeader("serverIp"); }
        if (serverIp == null) { serverIp = IpHelper.getServerIp(); }
        if (TraceInfo.getServerIp() == null || !serverIp.equals(TraceInfo.getServerIp())){
            TraceInfo.setServerIp(serverIp);
        }

        // envType
        String envType = MDC.get("envType");
        if (envType == null){ envType = req.getHeader("envType"); }
        if (envType == null) { envType = Sys.CURRENT_ENV.name(); }
        if (TraceInfo.getEnvType() == null || !envType.equals(TraceInfo.getEnvType().name())){
            TraceInfo.setEnvType(EnvType.valueOf(envType));
        }

        // traceId
        String traceId = MDC.get("traceId");
        if (traceId == null){ traceId = req.getHeader("traceId"); }
        if (traceId == null) { traceId = UniqueCodeUtil.getJavaUuid(); }
        traceInfo.setTraceId(traceId);


        // spanId
        String spanId = MDC.get("spanId");
        if (spanId == null) { spanId = req.getHeader("spanId"); }
        if (spanId == null) { spanId = "0"; }
        Integer newSpanId = Integer.valueOf(spanId) + 1;
        traceInfo.setSpanId(newSpanId);

        traceInfo.setOriginIp(IpHelper.getOriginIp(req));
        traceInfo.setUpstreamIp(IpHelper.getUpstreamIp(req));

        // 检查 cookie, header
        Enumeration<String> headerNames = req.getHeaderNames();
        if (headerNames != null){
            while (headerNames.hasMoreElements()){
                String s = headerNames.nextElement();
                if (TraceHelper.SERVICE_ID.equalsIgnoreCase(s)){
                    continue;
                }
                if (TraceHelper.INSTANCE_ID.equalsIgnoreCase(s)){
                    continue;
                }
                if (TraceHelper.UPSTREAM_SERVICE_ID.equalsIgnoreCase(s)){
                    continue;
                }
                if (TraceHelper.UPSTREAM_INSTANCE_ID.equalsIgnoreCase(s)){
                    continue;
                }
                MDC.put(s, req.getHeader(s));
            }
        }
        Cookie[] cookies = req.getCookies();
        if (cookies != null){
            for (Cookie cookie : cookies) {
                MDC.put(cookie.getName(), cookie.getValue());
            }
        }



        // zuul, gateway, feign 请求开始时，附加 upstream 信息
        // HandlerInterceptor 请接收请求，获取 upstream 信息【当前位置】
        getTraceInfo(req, traceInfo);


        MDC.put("applicationGroup", TraceInfo.getApplicationGroup());
        MDC.put("serverIp", TraceInfo.getServerIp());
        MDC.put("envType", TraceInfo.getEnvType().name());

        MDC.put("traceId", traceId);
        MDC.put("spanId", spanId);
        MDC.put("upstreamIp", traceInfo.getUpstreamIp());
        MDC.put("originIp", traceInfo.getOriginIp());

        MDC.put("serviceId", TraceInfo.getServiceId());
        MDC.put("instanceId", TraceInfo.getInstanceId());

        return traceInfo;
    }


    /**
     * 尝试获取 upstream 信息
     */
    public static void getTraceInfo(HttpServletRequest req, TraceInfo traceInfo){

        String upstreamServiceId = req.getHeader(TraceHelper.UPSTREAM_SERVICE_ID);
        String upstreamInstanceId = req.getHeader(TraceHelper.UPSTREAM_INSTANCE_ID);

        if (upstreamServiceId != null && upstreamInstanceId != null){
            TraceInfo traceInfoHistory = SERVICE_TRACES.get(upstreamInstanceId);
            if (traceInfoHistory == null){
                traceInfoHistory = new TraceInfo();
                traceInfoHistory.setUpstreamServiceId(upstreamServiceId);
                traceInfoHistory.setUpstreamInstanceId(upstreamInstanceId);
                SERVICE_TRACES.put(upstreamInstanceId, traceInfoHistory);
            }
            traceInfoHistory.setUpstreamRequestTime(System.currentTimeMillis());
        }
    }

}

package com.wkclz.core.helper;

import com.wkclz.core.base.Sys;
import com.wkclz.core.pojo.dto.User;
import com.wkclz.core.pojo.entity.TraceInfo;
import com.wkclz.core.pojo.enums.EnvType;
import com.wkclz.core.util.UniqueCodeUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 仅在应用入口处可用此功能
 */
@Component
public class LogTraceHelper {


    // 日志信息
    public final static String SERVER_IP = "server-ip";
    public final static String ENV_TYPE = "env-type";

    // 跟踪信息
    public final static String TRACE_ID = "trace-id";
    public final static String SPAN_ID = "span-id";
    public final static String UPSTREAM_IP = "upstream-ip";
    public final static String ORIGIN_IP = "origin-ip";

    // 用户信息
    public final static String TENANT_ID = "tenant-id";
    public final static String AUTH_ID = "auth-id";
    public final static String USER_ID = "user-id";

    @Autowired
    private AuthHelper authHelper;

    public TraceInfo checkTraceInfo(HttpServletRequest req, HttpServletResponse rep){

        // 如果已经存在，不再请求第二次，直接返回结果
        String traceId = MDC.get(TRACE_ID);
        if (StringUtils.isNotBlank(traceId)){
            TraceInfo traceInfo = new TraceInfo();
            traceInfo.setOriginIp(MDC.get(ORIGIN_IP));
            traceInfo.setUpstreamIp(MDC.get(UPSTREAM_IP));
            traceInfo.setTraceId(MDC.get(TRACE_ID));
            String spanId = MDC.get(SPAN_ID);
            if (StringUtils.isNotBlank(spanId)){
                Integer integer = Integer.valueOf(spanId);
                traceInfo.setSpanId(integer);
            }

            String tenantId = MDC.get(TENANT_ID);
            if (StringUtils.isNotBlank(tenantId)){
                Long l = Long.valueOf(tenantId);
                traceInfo.setTenantId(l);
            }

            String authId = MDC.get(AUTH_ID);
            if (StringUtils.isNotBlank(authId)){
                Long l = Long.valueOf(authId);
                traceInfo.setAuthId(l);
            }

            String userId = MDC.get(USER_ID);
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

        // serverIp
        String serverIp = MDC.get(SERVER_IP);
        if (serverIp == null){ serverIp = req.getHeader(SERVER_IP); }
        if (serverIp == null) { serverIp = IpHelper.getServerIp(); }
        if (TraceInfo.getServerIp() == null || !serverIp.equals(TraceInfo.getServerIp())){
            TraceInfo.setServerIp(serverIp);
        }

        // envType
        String envType = MDC.get(ENV_TYPE);
        if (envType == null){ envType = req.getHeader(ENV_TYPE); }
        if (envType == null) { envType = Sys.CURRENT_ENV.name(); }
        if (TraceInfo.getEnvType() == null || !envType.equals(TraceInfo.getEnvType().name())){
            TraceInfo.setEnvType(EnvType.valueOf(envType));
        }

        // 为储藏室，直接生成
        traceInfo.setTraceId(traceId = UniqueCodeUtil.getJavaUuid());


        // spanId
        String spanId = MDC.get(SPAN_ID);
        if (spanId == null) { spanId = req.getHeader(SPAN_ID); }
        if (spanId == null) { spanId = "0"; }
        Integer newSpanId = Integer.parseInt(spanId) + 1;
        traceInfo.setSpanId(newSpanId);

        traceInfo.setOriginIp(IpHelper.getOriginIp(req));
        traceInfo.setUpstreamIp(IpHelper.getUpstreamIp(req));

        /* 检查 cookie, header 【太多内容了有风险】
        Enumeration<String> headerNames = req.getHeaderNames();
        if (headerNames != null){
            while (headerNames.hasMoreElements()){
                String s = headerNames.nextElement();
                MDC.put(s, req.getHeader(s));
            }
        }
        Cookie[] cookies = req.getCookies();
        if (cookies != null){
            for (Cookie cookie : cookies) {
                MDC.put(cookie.getName(), cookie.getValue());
            }
        }
        */

        // 日志信息
        MDC.put(SERVER_IP, TraceInfo.getServerIp());
        MDC.put(ENV_TYPE, TraceInfo.getEnvType().name());

        // 跟踪信息
        MDC.put(TRACE_ID, traceId);
        MDC.put(SPAN_ID, spanId);
        MDC.put(UPSTREAM_IP, traceInfo.getUpstreamIp());
        MDC.put(ORIGIN_IP, traceInfo.getOriginIp());

        // 用户信息
        MDC.put(TENANT_ID, traceInfo.getTenantId() + "");
        MDC.put(AUTH_ID, traceInfo.getAuthId() + "");
        MDC.put(USER_ID, traceInfo.getUserId() + "");

        return traceInfo;
    }

}

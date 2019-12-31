package com.wkclz.core.helper;

import com.wkclz.core.base.Sys;
import com.wkclz.core.pojo.dto.User;
import com.wkclz.core.pojo.entity.TraceInfo;
import com.wkclz.core.pojo.enums.EnvType;
import com.wkclz.core.util.UniqueCodeUtil;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Enumeration;

/**
 * 仅在应用入口处可用此功能
 */
@Component
public class TraceHelper {

    @Autowired
    private AuthHelper authHelper;

    public TraceInfo checkTraceInfo(HttpServletRequest req, HttpServletResponse rep){


        TraceInfo traceInfo = new TraceInfo();
        traceInfo.setAuthId(-1L);
        traceInfo.setUserId(-1L);

        Long tenantId = authHelper.getTenantId(req);
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

        // applicationName
        String applicationName = MDC.get("applicationName");
        if (applicationName == null){ applicationName = req.getHeader("applicationName"); }
        if (applicationName == null) { applicationName = Sys.APPLICATION_NAME; }
        if (TraceInfo.getApplicationName() == null || !applicationName.equals(TraceInfo.getApplicationName())){
            TraceInfo.setApplicationName(applicationName);
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
        traceInfo.setRouterIp(IpHelper.getRouterIp(req));

        // 检查 cookie, header
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


        MDC.put("applicationGroup", TraceInfo.getApplicationGroup());
        MDC.put("applicationName", TraceInfo.getApplicationName());
        MDC.put("serverIp", TraceInfo.getServerIp());
        MDC.put("envType", TraceInfo.getEnvType().name());

        MDC.put("traceId", traceId);
        MDC.put("spanId", spanId);
        MDC.put("originIp", traceInfo.getOriginIp());
        MDC.put("routerIp", traceInfo.getRouterIp());

        return traceInfo;
    }
}

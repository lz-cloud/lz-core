package com.wkclz.core.pojo.entity;


import com.wkclz.core.pojo.enums.EnvType;

public class TraceInfo {

    /**
     * 应用组
     */
    private static String applicationGroup = null;
    /**
     * 应用名称
     */
    private static String applicationName = null;
    /**
     * 所在服务IP
     */
    private static String serverIp = null;
    /**
     * 环境
     */
    private static EnvType envType = null;


    /**
     * 跟踪ID
     */
    private String traceId;
    /**
     * 跟踪序列号
     */
    private Integer spanId;
    /**
     * 源IP
     */
    private String originIp;
    /**
     * 路由IP
     */
    private String routerIp;

    /**
     * 租户ID
     */
    private Long tenantId;
    /**
     * 认证ID
     */
    private Long authId;
    /**
     * 用户ID
     */
    private Long userId;


    public static String getApplicationGroup() {
        return applicationGroup;
    }

    public static void setApplicationGroup(String applicationGroup) {
        TraceInfo.applicationGroup = applicationGroup;
    }

    public static String getApplicationName() {
        return applicationName;
    }

    public static void setApplicationName(String applicationName) {
        TraceInfo.applicationName = applicationName;
    }

    public static String getServerIp() {
        return serverIp;
    }

    public static void setServerIp(String serverIp) {
        TraceInfo.serverIp = serverIp;
    }

    public static EnvType getEnvType() {
        return envType;
    }

    public static void setEnvType(EnvType envType) {
        TraceInfo.envType = envType;
    }

    public String getTraceId() {
        return traceId;
    }

    public void setTraceId(String traceId) {
        this.traceId = traceId;
    }

    public Integer getSpanId() {
        return spanId;
    }

    public void setSpanId(Integer spanId) {
        this.spanId = spanId;
    }

    public String getOriginIp() {
        return originIp;
    }

    public void setOriginIp(String originIp) {
        this.originIp = originIp;
    }

    public String getRouterIp() {
        return routerIp;
    }

    public void setRouterIp(String routerIp) {
        this.routerIp = routerIp;
    }

    public Long getTenantId() {
        return tenantId;
    }

    public void setTenantId(Long tenantId) {
        this.tenantId = tenantId;
    }

    public Long getAuthId() {
        return authId;
    }

    public void setAuthId(Long authId) {
        this.authId = authId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}

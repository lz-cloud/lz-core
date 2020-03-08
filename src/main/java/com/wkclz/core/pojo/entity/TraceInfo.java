package com.wkclz.core.pojo.entity;


import com.wkclz.core.pojo.enums.EnvType;


/**
 * 追踪信息
 */
public class TraceInfo {

    // 以下为服务调用关系信息

    /**
     * 来源应用名称
     */
    private String upstreamServiceId;
    /**
     * 来源应用名称
     */
    private String upstreamInstanceId;
    /**
     * 来源请求时间
     */
    private Long upstreamRequestTime;




    // 以下为跟踪信息

    /**
     * 路由IP
     */
    private String upstreamIp;


    /**
     * 应用组
     */
    private static String applicationGroup = null;
    /**
     * 应用名称
     */
    private static String serviceId = null;
    /**
     * 应用名称
     */
    private static String instanceId = null;
    /**
     * 所在服务IP
     */
    private static String serverIp = null;
    /**
     * 环境
     */
    private static EnvType envType = null;

    /**
     * 路由IP
     */
    private String originIp;

    /**
     * 跟踪ID
     */
    private String traceId;
    /**
     * 跟踪序列号
     */
    private Integer spanId;

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

    public static String getServiceId() {
        return serviceId;
    }

    public static void setServiceId(String serviceId) {
        TraceInfo.serviceId = serviceId;
    }

    public static String getInstanceId() {
        return instanceId;
    }

    public static void setInstanceId(String instanceId) {
        TraceInfo.instanceId = instanceId;
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

    public String getUpstreamIp() {
        return upstreamIp;
    }

    public void setUpstreamIp(String upstreamIp) {
        this.upstreamIp = upstreamIp;
    }

    public String getOriginIp() {
        return originIp;
    }

    public void setOriginIp(String originIp) {
        this.originIp = originIp;
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


    public String getUpstreamServiceId() {
        return upstreamServiceId;
    }

    public void setUpstreamServiceId(String upstreamServiceId) {
        this.upstreamServiceId = upstreamServiceId;
    }

    public String getUpstreamInstanceId() {
        return upstreamInstanceId;
    }

    public void setUpstreamInstanceId(String upstreamInstanceId) {
        this.upstreamInstanceId = upstreamInstanceId;
    }

    public Long getUpstreamRequestTime() {
        return upstreamRequestTime;
    }

    public void setUpstreamRequestTime(Long upstreamRequestTime) {
        this.upstreamRequestTime = upstreamRequestTime;
    }
}

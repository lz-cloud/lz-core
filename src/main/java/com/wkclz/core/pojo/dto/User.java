package com.wkclz.core.pojo.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;
import java.util.Map;

/**
 * Description:
 * Created: wangkaicun @ 2017-10-20 上午2:29
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class User<CmsUser, CmsUserAuth> {

    /**
     * token
     */
    private String token;

    /**
     * 当前 ip
     */
    private String ip;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 认证id
     */
    private Long authId;

    /**
     * 父id 【子账号，邀请账号】
     */
    private Long pid;

    /**
     * 用户名
     */
    private String username;

    /**
     * 登录次数
     */
    private Integer loginTimes;

    /**
     * 积分
     */
    private Integer point;

    /**
     * 用户
     */
    private CmsUser user;

    /**
     * 账号
     */
    private CmsUserAuth userAuth;

    /**
     * 用户扩展属性
     */
    private Map<String, Object> userProperties;

    /**
     * 管理id
     */
    private List<Long> adminIds;

    /**
     * 角色列表
     */
    private List<String> roles;


    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getAuthId() {
        return authId;
    }

    public void setAuthId(Long authId) {
        this.authId = authId;
    }

    public Long getPid() {
        return pid;
    }

    public void setPid(Long pid) {
        this.pid = pid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Integer getLoginTimes() {
        return loginTimes;
    }

    public void setLoginTimes(Integer loginTimes) {
        this.loginTimes = loginTimes;
    }

    public Integer getPoint() {
        return point;
    }

    public void setPoint(Integer point) {
        this.point = point;
    }

    public CmsUser getUser() {
        return user;
    }

    public void setUser(CmsUser user) {
        this.user = user;
    }

    public CmsUserAuth getUserAuth() {
        return userAuth;
    }

    public void setUserAuth(CmsUserAuth userAuth) {
        this.userAuth = userAuth;
    }

    public Map<String, Object> getUserProperties() {
        return userProperties;
    }

    public void setUserProperties(Map<String, Object> userProperties) {
        this.userProperties = userProperties;
    }

    public List<Long> getAdminIds() {
        return adminIds;
    }

    public void setAdminIds(List<Long> adminIds) {
        this.adminIds = adminIds;
    }

    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }
}

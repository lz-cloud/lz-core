package com.wkclz.core.pojo.entity;

import lombok.Data;

/**
 * Description: Create by Shrimp Generator
 * @author: wangkaicun @ current time
 */

@Data
public class AccessLog {

    /**
     * id
     */
    private Integer id;

    /**
     * 浏览器基本信息
     */
    private String userAgent;

    /**
     * 浏览器类型
     */
    private String browserType;

    /**
     * 浏览器名称
     */
    private String browserName;

    /**
     * 设备类型
     */
    private String deviceType;

    /**
     * 设备名称
     */
    private String deviceName;

    /**
     * 服务器名称
     */
    private String osName;

    /**
     * 服务器版本
     */
    private String osVersion;

    /**
     * 服务器架构
     */
    private String osArch;

    /**
     * 请求协议
     */
    private String httpProtocol;

    /**
     * 请求编码
     */
    private String characterEncoding;

    /**
     * Accept
     */
    private String accept;

    /**
     * Accept-语言
     */
    private String acceptLanguage;

    /**
     * Accept-编码
     */
    private String acceptEncoding;

    /**
     * Connection
     */
    private String connection;

    /**
     * Cookie
     */
    private String cookie;

    /**
     * Origin
     */
    private String origin;

    /**
     * 引用页
     */
    private String referer;

    /**
     * 请求 URL
     */
    private String requestUrl;

    /**
     * 请求 URI
     */
    private String requestUri;

    /**
     * 查询内容
     */
    private String queryString;

    /**
     * 客户端地址
     */
    private String remoteAddr;

    /**
     * 客户端端口
     */
    private Integer remotePort;

    /**
     * 地区
     */
    private String location;

    /**
     * ISP运营商
     */
    private String isp;

    /**
     * 服务器地址
     */
    private String localAddr;

    /**
     * 服务器名称
     */
    private String localName;

    /**
     * 请求方式
     */
    private String method;

    /**
     * 
     */
    private String serverName;

    /**
     * 用户token
     */
    private String token;

    /**
     * 认证
     */
    private Long authId;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 用户昵称
     */
    private String nickName;


}

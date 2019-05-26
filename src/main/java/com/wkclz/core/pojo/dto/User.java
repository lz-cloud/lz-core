package com.wkclz.core.pojo.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;
import java.util.Map;

/**
 * Description:
 * Created: wangkaicun @ 2017-10-20 上午2:29
 */
@EqualsAndHashCode(callSuper = false)
@Data
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

}

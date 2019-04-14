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

    private String token;
    private String ip;
    private Integer userId;
    private Integer authId;
    private String username;
    private Integer loginTimes;
    private Integer point;

    private CmsUser user;
    private CmsUserAuth userAuth;
    private Map<String, Object> userProperties;

    private List<Integer> adminIds;
    private List<String> roles;

}

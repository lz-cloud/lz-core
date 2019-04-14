package com.wkclz.core.pojo.enums;

import com.wkclz.core.base.annotation.Desc;

@Desc("系统环境")
public enum EnvType {


    DEV("开发环境"),
    SIT("集成测试环境"),
    UAT("验收测试环境"),
    PROD("生产环境");

    private String value;

    EnvType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}

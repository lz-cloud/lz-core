package com.wkclz.core.pojo.entity;


import java.util.List;

/**
 * 报表入参
 *
 * @author wangkc
 * @date 2018-12-22 17:22:23
 */
public class InParam {

    /**
     * 参数
     */
    private String field;

    /**
     * 参数类型
     */
    private String type;

    /**
     * 是否显示参数
     */
    private Boolean isDisplay;

    /**
     * 参数显示名称
     */
    private String displayName;

    /**
     * 是否必需
     */
    private Boolean require;

    /**
     * 下拉选项
     */
    private List<SelectOptions> selectOptions;


    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Boolean getDisplay() {
        return isDisplay;
    }

    public void setDisplay(Boolean display) {
        isDisplay = display;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public Boolean getRequire() {
        return require;
    }

    public void setRequire(Boolean require) {
        this.require = require;
    }

    public List<SelectOptions> getSelectOptions() {
        return selectOptions;
    }

    public void setSelectOptions(List<SelectOptions> selectOptions) {
        this.selectOptions = selectOptions;
    }
}


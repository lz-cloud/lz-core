package com.wkclz.core.pojo.entity;


/**
 * 报表出参
 *
 * @author wangkc
 * @date 2018-12-22 17:22:23
 */
public class OutParams {

    /**
     * 参数
     */
    private String field;

    /**
     * 参数显示名称
     */
    private String displayName;


    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }
}

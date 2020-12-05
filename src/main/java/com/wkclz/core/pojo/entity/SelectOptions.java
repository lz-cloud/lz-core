package com.wkclz.core.pojo.entity;

/**
 * 报表出参
 *
 * @author wangkc
 * @date 2018-12-23 11:51:46
 */
@Deprecated
public class SelectOptions {

    /**
     * 下拉选项 value
     */
    private String value;

    /**
     * 下拉选项含义 valueDesc
     */
    private String valueDesc;


    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getValueDesc() {
        return valueDesc;
    }

    public void setValueDesc(String valueDesc) {
        this.valueDesc = valueDesc;
    }
}

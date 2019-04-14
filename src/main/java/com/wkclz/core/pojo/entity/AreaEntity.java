package com.wkclz.core.pojo.entity;

import lombok.Data;

/**
 * Description: Create by Shrimp Generator
 *
 * @author: wangkaicun @ current time
 */
@Data
public class AreaEntity {

    /**
     * 父区划代码
     */
    private Long parentAreaCode;

    /**
     * 统计用区划代码
     */
    private Long areaCode;

    /**
     * 城乡分类代码
     */
    private Integer typeCode;

    /**
     * 名称
     */
    private String name;

    /**
     * 级别（1-5）
     */
    private Integer level;

    /**
     * 是否叶子（1是，0 不是）
     */
    private Integer isLeaf;

}

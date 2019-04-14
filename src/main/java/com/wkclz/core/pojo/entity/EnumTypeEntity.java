package com.wkclz.core.pojo.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 枚举类型实体
 */
@EqualsAndHashCode(callSuper = false)
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EnumTypeEntity {

    private Class clazz;
    private String enumType;
    private String enumTypeDesc;
}

package com.wkclz.core.pojo.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 枚举内容
 */
@EqualsAndHashCode(callSuper = false)
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EnumEntity {

    private String enumType;
    private String enumTypeDesc;
    private String enumKey;
    private String enumValue;
}

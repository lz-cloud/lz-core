package com.wkclz.core.base.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by wangkc on 2018/06/07.
 * 辅助java提取类详情
 */
@Documented
@Inherited
@Retention(RetentionPolicy.RUNTIME)
public @interface Debug {

    String value();

}

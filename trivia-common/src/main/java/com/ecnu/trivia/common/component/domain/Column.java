package com.ecnu.trivia.common.component.domain;

import org.apache.ibatis.type.JdbcType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by Jack Chen at 2014/6/24
 */

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Column {
    /** 数据库字段名，默认为属性字段名 大写转_形式 */
    String value() default "";

    /** 数据库类型信息 */
    JdbcType jdbcType();
}

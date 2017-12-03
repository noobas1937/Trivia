package com.ecnu.trivia.common.component.domain;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 用于对表进行映射
 * Created by Jack Chen at 2014/6/24
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Table {
    /** 表名 */
    String value();
    /** 表类型 */
    TableType type() default TableType.standard;

}

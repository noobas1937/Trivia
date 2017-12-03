package com.ecnu.trivia.common.component.domain;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 表示为数据库对应映射的主键列
 * Created by Jack Chen at 2014/6/24
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Id {
    /** 如果为空时是否由数据库自动生成 */
    boolean generated() default false;
}

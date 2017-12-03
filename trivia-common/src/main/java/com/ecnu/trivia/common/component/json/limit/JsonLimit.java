package com.ecnu.trivia.common.component.json.limit;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 用于提示针对json输出的限制信息
 * Created by Jack Chen at 12/12/2014
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface JsonLimit {
    /** 限制值的表达式 */
    String[] value();
}

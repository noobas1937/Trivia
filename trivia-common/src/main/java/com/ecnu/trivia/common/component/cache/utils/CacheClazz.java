package com.ecnu.trivia.common.component.cache.utils;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/** Created by Jack Chen at 15-7-29 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface CacheClazz {

    /** 所在的类表达式 */
    String clazz();
}

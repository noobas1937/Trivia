package com.ecnu.trivia.common.log;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 用于表示哪些方法不需要作日志记录
 * <p/>
 * Created by Jack Chen at 15-11-15
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface NonLogged {
}

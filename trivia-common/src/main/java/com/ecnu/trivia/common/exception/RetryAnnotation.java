package com.ecnu.trivia.common.exception;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 异常重试注解。
 *  只应用于方法调用失败后重试。
 * @author Jack Chen
 * @date 2015/7/8.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RetryAnnotation {

    /**
     * 失败后默认重试次数。
     * @return 默认0次。
     */
    int retryTimes() default 0;

    /**
     * 间隔多长时间后重试运行。
     * 单位ms
     * @return 重试间隔时间。
     */
    long sleep() default 1000;

    /**
     * 重试动作应用于哪些异常触发的情况下。
     * @return 异常类型集合。
     */
    Class[] exceptions() default {};
}

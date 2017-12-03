/** Created by Jack Chen at 2015/6/29 */
package com.ecnu.trivia.common.component.json.format;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 用于处理针对特定方法对于date类型的输出信息
 *
 * @author Jack Chen
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface JsonFormat {
    /** 要处理格式的属性名 */
    String value();

    /** 是否输出时间信息 */
    boolean time() default true;

    /** 日期的format信息 */
    String dateFormat() default "";

    /** 时间的format信息 */
    String timeFormat() default "";

    /** 处理格式的类型 */
    Class<?> type() default Object.class;
}

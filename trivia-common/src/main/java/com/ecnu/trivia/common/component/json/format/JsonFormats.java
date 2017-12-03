package com.ecnu.trivia.common.component.json.format;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 包含多个jsonFormat
 * Created by Jack Chen at 2015/6/29
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface JsonFormats {
    JsonFormat[] value();
}

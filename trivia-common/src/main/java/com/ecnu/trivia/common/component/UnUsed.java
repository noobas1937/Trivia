package com.ecnu.trivia.common.component;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 用于表示这个类或方法 当前还没有被使用到，但未来可能会被使用
 * 或者描述当前某此方法还不会被发布至线上(仅是当前已完成的功能，且未完成测试通过)
 * <p/>
 * Created by Jack Chen at 15-8-2
 */
@Retention(RetentionPolicy.SOURCE)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface UnUsed {
}

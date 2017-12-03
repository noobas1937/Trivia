/** Created by Jack Chen at 8/22/2014 */
package com.ecnu.trivia.common.component;

import com.ecnu.trivia.common.util.ObjectUtils;

/**
 * 表示一个组合对象
 *
 * @author Jack Chen
 */
public class CompositeV1<T> {
    public final T first;

    public CompositeV1(T first) {
        this.first = first;
    }

    /** 判断此对象在逻辑上是否为null */
    public boolean isNull() {
        return ObjectUtils.isNullOrEmpty(first);
    }
}

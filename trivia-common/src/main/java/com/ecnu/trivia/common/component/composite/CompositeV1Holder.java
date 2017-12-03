/** Created by Jack Chen at 8/22/2014 */
package com.ecnu.trivia.common.component.composite;

import com.ecnu.trivia.common.component.CompositeV1;
import com.ecnu.trivia.common.util.ObjectUtils;

/**
 * 表示一个组合对象
 *
 * @author Jack Chen
 */
public class CompositeV1Holder<T> {
    private T first;

    public CompositeV1Holder() {
    }

    public CompositeV1Holder(T first) {
        this.first = first;
    }

    public T getFirst() {
        return first;
    }

    public void setFirst(T first) {
        this.first = first;
    }

    public boolean isNull() {
        return ObjectUtils.isNullOrEmpty(first);
    }

    /** 转换为cv1对象 */
    public CompositeV1<T> toCv1() {
        return new CompositeV1<>(first);
    }
}

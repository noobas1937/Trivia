/** Created by Jack Chen at 8/22/2014 */
package com.ecnu.trivia.common.component.composite;

import com.ecnu.trivia.common.component.CompositeV2;
import com.ecnu.trivia.common.util.ObjectUtils;

/**
 * 表示一个组合对象
 *
 * @author Jack Chen
 */
public class CompositeV2Holder<T, K> extends CompositeV1Holder<T> {
    private K second;

    public CompositeV2Holder() {
    }

    public CompositeV2Holder(T first, K second) {
        super(first);
        this.second = second;
    }

    public K getSecond() {
        return second;
    }

    public void setSecond(K second) {
        this.second = second;
    }

    /** 是否是空值 */
    @Override
    public boolean isNull() {
        return super.isNull() && ObjectUtils.isNullOrEmpty(second);
    }

    /** 转换为cv2对象 */
    public CompositeV2<T, K> toCv2() {
        return new CompositeV2<>(getFirst(), getSecond());
    }
}

/** Created by Jack Chen at 8/22/2014 */
package com.ecnu.trivia.common.component.composite;

import com.ecnu.trivia.common.component.CompositeV3;
import com.ecnu.trivia.common.util.ObjectUtils;

/**
 * 表示一个组合对象
 *
 * @author Jack Chen
 */
public class CompositeV3Holder<T, K, M> extends CompositeV2Holder<T, K> {
    private M third;

    public CompositeV3Holder() {
    }

    public CompositeV3Holder(T first, K second, M third) {
        super(first, second);
        this.third = third;
    }

    public M getThird() {
        return third;
    }

    public void setThird(M third) {
        this.third = third;
    }

    @Override
    public boolean isNull() {
        return super.isNull() && ObjectUtils.isLogicalNull(third);
    }

    /** 转换为cv3对象 */
    public CompositeV3<T, K, M> toCv3() {
        return new CompositeV3<>(getFirst(), getSecond(), getThird());
    }
}

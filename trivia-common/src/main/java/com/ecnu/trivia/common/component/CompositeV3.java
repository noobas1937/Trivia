/** Created by Jack Chen at 8/22/2014 */
package com.ecnu.trivia.common.component;

import com.ecnu.trivia.common.util.ObjectUtils;

/**
 * 表示一个组合对象
 *
 * @author Jack Chen
 */
public class CompositeV3<T, K, M> extends CompositeV2<T, K> {
    @SuppressWarnings("unchecked")
    public static final CompositeV3 NULL = new CompositeV3<>(null, null, null);

    public final M third;

    public CompositeV3(T first, K second, M third) {
        super(first, second);
        this.third = third;
    }

    public CompositeV3(CompositeV2<T, K> cv2, M third) {
        this(cv2.first, cv2.second, third);
    }

    /** 是否是空值 */
    @Override
    public boolean isNull() {
        return this == NULL || (ObjectUtils.isNullOrEmpty(first) && ObjectUtils.isNullOrEmpty(second) && ObjectUtils.isNullOrEmpty(third));
    }

    @Override
    public String toString() {
        return "CompositeV3{" +
                "first=" + first +
                ", second=" + second +
                ", third=" + third +
                '}';
    }

    /** 返回其中的cv2对象 */
    public CompositeV2<T, K> cv2() {
        return new CompositeV2<>(first, second);
    }

    @Override
    public boolean equals(Object o) {
        if(this == o) {
            return true;
        }
        if(o == null || getClass() != o.getClass()) {
            return false;
        }

        CompositeV3 that = (CompositeV3) o;

        if(first != null ? !first.equals(that.first) : that.first != null) {
            return false;
        }
        if(second != null ? !second.equals(that.second) : that.second != null) {
            return false;
        }
        if(third != null ? !third.equals(that.third) : that.third != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = first != null ? first.hashCode() : 0;
        result = 31 * result + (second != null ? second.hashCode() : 0);
        result = 31 * result + (third != null ? third.hashCode() : 0);
        return result;
    }
}

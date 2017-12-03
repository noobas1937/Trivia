/** Created by Jack Chen at 12/5/2014 */
package com.ecnu.trivia.common.component.domain;

import com.google.common.base.Joiner;
import com.ecnu.trivia.common.util.JoinerUtils;
import com.ecnu.trivia.common.util.ObjectUtils;

import java.io.Serializable;
import java.util.Arrays;

/**
 * 用于描述一个对象的主键,主键可以对单个列组成，也可以由多个列组合成一个联合主键
 *
 * @author Jack Chen
 */

public class Key implements Serializable {
    /** 对应的主键记录 */
    private final Comparable[] ids;

    private Key(Comparable... ids) {
        this.ids = ids;
    }

    /** 返回相应的主键信息 */
    public Comparable[] getIds() {
        return ids;
    }

    /** 判断该主键是否是有效的 */
    public boolean isValid() {
        return !ObjectUtils.isLogicalNull(ids);
    }

    /** 构建主键对象 */
    public static Key of(Comparable... ids) {
        return new Key(ids);
    }

    @Override
    public String toString() {
        return ids == null ? "<NULL>" : JoinerUtils.joinByComma(ids, null);
    }

    @Override
    public boolean equals(Object o) {
        if(this == o) {
            return true;
        }
        if(o == null || getClass() != o.getClass()) {
            return false;
        }

        Key key = (Key) o;

        return Arrays.equals(ids, key.ids);
    }

    @Override
    public int hashCode() {
        return ids != null ? Arrays.hashCode(ids) : 0;
    }

    /** 返回相应的主键集 */
    public String ids() {
        return Joiner.on('-').join(ids);
    }
}

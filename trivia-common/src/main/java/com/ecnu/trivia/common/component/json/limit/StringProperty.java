/** Created by Jack Chen at 12/12/2014 */
package com.ecnu.trivia.common.component.json.limit;

import com.alibaba.fastjson.serializer.JSONSerializer;
import com.google.common.primitives.Ints;

import javax.annotation.Nonnull;

/**
 * 用于描述基于字符串的属性表达式
 *
 * @author Jack Chen
 */
class StringProperty implements LimitProperty<StringProperty> {
    private final String property;
    private final int pathLength;

    public StringProperty(String property) {
        this.property = property;
        pathLength = LimitedPropertyFilter.countDot(property);
    }

    @Override
    public int compareTo(@Nonnull StringProperty o) {
        return Ints.compare(this.pathLength, o.pathLength);
    }

    @Override
    public boolean allowPath(String path, int sPathLength, Object source, String name, Object resultValue, JSONSerializer jsonSerializer) {
        if(pathLength >= sPathLength) {
            if(property.equals(path) || property.startsWith(path + ".")) {
                return true;
            }
        }

        return false;
    }

    @Override
    public int getPathLength() {
        return pathLength;
    }

    @Override
    public String toString() {
        return "StringProperty{" +
                "property='" + property + '\'' +
                ", pathLength=" + pathLength +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if(this == o) {
            return true;
        }
        if(o == null || getClass() != o.getClass()) {
            return false;
        }

        StringProperty that = (StringProperty) o;

        return !(property != null ? !property.equals(that.property) : that.property != null);

    }

    @Override
    public int hashCode() {
        return property != null ? property.hashCode() : 0;
    }
}

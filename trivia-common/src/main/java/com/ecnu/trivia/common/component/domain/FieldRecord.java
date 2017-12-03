/** Created by Jack Chen at 12/25/2014 */
package com.ecnu.trivia.common.component.domain;

/**
 * 字段记录，记录当前字段的信息及相应的值
 *
 * @author Jack Chen
 */
public class FieldRecord<T> {
    private final PropertyColumn propertyColumn;
    private final T value;

    public FieldRecord(PropertyColumn propertyColumn, T value) {
        this.propertyColumn = propertyColumn;
        this.value = value;
    }

    /** 获取变更记录所对应的属性 */
    public PropertyColumn getPropertyColumn() {
        return propertyColumn;
    }

    /** 获取对应的值 */
    private T getValue() {
        return value;
    }
}

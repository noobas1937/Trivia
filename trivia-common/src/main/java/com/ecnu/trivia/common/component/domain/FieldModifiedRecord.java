/** Created by Jack Chen at 12/24/2014 */
package com.ecnu.trivia.common.component.domain;

/**
 * 字段变更记录，用于记录在domain中经过修改的信息
 *
 * @author Jack Chen
 */
public class FieldModifiedRecord<T> {
    private final PropertyColumn propertyColumn;
    private final T oldValue;
    private final T newValue;

    public FieldModifiedRecord(PropertyColumn propertyColumn, T oldValue, T newValue) {
        this.propertyColumn = propertyColumn;
        this.oldValue = oldValue;
        this.newValue = newValue;
    }

    /** 获取变更记录所对应的属性 */
    public PropertyColumn getPropertyColumn() {
        return propertyColumn;
    }

    /** 获取变更前的值 */
    public T getOldValue() {
        return oldValue;
    }

    /** 获取变更后的值 */
    public T getNewValue() {
        return newValue;
    }

    /** 转换为旧值的记录 */
    public FieldRecord<T> toOldRecord() {
        return new FieldRecord<>(propertyColumn, oldValue);
    }

    /** 转换为新值的记录 */
    public FieldRecord<T> toNewRecord() {
        return new FieldRecord<>(propertyColumn, newValue);
    }
}

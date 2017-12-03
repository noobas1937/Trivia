/** Created by Jack Chen at 2014/6/24 */
package com.ecnu.trivia.common.component.domain;

import org.apache.ibatis.type.JdbcType;

/**
 * 描述domain类属性值和列的映射关系
 *
 * @author Jack Chen
 */
public class PropertyColumn {
    /** 属性名 */
    private String property;
    /** 属性类型 */
    private Class<?> propertyType;
    /** 列名 */
    private String column;
    /** 列类型 */
    private JdbcType columnType;
    /** 是否是主键列 */
    private boolean keyColumn;
    /** 主键列是否为可生成的 */
    private boolean keyGenerated;

    public PropertyColumn(String property, Class<?> propertyType, String column, JdbcType columnType, boolean keyColumn, boolean keyGenerated) {
        this.property = property;
        this.propertyType = propertyType;
        this.column = column;
        this.columnType = columnType;
        this.keyColumn = keyColumn;
        this.keyGenerated = keyGenerated;
    }

    public String getProperty() {
        return property;
    }

    public Class<?> getPropertyType() {
        return propertyType;
    }

    public String getColumn() {
        return column;
    }

    public JdbcType getColumnType() {
        return columnType;
    }

    public boolean isKeyColumn() {
        return keyColumn;
    }

    public boolean isKeyGenerated() {
        return keyGenerated;
    }

    /** 使用property判断惟一性，因此在一个domain中，不同的property表示数据不一致，而相同的property表示的则一定是相同的定义 */
    @Override
    public boolean equals(Object o) {
        if(this == o) {
            return true;
        }
        if(o == null || getClass() != o.getClass()) {
            return false;
        }

        PropertyColumn that = (PropertyColumn) o;

        return !(property != null ? !property.equals(that.property) : that.property != null);
    }

    @Override
    public int hashCode() {
        return property != null ? property.hashCode() : 0;
    }
}

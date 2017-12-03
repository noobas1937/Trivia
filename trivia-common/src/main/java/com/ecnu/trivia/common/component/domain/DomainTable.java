/** Created by Jack Chen at 2014/6/24 */
package com.ecnu.trivia.common.component.domain;

import com.google.common.base.Function;
import com.ecnu.trivia.common.util.MapUtils;

import java.util.List;
import java.util.Map;

/**
 * 描述对domain对象的数据封装
 *
 * @author Jack Chen
 */
public class DomainTable {
    /** 模型类 */
    private Class<?> domainType;
    /** 表名 */
    private String table;
    /** 主键列 */
    private List<PropertyColumn> keyColumnList;
    /** 所有列 */
    private List<PropertyColumn> propertyColumnList;
    /** 可自动产生主键的列(仅限1个) */
    private PropertyColumn generatedKeyPropertyColumn;
    /** 非自动产生主键的列 */
    private List<PropertyColumn> nonGeneratedPropertyColumnList;

    /** 更方便地访问列属性 */
    private Map<String, PropertyColumn> propertyColumnMap;

    private boolean frozen;

    public Class<?> getDomainType() {
        return domainType;
    }

    public void setDomainType(Class<?> domainType) {
        validateFrozen();
        this.domainType = domainType;
    }

    public String getTable() {
        return table;
    }

    public void setTable(String table) {
        validateFrozen();
        this.table = table;
    }

    public List<PropertyColumn> getKeyColumnList() {
        return keyColumnList;
    }

    public void setKeyColumnList(List<PropertyColumn> keyColumnList) {
        validateFrozen();
        this.keyColumnList = keyColumnList;
    }

    public List<PropertyColumn> getPropertyColumnList() {
        return propertyColumnList;
    }

    public void setPropertyColumnList(List<PropertyColumn> propertyColumnList) {
        validateFrozen();
        this.propertyColumnList = propertyColumnList;
    }

    public PropertyColumn getGeneratedKeyPropertyColumn() {
        return generatedKeyPropertyColumn;
    }

    public void setGeneratedKeyPropertyColumn(PropertyColumn generatedKeyPropertyColumn) {
        validateFrozen();
        this.generatedKeyPropertyColumn = generatedKeyPropertyColumn;
    }

    public List<PropertyColumn> getNonGeneratedPropertyColumnList() {
        return nonGeneratedPropertyColumnList;
    }

    public void setNonGeneratedPropertyColumnList(List<PropertyColumn> nonGeneratedPropertyColumnList) {
        validateFrozen();
        this.nonGeneratedPropertyColumnList = nonGeneratedPropertyColumnList;
    }

    public void frozen() {
        //将属性列表转为map,以方便访问
        propertyColumnMap = MapUtils.asMap(propertyColumnList, new Function<PropertyColumn, String>() {
            @Override
            public String apply(PropertyColumn input) {
                return input.getProperty();
            }
        });

        this.frozen = true;
    }

    private void validateFrozen() {
        if(frozen) {
            throw new RuntimeException("已冻结结构不可再修改");
        }
    }

    /** 根据属性获取相应的定义 */
    public PropertyColumn getPropertyColumn(String property) {
        return propertyColumnMap.get(property);
    }
}

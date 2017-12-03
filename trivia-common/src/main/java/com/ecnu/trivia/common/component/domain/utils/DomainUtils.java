/** Created by Jack Chen at 2014/6/24 */
package com.ecnu.trivia.common.component.domain.utils;

import com.google.common.base.CaseFormat;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.ecnu.trivia.common.component.domain.*;
import com.ecnu.trivia.common.component.exception.Asserts;
import com.ecnu.trivia.common.util.ObjectUtils;
import org.apache.ibatis.type.JdbcType;
import org.springframework.beans.BeanUtils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 用于处理模型映射的工具类
 *
 * @author Jack Chen
 */
public class DomainUtils {
    private static Map<Class<?>, DomainTable> domainTableMap = Maps.newHashMap();
    private static Set<Class<?>> resolvedSet = Sets.newHashSet();

    /** 获取domain类的数据映射 */
    public static DomainTable getDomainTable(Class<?> domainClass) {
        domainClass = getDomainClass(domainClass);
        if(domainClass == null) {
            return null;
        }

        return domainTableMap.get(domainClass);
    }

    private static void resolveDomainTable(Class<?> domainClass) {
        DomainTable domainTable = new DomainTable();

        //表，类映射
        Table table = domainClass.getAnnotation(Table.class);
        domainTable.setTable(table.value());
        domainTable.setDomainType(domainClass);

        //所有列
        List<PropertyColumn> propertyColumnList = Lists.newArrayList();
        Class<?> currentClass = domainClass;
        while(currentClass != Object.class) {
            Field[] fields = currentClass.getDeclaredFields();
            for(Field field : fields) {
                Column column = field.getAnnotation(Column.class);
                if(column == null) {
                    continue;
                }
                Id key = field.getAnnotation(Id.class);

                String property = field.getName();//属性
                Class<?> propertyType = field.getType();//属性类型
                String columnName = column.value();//表字段名
                if(ObjectUtils.isNullOrEmpty(columnName)) {
                    columnName = CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, property);
                }
                JdbcType columnType = column.jdbcType();//表字段类型
                boolean keyColumn = key != null;//是否主键列
                boolean keyGenerated = keyColumn && key.generated();//是否表字段自动生成

                PropertyColumn propertyColumn = new PropertyColumn(property, propertyType, columnName, columnType, keyColumn, keyGenerated);
                propertyColumnList.add(propertyColumn);
            }

            currentClass = currentClass.getSuperclass();
        }
        domainTable.setPropertyColumnList(propertyColumnList);

        //主键列
        List<PropertyColumn> keyColumnList = Lists.newArrayList();
        for(PropertyColumn propertyColumn : propertyColumnList) {
            if(propertyColumn.isKeyColumn()) {
                keyColumnList.add(propertyColumn);
            }
        }
        domainTable.setKeyColumnList(keyColumnList);

        //可自动产生主键的列
        PropertyColumn generatedKeyPropertyColumn = Iterables.find(keyColumnList, new Predicate<PropertyColumn>() {
            @Override
            public boolean apply(PropertyColumn input) {
                return input.isKeyGenerated();
            }
        }, null);
        domainTable.setGeneratedKeyPropertyColumn(generatedKeyPropertyColumn);

        //非自动产生主键的列
        List<PropertyColumn> nonGeneratedPropertyColumnList = Lists.newArrayList(propertyColumnList);
        if(generatedKeyPropertyColumn != null) {
            nonGeneratedPropertyColumnList.remove(generatedKeyPropertyColumn);
        }
        domainTable.setNonGeneratedPropertyColumnList(nonGeneratedPropertyColumnList);

        domainTable.frozen();

        domainTableMap.put(domainClass, domainTable);
    }

    /** 添加域模型类 */
    public static void addDomainClass(Class<?> domainClass) {
        Asserts.assertTrue(domainClass.getAnnotation(Table.class) != null, "类:{}不是domain类,没有Table注解", domainClass);
        resolvedSet.add(domainClass);

        resolveDomainTable(domainClass);
    }

    /** 获取指定类所对应的domain类 */
    @SuppressWarnings("unchecked")
    public static <S, T extends S> Class<S> getDomainClass(Class<T> clazz) {
        if(clazz == Object.class) {
            return null;
        }
        boolean found = resolvedSet.contains(clazz);
        while(!found && clazz != Object.class) {
            clazz = (Class) clazz.getSuperclass();
            found = resolvedSet.contains(clazz);
        }

        return clazz == Object.class ? null : (Class) clazz;
    }

    /** 获取domain对象的可generated的主键 */
    @SuppressWarnings("unchecked")
    public static <T> T getGeneratedKeyValue(Object domainObj) {
        DomainTable domainTable = getDomainTable(domainObj.getClass());
        if(domainTable == null) {
            return null;
        }
        PropertyColumn keyColumn = domainTable.getGeneratedKeyPropertyColumn();
        if(keyColumn == null) {
            return null;
        }
        try {
            return (T) BeanUtils.getPropertyDescriptor(domainTable.getDomainType(), keyColumn.getProperty()).getReadMethod().invoke(domainObj);
        } catch(IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }
}

package org.apache.ibatis.executor.resultset;

import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.executor.parameter.ParameterHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ResultMap;
import org.apache.ibatis.mapping.ResultMapping;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.apache.ibatis.type.TypeHandler;
import org.apache.ibatis.type.TypeHandlerRegistry;

import java.sql.SQLException;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * 替换原mybatis的结果处理方式，以支持驼峰式字段
 *
 * @author Jack Chen
 */
@SuppressWarnings({"unchecked", "unused"})
public class FastResultSetHandlerInstead extends DefaultResultSetHandler {
    private org.apache.ibatis.session.Configuration configuration;
    private TypeHandlerRegistry typeHandlerRegistry;
    private BoundSql boundSql;
    private org.apache.ibatis.reflection.factory.ObjectFactory objectFactory;

    public FastResultSetHandlerInstead(Executor executor, MappedStatement mappedStatement, ParameterHandler parameterHandler, ResultHandler resultHandler, BoundSql boundSql, RowBounds rowBounds) {
        super(executor, mappedStatement, parameterHandler, resultHandler, boundSql, rowBounds);
    }

    private boolean applyAutomaticMappings(ResultSetWrapper rsw, ResultMap resultMap, MetaObject metaObject, String columnPrefix) throws SQLException {
        final List<String> unmappedColumnNames = rsw.getUnmappedColumnNames(resultMap, columnPrefix);
        boolean foundValues = false;
        for(String columnName : unmappedColumnNames) {
            String propertyName = columnName;
            if(columnPrefix != null && columnPrefix.length() > 0) {
                // When columnPrefix is specified,
                // ignore columns without the prefix.
                if(columnName.toUpperCase(Locale.ENGLISH).startsWith(columnPrefix)) {
                    propertyName = columnName.substring(columnPrefix.length());
                } else {
                    continue;
                }
            }
            boolean mapUnderscoreToCamelCase = configuration.isMapUnderscoreToCamelCase();
            String property = metaObject.findProperty(propertyName, mapUnderscoreToCamelCase);

            //尝试去下划线匹配
            if(property == null && !mapUnderscoreToCamelCase) {
                property = metaObject.findProperty(propertyName, true);
            }

            if(property != null && metaObject.hasSetter(property)) {
                final Class<?> propertyType = metaObject.getSetterType(property);
                if(typeHandlerRegistry.hasTypeHandler(propertyType)) {
                    final TypeHandler<?> typeHandler = rsw.getTypeHandler(propertyType, columnName);
                    final Object value = typeHandler.getResult(rsw.getResultSet(), columnName);
                    if(value != null || configuration.isCallSettersOnNulls()) { // issue #377, call setter on nulls
                        if(value != null || !propertyType.isPrimitive()) {
                            metaObject.setValue(property, value);
                        }
                        foundValues = true;
                    }
                }
            }
        }
        return foundValues;
    }

    private Object createResultObject(ResultSetWrapper rsw, ResultMap resultMap, List<Class<?>> constructorArgTypes, List<Object> constructorArgs, String columnPrefix) throws SQLException {
        Class<?> resultType = resultMap.getType();
        List<ResultMapping> constructorMappings = resultMap.getConstructorResultMappings();
        if(typeHandlerRegistry.hasTypeHandler(resultType)) {
            return createPrimitiveResultObject(rsw, resultMap, columnPrefix);
        } else if(constructorMappings.size() > 0) {
            return createParameterizedResultObject(rsw, resultType, constructorMappings, constructorArgTypes, constructorArgs, columnPrefix);
        } else {
            //特殊处理，优先判断特殊的结果类
            final String _clazz = "_clazz";// see Mapper
            Object paramObject = boundSql.getParameterObject();
            if(paramObject instanceof Map) {
                Map map = (Map) paramObject;
                if(map.containsKey(_clazz)) {
                    resultType = (Class<?>) map.get(_clazz);
                }
            }
            return objectFactory.create(resultType);
        }
    }

    private Object createPrimitiveResultObject(ResultSetWrapper rsw, ResultMap resultMap, String columnPrefix) throws SQLException {
        return null;
    }

    @Override
    Object createParameterizedResultObject(ResultSetWrapper rsw, Class<?> resultType, List<ResultMapping> constructorMappings, List<Class<?>> constructorArgTypes, List<Object> constructorArgs, String columnPrefix) {
        return null;
    }
}

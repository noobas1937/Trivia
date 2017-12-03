/** Created by Jack Chen at 11/24/2014 */
package com.ecnu.trivia.common.component.domain;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.ecnu.trivia.common.component.CompositeV2;
import org.springframework.util.ReflectionUtils;

import java.io.Serializable;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Field;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * 描述通用的模型结构
 *
 * @author Jack Chen
 */
public abstract class Domain<T extends Domain> extends Attributable implements Cloneable, Serializable {

    /** 默认构建函数 */
    public Domain() {
    }

    /** 使用主键构建对象 子类应该实现该函数 */
    public Domain(Key key) {
    }

    /** 返回该对象的主键信息 */
    public abstract Key key();

    /** 清除此key数据 */
    public abstract void clearKey();

    //-------------------------------------------------------- 主键模型 end ------------------------------------------//

    //--------------------------------------------------- 数据变更模型 start ----------------------------------------//

    /** 记录数据的变更信息 */
    private Map<PropertyColumn, FieldModifiedRecord<?>> dataModifiedRecordMap = Maps.newHashMap();
    /** 记录标记，只有打开此标识才进行记录 */
    private boolean recordFlag;
    /** 强行记录标记，用于强行记录set内容(忽略更新标记) */
    private boolean forceRecordFlag;

    /** 无用方法，让recordFlag过ide检测 */
    @SuppressWarnings("unused")
    private boolean __getRecordFlag() {
        return recordFlag;
    }

    /** 无用方法，让forceRecordFlag过ide检测 */
    @SuppressWarnings("unused")
    private boolean __getForceRecordFlag() {
        return forceRecordFlag;
    }

    /**
     * 清除之前记录的数据信息,并根据标记确认是否开始记录
     *
     * @param flag 是否开始记录
     */
    public void record(boolean flag) {
        dataModifiedRecordMap.clear();
        this.recordFlag = flag;
    }

    /**
     * 清除之前记录的数据信息,并根据标记确认是否开始强行记录
     *
     * @param flag 是否开始强行记录
     */
    public void forceRecord(boolean flag) {
        this.forceRecordFlag = flag;
        record(flag);
    }

    /** 是否存在变更的记录信息 */
    public boolean recorded() {
        return !dataModifiedRecordMap.isEmpty();
    }

    /** 返回已经记录的数据信息,为避免修改原始数据，返回的数据为副本数据 */
    public Set<FieldModifiedRecord<?>> recordData() {
        return Sets.newHashSet(dataModifiedRecordMap.values());
    }

    public final <V> void addModifiedRecord(DomainTable domainTable, String property, V newValue) {
        if(domainTable == null) {
            return;
        }

        if(!recordFlag) {
            return;
        }

        PropertyColumn propertyColumn = domainTable.getPropertyColumn(property);
        if(propertyColumn == null) {
            return;
        }

        V oldValue = _getFieldValue(property);

        if(forceRecordFlag || !Objects.equals(oldValue, newValue)) {
            @SuppressWarnings("unchecked")
            FieldModifiedRecord<V> sourceRecord = (FieldModifiedRecord) dataModifiedRecordMap.get(propertyColumn);
            if(sourceRecord != null) {
                dataModifiedRecordMap.put(propertyColumn, new FieldModifiedRecord<Object>(propertyColumn, sourceRecord.getOldValue(), newValue));
            } else {
                dataModifiedRecordMap.put(propertyColumn, new FieldModifiedRecord<>(propertyColumn, oldValue, newValue));
            }
        }
    }

    private static Map<CompositeV2<Class, String>, MethodHandle> _methodHandleMap = Maps.newHashMap();

    @SuppressWarnings("unchecked")
    private <V> V _getFieldValue(String property) {
        try{
            CompositeV2<Class, String> cv2 = new CompositeV2<Class, String>(this.getClass(), property);
            MethodHandle methodHandle = _methodHandleMap.get(cv2);
            if(methodHandle == null) {
                Field field = ReflectionUtils.findField(this.getClass(), property);
                field.setAccessible(true);
                methodHandle = MethodHandles.lookup().unreflectGetter(field);
                _methodHandleMap.put(cv2, methodHandle);
            }

            return (V) methodHandle.invoke(this);
        } catch(Throwable t) {
            throw new RuntimeException(t.getMessage(), t);
        }
    }

    //--------------------------------------------------- 数据变更模型 end ----------------------------------------//

    @SuppressWarnings("unchecked")
    @Override
    public T clone() {
        try{
            Domain t = (T) super.clone();
            t.dataModifiedRecordMap = dataModifiedRecordMap.isEmpty() ? Maps.newHashMap() : Maps.newHashMap(dataModifiedRecordMap);
            return (T) t;
        } catch(CloneNotSupportedException ignore) {
        }

        return null;
    }

    @Override
    public boolean equals(Object o) {
        if(this == o) {
            return true;
        }
        if(o == null || getClass() != o.getClass()) {
            return false;
        }

        Domain that = (Domain) o;

        return Objects.equals(key(), that.key());
    }

    @Override
    public int hashCode() {
        Key key = key();
        if(key == null) {
            return 0;
        }

        return key.hashCode();
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + "{" + key() + "}";
    }
}

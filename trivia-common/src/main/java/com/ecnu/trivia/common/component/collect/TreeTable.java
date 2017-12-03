/** Created by Jack Chen at 15-8-21 */
package com.ecnu.trivia.common.component.collect;

import com.google.common.collect.Maps;
import com.google.common.collect.Ordering;
import com.google.common.collect.Sets;
import com.google.common.collect.Table;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

/** @author Jack Chen */
public class TreeTable<R, C, V> implements Table<R, C, V> {
    /** 行比较器 */
    @SuppressWarnings("all")
    private final Comparator<R> rowComparator;

    /** 列比较器 */
    private Comparator<C> columnComparator;

    /** 描述底层值信息 */
    private TreeMap<R, Map<C, V>> valueMap;

    /** 相应的列集合 */
    private TreeSet<C> columnSet;

    @SuppressWarnings({"unused", "unchecked"})
    public TreeTable() {
        this((Comparator) Ordering.natural(), (Comparator) Ordering.natural());
    }

    public TreeTable(Comparator<R> rowComparator, Comparator<C> columnComparator) {
        this.rowComparator = rowComparator;
        this.columnComparator = columnComparator;
        valueMap = Maps.newTreeMap(rowComparator);
        columnSet = Sets.newTreeSet(columnComparator);
    }

    public Comparator<R> getRowComparator() {
        return rowComparator;
    }

    public Comparator<C> getColumnComparator() {
        return columnComparator;
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean contains(@Nullable Object rowKey, @Nullable Object columnKey) {
        if(rowKey == null || columnKey == null) {
            return false;
        }

        R rKey = (R) rowKey;
        C cKey = (C) columnKey;
        Map<C, V> columnMap = valueMap.get(rKey);
        return columnMap != null && columnMap.containsKey(cKey);
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean containsRow(@Nullable Object rowKey) {
        if(rowKey == null) {
            return false;
        }

        R rKey = (R) rowKey;
        return valueMap.containsKey(rKey);
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean containsColumn(@Nullable Object columnKey) {
        if(columnKey == null) {
            return false;
        }

        C cKey = (C) columnKey;
        return columnSet.contains(cKey);
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean containsValue(@Nullable Object value) {
        if(value == null) {
            return false;
        }

        V v = (V) value;
        for(Map<C, V> row : rowMap().values()) {
            if(row.containsValue(v)) {
                return true;
            }
        }
        return false;
    }

    @Override
    @SuppressWarnings("unchecked")
    public V get(@Nullable Object rowKey, @Nullable Object columnKey) {
        if(rowKey == null || columnKey == null) {
            return null;
        }

        R rKey = (R) rowKey;
        C cKey = (C) columnKey;

        Map<C, V> columnMap = valueMap.get(rKey);
        if(columnMap != null) {
            return columnMap.get(cKey);
        }

        return null;
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean isEmpty() {
        return valueMap.isEmpty();
    }

    @Override
    public int size() {
        int size = 0;
        for(Map<C, V> map : valueMap.values()) {
            size += map.size();
        }
        return size;
    }

    @Override
    public void clear() {
        valueMap.clear();
    }

    private Map<C, V> getOrCreate(R rowKey) {
        Map<C, V> map = valueMap.get(rowKey);
        if(map == null) {
            map = Maps.newTreeMap(columnComparator);
            valueMap.put(rowKey, map);
        }
        return map;
    }

    @Override
    public V put(@Nonnull R rowKey, @Nonnull C columnKey, @Nonnull V value) {
        Objects.requireNonNull(rowKey);
        Objects.requireNonNull(columnKey);
        Objects.requireNonNull(value);

        V v = getOrCreate(rowKey).put(columnKey, value);

        //同时加入到列集合中
        columnSet.add(columnKey);

        return v;
    }

    @Override
    public void putAll(@Nonnull Table<? extends R, ? extends C, ? extends V> table) {
        @SuppressWarnings("unchecked")
        Table<R, C, V> sourceTable = (Table<R, C, V>) table;

        for(Map.Entry<R, Map<C, V>> e : sourceTable.rowMap().entrySet()) {
            R r = e.getKey();
            Map<C, V> cMap = e.getValue();
            for(Map.Entry<C, V> re : cMap.entrySet()) {
                put(r, re.getKey(), re.getValue());
            }
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public V remove(@Nullable Object rowKey, @Nullable Object columnKey) {
        if(rowKey == null || columnKey == null) {
            return null;
        }

        R rKey = (R) rowKey;
        C cKey = (C) columnKey;

        Map<C, V> map = valueMap.get(rKey);
        if(map == null) {
            return null;
        }

        V value = map.remove(cKey);

        //如果相应的列map均为空，则移除该值
        if(map.isEmpty()) {
            valueMap.remove(rKey);
        }

        return value;
    }

    @Override
    public Map<C, V> row(@Nonnull R rowKey) {
        return valueMap.get(rowKey);
    }

    @Override
    public Map<R, V> column(@Nonnull C columnKey) {
        throw new UnsupportedOperationException("不支持直接取列信息");
    }

    @Override
    public Set<Cell<R, C, V>> cellSet() {
        throw new UnsupportedOperationException("不支持获取单元格信息");
    }

    @Override
    public Set<R> rowKeySet() {
        return valueMap.keySet();
    }

    @Override
    public Set<C> columnKeySet() {
        return columnSet;
    }

    @Override
    public Collection<V> values() {
        throw new UnsupportedOperationException("不支持获取所有值信息");
    }

    @Override
    public Map<R, Map<C, V>> rowMap() {
        return valueMap;
    }

    @Override
    public Map<C, Map<R, V>> columnMap() {
        throw new UnsupportedOperationException("不支持直接取列信息");
    }
}

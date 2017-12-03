package com.ecnu.trivia.common.component.cache.web;

import com.google.common.collect.Maps;
import com.ecnu.trivia.common.component.cache.AbstractCache;
import com.ecnu.trivia.common.component.cache.utils.CacheValue;


import java.util.concurrent.ConcurrentMap;

/**
 * 实现系统全局缓存
 *
 * @author Jack Chen
 */
public class ApplicationCache extends AbstractCache
{
    //考虑应用MapDB替换普通ConcurrentMap
    private ConcurrentMap<String, Object> map = Maps.newConcurrentMap();

    @Override
    protected String prefix() {
        return "appCache";
    }

    @Override
    public String getName() {
        return CacheValue.APPLICATION;
    }

    @Override
    public Object getNativeCache() {
        return map;
    }

    @Override
    public ValueWrapper get(Object key) {
        return toWrapper(map.get(key(key)));
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T get(Object key, Class<T> type) {
        return (T) fromStoreValue(map.get(key(key)));
    }

    @Override
    public void put(Object key, Object value) {
        map.put(key(key), toStoreValue(value));
    }

    @Override
    public ValueWrapper putIfAbsent(Object key, Object value) {
        return toWrapper(map.putIfAbsent(key(key), toStoreValue(value)));
    }

    @Override
    public void evict(Object key) {
        map.remove(key(key));
    }

    @Override
    public void clear() {
        map.clear();
    }

    @Override
    public boolean isAvailable() {
        return true;
    }
}

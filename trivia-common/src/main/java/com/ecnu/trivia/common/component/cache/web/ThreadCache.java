/** Created by Jack Chen at 15-7-20 */
package com.ecnu.trivia.common.component.cache.web;

import com.google.common.collect.Maps;

import com.ecnu.trivia.common.component.cache.AbstractCache;
import com.ecnu.trivia.common.component.cache.utils.CacheValue;
import org.springframework.cache.support.SimpleValueWrapper;

import java.util.concurrent.ConcurrentHashMap;

/**
 * 实现基于线程的缓存处理
 *
 * @author Jack Chen
 */
public class ThreadCache extends AbstractCache
{
    @SuppressWarnings("unchecked")
    public static ThreadLocal<ConcurrentHashMap<String, Object>> mapThreadLocal = new ThreadLocal() {
        @Override
        protected Object initialValue() {
            return Maps.newConcurrentMap();
        }
    };

    @Override
    public String getName() {
        return CacheValue.THREAD;
    }

    @Override
    protected String prefix() {
        return "threadCache";
    }

    @Override
    public Object getNativeCache() {
        return mapThreadLocal.get();
    }

    @Override
    public ValueWrapper get(Object key) {
        return toWrapper(mapThreadLocal.get().get(key(key)));
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T get(Object key, Class<T> type) {
        return (T) fromStoreValue(mapThreadLocal.get().get(key(key)));
    }

    @Override
    public void put(Object key, Object value) {
        mapThreadLocal.get().put(key(key), toStoreValue(value));
    }

    @Override
    public ValueWrapper putIfAbsent(Object key, Object value) {
        Object existing = mapThreadLocal.get().putIfAbsent(key(key), toStoreValue(value));
        return toWrapper(existing);
    }

    @Override
    public void evict(Object key) {
        mapThreadLocal.get().remove(key(key));
    }

    @Override
    public void clear() {
        clearThreadCache();
    }

    public static void clearThreadCache() {
        mapThreadLocal.get().clear();
    }

    @Override
    public boolean isAvailable() {
        return true;
    }

    /** 线程级缓存不再需要clone，因为单线程内不存在互相修改的问题 */
    @Override
    protected ValueWrapper toWrapper(Object value) {
        return (value != null ? new SimpleValueWrapper(fromStoreValue(value)) : null);
    }
}

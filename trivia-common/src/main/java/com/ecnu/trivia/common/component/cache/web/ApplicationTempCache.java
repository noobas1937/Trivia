/** Created by Jack Chen at 15-7-24 */
package com.ecnu.trivia.common.component.cache.web;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import com.ecnu.trivia.common.ApplicationContextHolder;
import com.ecnu.trivia.common.component.cache.AbstractCache;
import com.ecnu.trivia.common.component.cache.utils.CacheValue;
import com.ecnu.trivia.common.exception.IRCloudException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * 实现全局的临时缓存
 *
 * @author Jack Chen
 */
public class ApplicationTempCache extends AbstractCache
{
    private Cache<String, Object> cache;

    @Override
    public void init() {
        Config config = Config.getInstance();
        cache = CacheBuilder.newBuilder()
                .expireAfterAccess(config.expire, TimeUnit.SECONDS)
                .softValues()
                .maximumSize(config.capacity)
                .build();
    }

    @Override
    protected String prefix() {
        return "appTempCache";
    }

    @Override
    public String getName() {
        return CacheValue.APPLICATION_TEMP;
    }

    @Override
    public Object getNativeCache() {
        return cache;
    }

    @Override
    public ValueWrapper get(Object key) {
        return toWrapper(cache.getIfPresent(key(key)));
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T get(Object key, Class<T> type) {
        return (T) fromStoreValue(cache.getIfPresent(key(key)));
    }

    @Override
    public void put(Object key, Object value) {
        cache.put(key(key), toStoreValue(value));
    }

    @Override
    public ValueWrapper putIfAbsent(Object key, final Object value) {
        final Object result;
        try{
            result = cache.get(key(key), new Callable<Object>() {
                @Override
                public Object call() throws Exception {
                    return toStoreValue(value);
                }
            });
        } catch(ExecutionException e) {
            throw new IRCloudException(e.getMessage(), e);
        }

        return toWrapper(result);
    }

    @Override
    public void evict(Object key) {
        cache.invalidate(key(key));
    }

    @Override
    public void clear() {
        cache.invalidateAll();
    }

    @Override
    public boolean isAvailable() {
        return true;
    }

    @Component(value = "ApplicationTempCache.Config")
    public static class Config {
        private static Config instance;

        /** 容量 默认10000 */
        @Value("${cache.applicationTemp.capacity:10000}")
        private int capacity;

        /** 过期时间(秒) 默认30分钟 */
        @Value("${cache.applicationTemp.expire:1800}")
        private int expire;

        public int getCapacity() {
            return capacity;
        }

        public int getExpire() {
            return expire;
        }

        public static Config getInstance() {
            if(instance == null) {
                instance = ApplicationContextHolder.getInstance(Config.class);
            }

            return instance;
        }
    }
}

/** Created by Jack Chen at 15-7-20 */
package com.ecnu.trivia.common.component.cache.web;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.Maps;

import com.ecnu.trivia.common.component.cache.AbstractCache;
import com.ecnu.trivia.common.component.cache.utils.CacheUtils;
import com.ecnu.trivia.common.component.cache.utils.CacheValue;
import com.ecnu.trivia.common.component.web.servlet.HttpServletContext;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * 实现本地会话级别的缓存
 *
 * @author Jack Chen
 */
public class LocalSessionCache extends AbstractCache
{
    private Cache<String, Map<String, Object>> localCache;

    private Cache<String, Map<String, Object>> _get(HttpSession httpSession) {
        if(localCache == null) {
            localCache = CacheBuilder.newBuilder().weakKeys().expireAfterWrite(httpSession.getMaxInactiveInterval(), TimeUnit.SECONDS).build();
        }

        return localCache;
    }

    @Override
    protected String prefix() {
        return "localSessionCache";
    }

    @Override
    public String getName() {
        return CacheValue.LOCAL_SESSION;
    }

    private static HttpSession getSession() {
        HttpServletRequest request = HttpServletContext.getRequestOrDefault(null);
        if(request == null) {
            return null;
        }

        return request.getSession();
    }

    @Override
    public Object getNativeCache() {
        return null;
    }

    @SuppressWarnings("unchecked")
    private <T> T _getValue(Object key) {
        HttpSession session = getSession();
        if(session == null) {
            return null;
        }

        Map<String, Object> map = _get(session).getIfPresent(session.getId());

        String strKey = key(key);
        return (T) (map == null ? null : map.get(strKey));
    }

    @Override
    public ValueWrapper get(Object key) {
        return toWrapper(_getValue(key));
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T get(Object key, Class<T> type) {
        return (T) fromStoreValue(_getValue(key));
    }

    private static Callable<Map<String, Object>> valueLoader = new Callable<Map<String, Object>>() {
        @Override
        public Map<String, Object> call() throws Exception {
            return Maps.newHashMap();
        }
    };

    @Override
    public void put(Object key, Object value) {
        HttpSession session = getSession();
        if(session == null) {
            return;
        }

        Map<String, Object> map;
        try{
            map = _get(session).get(session.getId(), valueLoader);
        } catch(ExecutionException e) {
            throw new RuntimeException(e.getMessage(), e);
        }

        String strKey = key(key);

        map.put(strKey, toStoreValue(value));
    }

    @Override
    public ValueWrapper putIfAbsent(Object key, Object value) {
        HttpSession session = getSession();
        if(session == null) {
            return null;
        }

        Map<String, Object> map;
        try{
            map = _get(session).get(session.getId(), valueLoader);
        } catch(ExecutionException e) {
            throw new RuntimeException(e.getMessage(), e);
        }

        String strKey = key(key);
        Object oldValue = map.get(strKey);

        if(oldValue == null) {
            map.put(strKey, toStoreValue(value));
        }

        return toWrapper(oldValue);
    }

    @Override
    public void evict(Object key) {
        HttpSession session = getSession();
        if(session == null) {
            return;
        }

        _get(session).invalidate(key(key));
    }

    @Override
    public void clear() {
        HttpSession session = getSession();
        if(session == null) {
            return;
        }

        _get(session).invalidateAll();
    }

    @Override
    public boolean isAvailable() {
        return CacheUtils.isJavaEEAvailable();
    }
}

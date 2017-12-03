/** Created by Jack Chen at 15-7-20 */
package com.ecnu.trivia.common.component.cache.web;


import com.ecnu.trivia.common.component.cache.AbstractCache;
import com.ecnu.trivia.common.component.cache.utils.CacheUtils;
import com.ecnu.trivia.common.component.cache.utils.CacheValue;
import com.ecnu.trivia.common.component.web.servlet.HttpServletContext;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;

/**
 * 实现请求级别的缓存
 *
 * @author Jack Chen
 */
public class RequestCache extends AbstractCache
{
    @Override
    public String getName() {
        return CacheValue.REQUEST;
    }

    private static HttpServletRequest getRequest() {
        return HttpServletContext.getRequestOrDefault(null);
    }

    @Override
    protected String prefix() {
        return "_requestCache";
    }

    @Override
    public Object getNativeCache() {
        return null;
    }

    @Override
    public ValueWrapper get(Object key) {
        HttpServletRequest request = getRequest();
        if(request == null) {
            return null;
        }

        String strKey = key(key);
        Object value = request.getAttribute(strKey);

        return toWrapper(value);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T get(Object key, Class<T> type) {
        HttpServletRequest request = getRequest();
        if(request == null) {
            return null;
        }

        return (T) fromStoreValue(request.getAttribute(key(key)));
    }

    @Override
    public void put(Object key, Object value) {
        HttpServletRequest request = getRequest();
        if(request == null) {
            return;
        }

        request.setAttribute(key(key), toStoreValue(value));
    }

    @Override
    public ValueWrapper putIfAbsent(Object key, Object value) {
        HttpServletRequest request = getRequest();
        if(request == null) {
            return null;
        }

        String strKey = key(key);

        Object oldValue = request.getAttribute(strKey);

        if(oldValue == null) {
            request.setAttribute(strKey, toStoreValue(value));
            return toWrapper(value);
        }

        return toWrapper(oldValue);
    }

    @Override
    public void evict(Object key) {
        HttpServletRequest request = getRequest();
        if(request == null) {
            return;
        }

        request.removeAttribute(key(key));
    }

    @Override
    public void clear() {
        HttpServletRequest request = getRequest();
        if(request == null) {
            return;
        }

        Enumeration<String> enumeration = request.getAttributeNames();
        while(enumeration.hasMoreElements()) {
            String key = enumeration.nextElement();
            if(key.startsWith(prefix())) {
                request.removeAttribute(key);
            }
        }
    }

    @Override
    public boolean isAvailable() {
        return CacheUtils.isJavaEEAvailable();
    }
}

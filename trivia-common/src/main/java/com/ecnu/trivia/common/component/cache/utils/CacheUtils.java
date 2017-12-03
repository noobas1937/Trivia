/** Created by Jack Chen at 15-7-29 */
package com.ecnu.trivia.common.component.cache.utils;

import com.google.common.collect.Maps;
import com.ecnu.trivia.common.component.CompositeV2;
import org.mvel2.MVEL;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;

/** @author Jack Chen */
public class CacheUtils {
    private static ConcurrentMap<String, Serializable> expressionCache = Maps.newConcurrentMap();

    public static CompositeV2<CacheClazz, CacheKey> resolveMethod(Method method, Object target, Object[] args) {
        Map<String, Object> ctx = Maps.newHashMap();
        ctx.put("self", target);
        ctx.put("args", args);

        return resolveMethod(method, ctx);
    }

    public static CompositeV2<CacheClazz, CacheKey> resolveMethod(Method method, Object cacheClazzCtx) {
        Class<?> cachedClass = null;
        CacheClazz cacheClazz = method.getAnnotation(CacheClazz.class);
        if(cacheClazz != null) {
            cachedClass = MVEL.executeExpression(buildExpression(cacheClazz.clazz()), cacheClazzCtx, Class.class);
        }
        if(cachedClass == null) {
            cachedClass = method.getReturnType();
        }

        CacheKey cache = method.getAnnotation(CacheKey.class);
        if(cache == null) {
            cache = cachedClass.getAnnotation(CacheKey.class);
        }

        return new CompositeV2<>(cacheClazz, cache);
    }

    /** 获取计算缓存 */
    public static Serializable buildExpression(String key) {
        Serializable value = expressionCache.get(key);
        if(value == null) {
            value = MVEL.compileExpression(key);
            expressionCache.put(key, value);
        }

        return value;
    }

    private static Boolean _javaEEAvailable = null;

    /** 是否javaEE可用,即存在相应的class */
    public static boolean isJavaEEAvailable() {
        if(_javaEEAvailable == null) {
            try{
                Class.forName("javax.servlet.http.HttpServletRequest");
                _javaEEAvailable = true;
            } catch(ClassNotFoundException ignore) {
                _javaEEAvailable = false;
            }
        }

        return _javaEEAvailable;
    }
}

package com.ecnu.trivia.common.component.cache;

import com.google.common.collect.Maps;
import com.ecnu.trivia.common.component.cache.utils.CacheClazz;
import com.ecnu.trivia.common.component.cache.utils.CacheKey;
import com.ecnu.trivia.common.component.cache.utils.CacheUtils;
import com.ecnu.trivia.common.component.CompositeV2;
import com.ecnu.trivia.common.util.ObjectUtils;
import org.mvel2.MVEL;
import org.springframework.cache.interceptor.KeyGenerator;

import java.lang.reflect.Method;
import java.util.Map;

/**
 * 用于实现domain的缓存key生成
 *
 * @author Jack Chen
 */
public class DomainKeyGenerator implements KeyGenerator {
    @Override
    public Object generate(Object target, Method method, Object... params) {
        throw new RuntimeException("不应该调用此方法");
    }

    public Object generateKey(String key, Object target, Method method, Object[] args) {
        Map<String, Object> ctx = Maps.newHashMap();
        ctx.put("self", target);
        ctx.put("args", args);

        CompositeV2<CacheClazz, CacheKey> cv2 = CacheUtils.resolveMethod(method, ctx);
        CacheKey cacheKey = cv2.second;

        String prefix = null;
        if(cacheKey != null) {
            prefix = cacheKey.prefix();
        }
        prefix = ObjectUtils.isNullOrEmpty(prefix) ? "" : prefix + "-";

        return prefix + MVEL.executeExpression(CacheUtils.buildExpression(key), (Object) ctx);
    }
}

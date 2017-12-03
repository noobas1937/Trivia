/** Created by Jack Chen at 15-8-5 */
package com.ecnu.trivia.common.component.cache;


import com.ecnu.trivia.common.component.cache.utils.CacheValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.Cache;
import org.springframework.cache.interceptor.CacheOperationInvocationContext;
import org.springframework.cache.interceptor.SimpleCacheResolver;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

/**
 * 解决在查找不到缓存的情况下，忽略相应的缓存设置
 *
 * @author Jack Chen
 */
public class DomainCacheResolver extends SimpleCacheResolver {
    private static final Logger logger = LoggerFactory.getLogger(DomainCacheResolver.class);

    @SuppressWarnings("unused")
    public DomainCacheResolver() {
    }

    public DomainCacheResolver(CacheManager cacheManager) {
        super(cacheManager);
    }

    @Override
    public Collection<? extends Cache> resolveCaches(CacheOperationInvocationContext<?> context) {
        Collection<String> cacheNames = getCacheNames(context);
        if(cacheNames == null) {
            return Collections.emptyList();
        } else {
            Collection<Cache> result = new ArrayList<>();
            for(String cacheName : cacheNames) {
                Cache cache = getCacheManager().getCache(cacheName);
                if(cache == null) {
                    if(!CacheValue.INNER_CACHE_VALUES.contains(cacheName)) {//如果是内置的，则表示是其它原因(如缓存不可用)，避免出现多次提示
                        logger.debug("根据:{}名找不到相应的缓存设置", cacheName);
                    }
                    continue;
                }
                result.add(cache);
            }
            return result;
        }
    }
}

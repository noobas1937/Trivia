/** Created by Jack Chen at 15-7-20 */
package com.ecnu.trivia.common.component.cache;

import com.google.common.collect.Lists;
import com.ecnu.trivia.common.ApplicationContextHolder;
import com.ecnu.trivia.common.util.ClassUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.cache.Cache;
import org.springframework.cache.ehcache.EhCacheCacheManager;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * 自实现cache管理，以提供动态添加cache实例的效果
 *
 * @author Jack Chen
 */
public class CacheManager extends SimpleCacheManager implements ApplicationContextAware {
    private static final Logger logger = LoggerFactory.getLogger(CacheManager.class);

    private static List<String> innerCacheClassList = Lists.newArrayList();

    static {
        innerCacheClassList.add("ThreadCache");
        innerCacheClassList.add("RequestCache");
        innerCacheClassList.add("LocalSessionCache");
        innerCacheClassList.add("SessionCache");
        innerCacheClassList.add("ApplicationCache");
        innerCacheClassList.add("ApplicationTempCache");
    }

    private ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    protected Collection<? extends Cache> loadCaches() {
        return super.loadCaches() == null ? Collections.<Cache>emptyList() : super.loadCaches();
    }

    /** 添加cache实例 */
    public void add(Cache cache) {
        super.addCache(cache);
    }

    @SuppressWarnings("unchecked")
    private CacheEx _loadCache(String cacheClassName) {
        Class<? extends CacheEx> clazz;
        try{
            clazz = (Class<? extends CacheEx>) Class.forName(cacheClassName);
        } catch(ClassNotFoundException ignore) {
            logger.warn("缓存类,类加载{}不可用,不能加载之", cacheClassName);
            return null;
        }

        CacheEx instance = ClassUtils.newInstance(clazz);
        if(!instance.isAvailable()) {
            logger.warn("缓存类:{},状态不可用", cacheClassName);
            return null;
        }

        applicationContext.getAutowireCapableBeanFactory().autowireBeanProperties(instance, AutowireCapableBeanFactory.AUTOWIRE_BY_TYPE, true);
        instance.init();
        return instance;
    }

    @Override
    public void afterPropertiesSet() {
        super.afterPropertiesSet();
        //加入内部的缓存实例
        for(String innerCacheClass : innerCacheClassList) {
            Cache cache = _loadCache(innerCacheClass);
            if(cache != null) {
                add(cache);
            }
        }

        //加入ehcache
        EhCacheCacheManager ehCacheCacheManager = ApplicationContextHolder.getInstance(EhCacheCacheManager.class);
        ehCacheCacheManager.getCacheNames().forEach(cacheName -> add(ehCacheCacheManager.getCache(cacheName)));

    }
}

package com.ecnu.trivia.common.component.cache;

import com.google.common.collect.Lists;
import com.ecnu.trivia.common.component.cache.utils.CacheClazz;
import com.ecnu.trivia.common.component.cache.utils.CacheKey;
import com.ecnu.trivia.common.component.cache.utils.CacheUtils;
import com.ecnu.trivia.common.component.CompositeV2;
import com.ecnu.trivia.common.component.mapper.Mapper;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.cache.interceptor.AbstractCacheInvoker;
import org.springframework.cache.interceptor.CacheAspectSupport;
import org.springframework.cache.interceptor.CacheEvictOperation;
import org.springframework.cache.interceptor.CacheOperation;
import org.springframework.cache.interceptor.CacheOperationInvocationContext;
import org.springframework.cache.interceptor.CacheOperationInvoker;
import org.springframework.cache.interceptor.CachePutOperation;
import org.springframework.cache.interceptor.CacheResolver;
import org.springframework.cache.interceptor.CacheableOperation;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.cache.interceptor.SimpleKeyGenerator;
import org.springframework.cache.support.SimpleValueWrapper;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.ObjectUtils;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Cache AOP应用类
 * @author Jack Chen
 * */
@Aspect
public class CacheAspect extends AbstractCacheInvoker implements InitializingBean, ApplicationContextAware {

    private static final Logger logger = LoggerFactory.getLogger(CacheAspect.class);

    private static final Object NO_RESULT = new Object();
    private static final Object RESULT_UNAVAILABLE = new Object();

    @Pointcut("execution(@org.springframework.cache.annotation.Cacheable * *(..))")
    private static void executionOfCacheableMethod() {
    }

    @Pointcut("execution(@org.springframework.cache.annotation.CacheEvict * *(..))")
    private static void executionOfCacheEvictMethod() {
    }

    @Pointcut("execution(@org.springframework.cache.annotation.CachePut * *(..))")
    private static void executionOfCachePutMethod() {
    }

    @Pointcut("execution(@org.springframework.cache.annotation.Caching * *(..))")
    private static void executionOfCachingMethod() {
    }

    @Pointcut("(" +
            "executionOfCacheableMethod()" +
            "|| executionOfCacheEvictMethod()" +
            "|| executionOfCachePutMethod()" +
            "|| executionOfCachingMethod())")
    private static void cacheMethodExecution() {
    }

    public CacheAspect() {
    }

    @Around("cacheMethodExecution()")
    public Object aroundCall(final ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        Method method = methodSignature.getMethod();

        CacheOperationInvoker aspectJInvoker = new CacheOperationInvoker() {
            @Override
            public Object invoke() {
                try{
                    return joinPoint.proceed();
                } catch(Throwable throwable) {
                    throw new ThrowableWrapper(throwable);
                }
            }
        };

        try{
            return execute(aspectJInvoker, joinPoint.getTarget(), method, joinPoint.getArgs());
        } catch(CacheOperationInvoker.ThrowableWrapper e) {
            throw e.getOriginal();
        }
    }

    private KeyGenerator keyGenerator = new SimpleKeyGenerator();

    private CacheResolver cacheResolver;

    private ApplicationContext applicationContext;

    private boolean initialized = false;

    @SuppressWarnings("unused")
    public void setKeyGenerator(KeyGenerator keyGenerator) {
        this.keyGenerator = keyGenerator;
    }

    @SuppressWarnings("unused")
    public void setCacheManager(CacheManager cacheManager) {
        this.cacheResolver = new DomainCacheResolver(cacheManager);
    }

    @SuppressWarnings("unused")
    public void setCacheResolver(CacheResolver cacheResolver) {
        Assert.notNull(cacheResolver);
        this.cacheResolver = cacheResolver;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Override
    public void afterPropertiesSet() {
        Assert.state(this.cacheResolver != null, "'cacheResolver' is required. Either set the cache resolver " +
                "to use or set the cache manager to create a default cache resolver based on it.");
        Assert.state(this.getErrorHandler() != null, "The 'errorHandler' is required.");
        Assert.state(this.applicationContext != null, "The application context was not injected as it should.");
        this.initialized = true;
    }

    protected Collection<? extends org.springframework.cache.Cache> getCaches(CacheOperationInvocationContext<CacheOperation> context,
                                                                              CacheResolver cacheResolver) {
        return cacheResolver.resolveCaches(context);
    }

    protected CacheOperationContext getOperationContext(CacheOperation operation, Method method, Object[] args, Object target) {
        return new CacheOperationContext(operation, method, args, target);
    }

    public Object execute(CacheOperationInvoker invoker, Object target, Method method, Object[] args) {
        // check whether aspect is enabled
        // to cope with cases where the AJ is pulled in automatically
        if(this.initialized) {
            Collection<CacheOperation> operations = parseCacheAnnotations(method);

            boolean shouldInvokeCache = true;

            //处理缓存name值
            for(CacheOperation operation : operations) {
                if(com.ecnu.trivia.common.util.ObjectUtils.isNullOrEmpty(operation.getCacheNames())) {
                    CompositeV2<CacheClazz, CacheKey> cv2 = CacheUtils.resolveMethod(method, target, args);
                    if(cv2.second != null) {
                        operation.setCacheNames(cv2.second.cacheName());
                    }
                }

                if(com.ecnu.trivia.common.util.ObjectUtils.isNullOrEmpty(operation.getCacheNames())) {
                    if(method.getDeclaringClass() != Mapper.class) {//避免出现太多的打印输出,因为很多domain类并没有定义相应的输出信息
                        logger.debug("没有定义缓存value值" + target + "," + method + "," + Arrays.toString(args));
                    }
                    shouldInvokeCache = false;
                    break;
                }
            }

            if(!shouldInvokeCache) {
                return invoker.invoke();
            }

            if(!CollectionUtils.isEmpty(operations)) {
                return execute(invoker, new CacheOperationContexts(operations, method, args, target));
            }
        }

        return invoker.invoke();
    }

    protected Object invokeOperation(CacheOperationInvoker invoker) {
        return invoker.invoke();
    }

    private Object execute(CacheOperationInvoker invoker, CacheOperationContexts contexts) {
        // Process any early evictions
        processCacheEvicts(contexts.get(CacheEvictOperation.class), true, NO_RESULT);

        // Check if we have a cached item matching the conditions
        org.springframework.cache.Cache.ValueWrapper cacheHit = findCachedItem(contexts.get(CacheableOperation.class));

        // Collect puts from any @Cacheable miss, if no cached item is found
        List<CachePutRequest> cachePutRequests = new LinkedList<>();
        if(cacheHit == null) {
            collectPutRequests(contexts.get(CacheableOperation.class), NO_RESULT, cachePutRequests);
        }

        org.springframework.cache.Cache.ValueWrapper result = null;

        // If there are no put requests, just use the cache hit
        if(cachePutRequests.isEmpty() && !hasCachePut(contexts)) {
            result = cacheHit;
        }

        // Invoke the method if don't have a cache hit
        if(result == null) {
            result = new SimpleValueWrapper(invokeOperation(invoker));
        }

        // Collect any explicit @CachePuts
        collectPutRequests(contexts.get(CachePutOperation.class), result.get(), cachePutRequests);

        // Process any collected put requests, either from @CachePut or a @Cacheable miss
        for(CachePutRequest cachePutRequest : cachePutRequests) {
            cachePutRequest.apply(result.get());
        }

        // Process any late evictions
        processCacheEvicts(contexts.get(CacheEvictOperation.class), false, result.get());

        return result.get();
    }

    private boolean hasCachePut(CacheOperationContexts contexts) {
        // Evaluate the conditions *without* the result object because we don't have it yet.
        Collection<CacheOperationContext> cachePutContexts = contexts.get(CachePutOperation.class);
        Collection<CacheOperationContext> excluded = new ArrayList<>();
        for(CacheOperationContext context : cachePutContexts) {
            if(!context.isConditionPassing(RESULT_UNAVAILABLE)) {
                excluded.add(context);
            }
        }
        // check if  all puts have been excluded by condition
        return cachePutContexts.size() != excluded.size();
    }

    private void processCacheEvicts(Collection<CacheOperationContext> contexts, boolean beforeInvocation, Object result) {
        for(CacheOperationContext context : contexts) {
            CacheEvictOperation operation = (CacheEvictOperation) context.getOperation();
            if(beforeInvocation == operation.isBeforeInvocation() && isConditionPassing(context, result)) {
                performCacheEvict(context, operation, result);
            }
        }
    }

    private void performCacheEvict(CacheOperationContext context, CacheEvictOperation operation, Object result) {
        Object key = null;
        for(org.springframework.cache.Cache cache : context.getCaches()) {
            if(operation.isCacheWide()) {
                logInvalidating(context, operation, null);
                doClear(cache);
            } else {
                if(key == null) {
                    key = context.generateKey(result);
                }
                logInvalidating(context, operation, key);
                doEvict(cache, key);
            }
        }
    }

    private void logInvalidating(CacheOperationContext context, CacheEvictOperation operation, Object key) {
        if(logger.isTraceEnabled()) {
            logger.trace("Invalidating " + (key != null ? "cache key [" + key + "]" : "entire cache") +
                    " for operation " + operation + " on method " + context.method);
        }
    }

    private org.springframework.cache.Cache.ValueWrapper findCachedItem(Collection<CacheOperationContext> contexts) {
        Object result = NO_RESULT;
        for(CacheOperationContext context : contexts) {
            if(isConditionPassing(context, result)) {
                Object key = generateKey(context, result);
                org.springframework.cache.Cache.ValueWrapper cached = findInCaches(context, key);
                if(cached != null) {
                    return cached;
                }
            }
        }
        return null;
    }

    private void collectPutRequests(Collection<CacheOperationContext> contexts,
                                    Object result, Collection<CachePutRequest> putRequests) {

        for(CacheOperationContext context : contexts) {
            if(isConditionPassing(context, result)) {
                Object key = generateKey(context, result);
                putRequests.add(new CachePutRequest(context, key));
            }
        }
    }

    private org.springframework.cache.Cache.ValueWrapper findInCaches(CacheOperationContext context, Object key) {
        for(org.springframework.cache.Cache cache : context.getCaches()) {
            org.springframework.cache.Cache.ValueWrapper wrapper = doGet(cache, key);
            if(wrapper != null) {
                return wrapper;
            }
        }
        return null;
    }

    private boolean isConditionPassing(CacheOperationContext context, Object result) {
        boolean passing = context.isConditionPassing(result);
        if(!passing && logger.isTraceEnabled()) {
            logger.trace("Cache condition failed on method " + context.method +
                    " for operation " + context.operation);
        }
        return passing;
    }

    private Object generateKey(CacheOperationContext context, Object result) {
        Object key = context.generateKey(result);
        Assert.notNull(key, "Null key returned for cache operation (maybe you are using named params " +
                "on classes without debug info?) " + context.operation);
        if(logger.isTraceEnabled()) {
            logger.trace("Computed cache key " + key + " for operation " + context.operation);
        }
        return key;
    }


    private class CacheOperationContexts {

        private final MultiValueMap<Class<? extends CacheOperation>, CacheOperationContext> contexts =
                new LinkedMultiValueMap<>();

        public CacheOperationContexts(Collection<? extends CacheOperation> operations,
                                      Method method, Object[] args, Object target) {
            for(CacheOperation operation : operations) {
                this.contexts.add(operation.getClass(), getOperationContext(operation, method, args, target));
            }
        }

        public Collection<CacheOperationContext> get(Class<? extends CacheOperation> operationClass) {
            Collection<CacheOperationContext> result = this.contexts.get(operationClass);
            return (result != null ? result : Collections.<CacheOperationContext>emptyList());
        }
    }


    protected class CacheOperationContext implements CacheOperationInvocationContext<CacheOperation> {

        private final CacheOperation operation;

        private final Method method;

        private final Object[] args;

        private final Object target;

        private final Collection<? extends org.springframework.cache.Cache> caches;


        public CacheOperationContext(CacheOperation operation, Method method,
                                     Object[] args, Object target) {
            this.operation = operation;
            this.method = method;
            this.args = extractArgs(method, args);
            this.target = target;
            this.caches = CacheAspect.this.getCaches(this, cacheResolver);
        }


        @Override
        public CacheOperation getOperation() {
            return operation;
        }

        @Override
        public Object getTarget() {
            return target;
        }

        @Override
        public Method getMethod() {
            return method;
        }

        @Override
        public Object[] getArgs() {
            return args;
        }

        private Object[] extractArgs(Method method, Object[] args) {
            if(!method.isVarArgs()) {
                return args;
            }
            Object[] varArgs = ObjectUtils.toObjectArray(args[args.length - 1]);
            Object[] combinedArgs = new Object[args.length - 1 + varArgs.length];
            System.arraycopy(args, 0, combinedArgs, 0, args.length - 1);
            System.arraycopy(varArgs, 0, combinedArgs, args.length - 1, varArgs.length);
            return combinedArgs;
        }

        /**
         * @see CacheAspectSupport.CacheOperationContext#isConditionPassing(Object)
         */
        protected boolean isConditionPassing(@SuppressWarnings("unused") Object result) {
            //这里直接放过
            return true;
        }

        /**
         * @see CacheAspectSupport.CacheOperationContext#canPutToCache(Object)
         */
        protected boolean canPutToCache(@SuppressWarnings("unused") Object value) {
            //直接放过
            return true;
        }

        protected Object generateKey(@SuppressWarnings("unused") Object result) {
            return ((DomainKeyGenerator) keyGenerator).generateKey(operation.getKey(), this.target, this.method, this.args);
        }

        protected Collection<? extends org.springframework.cache.Cache> getCaches() {
            return this.caches;
        }
    }


    private class CachePutRequest {

        private final CacheOperationContext context;

        private final Object key;

        public CachePutRequest(CacheOperationContext context, Object key) {
            this.context = context;
            this.key = key;
        }

        public void apply(Object result) {
            if(this.context.canPutToCache(result)) {
                for(org.springframework.cache.Cache cache : this.context.getCaches()) {
                    doPut(cache, this.key, result);
                }
            }
        }
    }

    protected Collection<CacheOperation> parseCacheAnnotations(Method method) {
        Collection<CacheOperation> ops = Lists.newArrayList();

        Cacheable cacheable = method.getAnnotation(Cacheable.class);
        if(cacheable != null) {
            ops.add(parseCacheAbleAnnotation(method, cacheable));
        }
        CacheEvict evict = method.getAnnotation(CacheEvict.class);
        if(evict != null) {
            ops.add(parseEvictAnnotation(method, evict));
        }
        CachePut put = method.getAnnotation(CachePut.class);
        if(put != null) {
            ops.add(parsePutAnnotation(method, put));
        }
        Caching caching = method.getAnnotation(Caching.class);
        if(caching != null) {
            ops.addAll(parseCachingAnnotation(method, caching));
        }

        return ops;
    }

    CacheableOperation parseCacheAbleAnnotation(AnnotatedElement ae, Cacheable caching) {
        CacheableOperation op = new CacheableOperation();

        op.setCacheNames(caching.value());
        op.setCondition(caching.condition());
        op.setUnless(caching.unless());
        op.setKey(caching.key());
        op.setKeyGenerator(caching.keyGenerator());
        op.setCacheManager(caching.cacheManager());
        op.setCacheResolver(caching.cacheResolver());
        op.setName(ae.toString());

        return op;
    }

    CacheEvictOperation parseEvictAnnotation(AnnotatedElement ae, CacheEvict caching) {
        CacheEvictOperation op = new CacheEvictOperation();

        op.setCacheNames(caching.value());
        op.setCondition(caching.condition());
        op.setKey(caching.key());
        op.setKeyGenerator(caching.keyGenerator());
        op.setCacheManager(caching.cacheManager());
        op.setCacheResolver(caching.cacheResolver());
        op.setCacheWide(caching.allEntries());
        op.setBeforeInvocation(caching.beforeInvocation());
        op.setName(ae.toString());

        return op;
    }

    CacheOperation parsePutAnnotation(AnnotatedElement ae, CachePut caching) {
        CachePutOperation op = new CachePutOperation();

        op.setCacheNames(caching.value());
        op.setCondition(caching.condition());
        op.setUnless(caching.unless());
        op.setKey(caching.key());
        op.setKeyGenerator(caching.keyGenerator());
        op.setCacheManager(caching.cacheManager());
        op.setCacheResolver(caching.cacheResolver());
        op.setName(ae.toString());

        return op;
    }

    Collection<CacheOperation> parseCachingAnnotation(AnnotatedElement ae, Caching caching) {
        Collection<CacheOperation> ops = Lists.newArrayList();

        Cacheable[] cacheAbles = caching.cacheable();
        if(!ObjectUtils.isEmpty(cacheAbles)) {
            for(Cacheable cacheable : cacheAbles) {
                ops.add(parseCacheAbleAnnotation(ae, cacheable));
            }
        }
        CacheEvict[] evicts = caching.evict();
        if(!ObjectUtils.isEmpty(evicts)) {
            for(CacheEvict evict : evicts) {
                ops.add(parseEvictAnnotation(ae, evict));
            }
        }
        CachePut[] updates = caching.put();
        if(!ObjectUtils.isEmpty(updates)) {
            for(CachePut update : updates) {
                ops.add(parsePutAnnotation(ae, update));
            }
        }

        return ops;
    }
}

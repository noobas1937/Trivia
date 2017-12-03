package com.ecnu.trivia.common.component.cache.web;


import com.ecnu.trivia.common.component.cache.AbstractCache;
import com.ecnu.trivia.common.component.cache.utils.CacheUtils;
import com.ecnu.trivia.common.component.cache.utils.CacheValue;
import com.ecnu.trivia.common.component.listener.ListenerRegister;
import com.ecnu.trivia.common.component.web.servlet.HttpServletContext;
import com.ecnu.trivia.common.component.web.session.SessionCacheAccessor;
import com.ecnu.trivia.common.component.web.session.SessionListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.support.SimpleValueWrapper;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 实现会话级别的缓存
 * 备注：此处使用了与原始的session并不一致的key，目的在于保证获取的数据是通过单次获取来进行的，而不是一次性地将数据全部获取出来(使用hkeys)
 *
 * @author Jack Chen
 */
public class SessionCache extends AbstractCache implements SessionListener
{
  private static final Logger logger = LoggerFactory.getLogger(SessionCache.class);

  private static final int defaultTtl = -1;

  private static final String sessionHashKeyPrefix = "cache:";

  /** 内部使用与线程缓存共用体系 */
  private static ThreadLocal<ConcurrentHashMap<String, Object>> mapThreadLocal = ThreadCache.mapThreadLocal;

  @Resource
  private SessionCacheAccessor sessionCacheAccessor;
  /** 当前过期时间 */
  private int ttl = defaultTtl;

  @Override
  public void onDelete(String sessionId) {
    //当会话结束时，删除自身数据
    sessionCacheAccessor.delete(sessionKey(sessionId));
  }

  @Override
  public void init() {
    ListenerRegister.register(this);
  }

  @Override
  public String getName() {
    return CacheValue.SESSION;
  }

  @Override
  protected String prefix() {
    //不再使用前缀
    return "";
  }

  @Override
  protected String key(Object key) {
    return key == null ? "_NULL" : key.toString();
  }

  private static String sessionKey(String sessionId) {
    return sessionHashKeyPrefix + sessionId;
  }

  @Override
  public Object getNativeCache() {
    return null;
  }

  private HttpSession getSession() {
    HttpServletRequest request = HttpServletContext.getRequestOrDefault(null);
    if(request == null) {
        return null;
    }

    HttpSession session = request.getSession();
    if(ttl == defaultTtl && session != null) {
      ttl = session.getMaxInactiveInterval();
    }

    return session;
  }

  @Override
  public ValueWrapper get(Object key) {
    HttpSession session = getSession();
    if(session == null) {
        return null;
    }

    String strKey = key(key);
    Map<String, Object> threadMap = mapThreadLocal.get();

    Object value = threadMap.get(strKey);
    if(value != null) {
      return toWrapper(value);
    }

    String sessionKey = sessionKey(session.getId());
    value = sessionCacheAccessor.hashGet(sessionKey, strKey);
    if(value != null) {
      threadMap.put(strKey, value);
    }

    return toWrapper(value);
  }

  @Override
  @SuppressWarnings("unchecked")
  public <T> T get(Object key, Class<T> type) {
    HttpSession session = getSession();
    if(session == null) {
        return null;
    }

    String strKey = key(key);
    Map<String, Object> threadMap = mapThreadLocal.get();

    Object value = threadMap.get(strKey);
    if(value != null) {
      return (T) fromStoreValue(value);
    }

    String sessionKey = sessionKey(session.getId());
    value = sessionCacheAccessor.hashGet(sessionKey, strKey);
    if(value != null) {
      threadMap.put(strKey, value);
    }

    return (T) fromStoreValue(value);
  }

  @Override
  public void put(Object key, Object value) {
    HttpSession session = getSession();
    if(session == null) {
        return;
    }

    String strKey = key(key);
    value = toStoreValue(value);

    String sessionKey = sessionKey(session.getId());
    mapThreadLocal.get().put(strKey, value);
    sessionCacheAccessor.hashSet(sessionKey, strKey, value);
    sessionCacheAccessor.expire(sessionKey, ttl);
  }

  @Override
  public ValueWrapper putIfAbsent(Object key, Object value) {
    HttpSession session = getSession();
    if(session == null) {
        return null;
    }

    String strKey = key(key);
    String sessionKey = sessionKey(session.getId());
    Object oldValue = sessionCacheAccessor.hashGet(sessionKey, strKey);

    if(oldValue == null) {
      sessionCacheAccessor.hashSet(sessionKey, strKey, toStoreValue(value));
      sessionCacheAccessor.expire(sessionKey, ttl);
    }

    return toWrapper(oldValue);
  }

  @Override
  public void evict(Object key) {
    HttpSession session = getSession();
    if(session == null) {
        return;
    }

    String strKey = key(key);
    String sessionKey = sessionKey(session.getId());

    mapThreadLocal.get().remove(strKey);
    sessionCacheAccessor.hashDelete(sessionKey, strKey);
  }

  @Override
  public void clear() {
    //不支持
    logger.info("调用了sessionCache清除方法");
  }

  @Override
  protected ValueWrapper toWrapper(Object value) {
    return (value != null ? new SimpleValueWrapper(fromStoreValue(value)) : null);
  }

  @Override
  public boolean isAvailable() {
    return CacheUtils.isJavaEEAvailable();
  }
}

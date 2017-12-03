/** Created by flym at 3/3/2015 */
package com.ecnu.trivia.common.component.web.session;


import com.ecnu.trivia.common.ApplicationContextHolder;
import com.ecnu.trivia.common.component.cache.redis.RedisCacheAccessor;
import com.ecnu.trivia.common.component.listener.ListenerRegister;
import org.springframework.session.ExpiringSession;
import org.springframework.session.MapSession;
import org.springframework.session.SessionRepository;
import org.springframework.util.Assert;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * 实现spring-session中redis存储的策略
 * 与spring-session自带RedisOperationsSessionRepository实现一致，但替换原使用spring-data的部分
 *
 * @author Jack Chen
 * @see org.springframework.session.data.redis.RedisOperationsSessionRepository
 */
public class RedisOperationsSessionRepository implements SessionRepository<RedisOperationsSessionRepository.RedisSession> {
    /**
     * The prefix for each key of the Redis Hash representing a single session. The suffix is the unique session id.
     */
    static final String BOUNDED_HASH_KEY_PREFIX = "spring:session:sessions:";

    /**
     * The key in the Hash representing {@link ExpiringSession#getCreationTime()}
     */
    static final String CREATION_TIME_ATTR = "creationTime";

    /**
     * The key in the Hash representing {@link ExpiringSession#getMaxInactiveIntervalInSeconds()}
     */
    static final String MAX_INACTIVE_ATTR = "maxInactiveInterval";

    /**
     * The key in the Hash representing {@link ExpiringSession#getLastAccessedTime()}
     */
    static final String LAST_ACCESSED_ATTR = "lastAccessedTime";

    /**
     * The prefix of the key for used for session attributes. The suffix is the name of the session attribute. For
     * example, if the session contained an attribute named attributeName, then there would be an entry in the hash named
     * sessionAttr:attributeName that mapped to its value.
     */
    static final String SESSION_ATTR_PREFIX = "sessionAttr:";

    private final RedisSessionExpirationPolicy expirationPolicy;
    private static RedisCacheAccessor cacheAccessor = ApplicationContextHolder.getInstance(SessionCacheAccessor.class);

    /**
     * If non-null, this value is used to override the default value for {@link RedisSession#setMaxInactiveIntervalInSeconds(int)}.
     */
    private Integer defaultMaxInactiveInterval;

    public RedisOperationsSessionRepository() {
        this.expirationPolicy = new RedisSessionExpirationPolicy();
    }

    /**
     * Sets the maximum inactive interval in seconds between requests before newly created sessions will be
     * invalidated. A negative time indicates that the session will never timeout. The default is 1800 (30 minutes).
     *
     * @param defaultMaxInactiveInterval the number of seconds that the {@link org.springframework.session.Session} should be kept alive between
     *                                   client requests.
     */
    public void setDefaultMaxInactiveInterval(int defaultMaxInactiveInterval) {
        this.defaultMaxInactiveInterval = defaultMaxInactiveInterval;
    }

    @Override
    public void save(RedisSession session) {
        session.saveDelta();
    }

    /** 实现一个自定义任务，每分钟执行一次清理操作 TODO */
    public void cleanupExpiredSessions() {
        this.expirationPolicy.cleanExpiredSessions();
    }

    @Override
    public RedisSession getSession(String id) {
        return getSession(id, false);
    }

    /**
     * @param sessionId    the session sessionId
     * @param allowExpired if true, will also include expired sessions that have not been
     *                     deleted. If false, will ensure expired sessions are not
     *                     returned.
     */
    private RedisSession getSession(String sessionId, boolean allowExpired) {
        Map<String, Object> entries = cacheAccessor.hashGet(getKey(sessionId));
        if(entries.isEmpty()) {
            return null;
        }
        MapSession loaded = new MapSession();
        loaded.setId(sessionId);
        for(Map.Entry<String, Object> entry : entries.entrySet()) {
            String key = entry.getKey();
            if(CREATION_TIME_ATTR.equals(key)) {
                loaded.setCreationTime((Long) entry.getValue());
            } else if(MAX_INACTIVE_ATTR.equals(key)) {
                loaded.setMaxInactiveIntervalInSeconds((Integer) entry.getValue());
            } else if(LAST_ACCESSED_ATTR.equals(key)) {
                loaded.setLastAccessedTime((Long) entry.getValue());
            } else if(key.startsWith(SESSION_ATTR_PREFIX)) {
                loaded.setAttribute(key.substring(SESSION_ATTR_PREFIX.length()), entry.getValue());
            }
        }
        if(!allowExpired && loaded.isExpired()) {
            return null;
        }
        RedisSession result = new RedisSession(loaded);
        result.originalLastAccessTime = loaded.getLastAccessedTime() + TimeUnit.SECONDS.toMillis(loaded.getMaxInactiveIntervalInSeconds());
        result.setLastAccessedTime(System.currentTimeMillis());
        return result;
    }

    @Override
    public void delete(String sessionId) {
        ExpiringSession session = getSession(sessionId, true);
        if(session == null) {
            return;
        }

        String key = getKey(sessionId);
        expirationPolicy.onDelete(session);

        // always delete they key since session may be null if just expired

        cacheAccessor.delete(key);

        for(SessionListener sessionListener : ListenerRegister.registeredListener(SessionListener.class)) {
            sessionListener.onDelete(sessionId);
        }
    }

    @Override
    public RedisSession createSession() {
        RedisSession redisSession = new RedisSession();
        if(defaultMaxInactiveInterval != null) {
            redisSession.setMaxInactiveIntervalInSeconds(defaultMaxInactiveInterval);
        }
        return redisSession;
    }

    /**
     * Gets the Hash key for this session by prefixing it appropriately.
     *
     * @param sessionId the session sessionId
     * @return the Hash key for this session by prefixing it appropriately.
     */
    static String getKey(String sessionId) {
        return BOUNDED_HASH_KEY_PREFIX + sessionId;
    }

    /**
     * Gets the key for the specified session attribute
     *
     * @param attributeName 属性key
     */
    static String getSessionAttrNameKey(String attributeName) {
        return SESSION_ATTR_PREFIX + attributeName;
    }

    /**
     * A custom implementation of {@link org.springframework.session.Session} that uses a {@link MapSession} as the basis for its mapping. It keeps
     * track of any attributes that have changed. When
     * {@link org.springframework.session.data.redis.RedisOperationsSessionRepository.RedisSession#saveDelta()} is invoked
     * all the attributes that have been changed will be persisted.
     *
     * @author Rob Winch
     * @since 1.0
     */
    final class RedisSession implements ExpiringSession {
        private final MapSession cached;
        private Long originalLastAccessTime;
        private Map<String, Object> delta = new HashMap<>();

        /**
         * Creates a new instance ensuring to mark all of the new attributes to be persisted in the next save operation.
         */
        RedisSession() {
            this(new MapSession());
            delta.put(CREATION_TIME_ATTR, getCreationTime());
            delta.put(MAX_INACTIVE_ATTR, getMaxInactiveIntervalInSeconds());
            delta.put(LAST_ACCESSED_ATTR, getLastAccessedTime());
        }

        /**
         * Creates a new instance from the provided {@link MapSession}
         *
         * @param cached the {@MapSession} that represents the persisted session that was retrieved. Cannot be null.
         */
        RedisSession(MapSession cached) {
            Assert.notNull("MapSession cannot be null");
            this.cached = cached;
        }

        @Override
        public void setLastAccessedTime(long lastAccessedTime) {
            cached.setLastAccessedTime(lastAccessedTime);
            delta.put(LAST_ACCESSED_ATTR, getLastAccessedTime());
        }

        @Override
        public boolean isExpired() {
            return cached.isExpired();
        }

        @Override
        public long getCreationTime() {
            return cached.getCreationTime();
        }

        @Override
        public String getId() {
            return cached.getId();
        }

        @Override
        public long getLastAccessedTime() {
            return cached.getLastAccessedTime();
        }

        @Override
        public void setMaxInactiveIntervalInSeconds(int interval) {
            cached.setMaxInactiveIntervalInSeconds(interval);
            delta.put(MAX_INACTIVE_ATTR, getMaxInactiveIntervalInSeconds());
        }

        @Override
        public int getMaxInactiveIntervalInSeconds() {
            return cached.getMaxInactiveIntervalInSeconds();
        }

        @Override
        @SuppressWarnings("unchecked")
        public <T> T getAttribute(String attributeName) {
            return (T) cached.getAttribute(attributeName);
        }

        @Override
        public Set<String> getAttributeNames() {
            return cached.getAttributeNames();
        }

        @Override
        public void setAttribute(String attributeName, Object attributeValue) {
            cached.setAttribute(attributeName, attributeValue);
            delta.put(getSessionAttrNameKey(attributeName), attributeValue);
        }

        @Override
        public void removeAttribute(String attributeName) {
            cached.removeAttribute(attributeName);
            delta.put(getSessionAttrNameKey(attributeName), null);
        }

        /**
         * Saves any attributes that have been changed and updates the expiration of this session.
         */
        private void saveDelta() {
            String sessionId = getId();
            String key = getKey(sessionId);
            cacheAccessor.hashSet(key, delta);
            delta = new HashMap<>(delta.size());

            expirationPolicy.onExpirationUpdated(originalLastAccessTime, this);
        }
    }

}

/** Created by flym at 3/3/2015 */
package com.ecnu.trivia.common.component.web.session;


import com.ecnu.trivia.common.ApplicationContextHolder;
import com.ecnu.trivia.common.component.cache.redis.RedisCacheAccessor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.session.ExpiringSession;

import java.util.Calendar;
import java.util.Date;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * 实现原spring-session中 RedisSessionExpirationPolicy部分，但替换原使用spring-data部分
 *
 * @author Jack Chen
 * @see org.springframework.session.data.redis.RedisSessionExpirationPolicy
 */
public class RedisSessionExpirationPolicy
{

    private static final Log logger = LogFactory.getLog(RedisSessionExpirationPolicy.class);

    /**
     * The prefix for each key of the Redis Hash representing a single session. The suffix is the unique session id.
     */
    static final String EXPIRATION_BOUNDED_HASH_KEY_PREFIX = "spring:session:expirations:";

    private static RedisCacheAccessor cacheAccessor = ApplicationContextHolder.getInstance(SessionCacheAccessor.class);

    public RedisSessionExpirationPolicy() {
    }

    public void onDelete(ExpiringSession session) {
        long toExpire = roundUpToNextMinute(expiresInMillis(session));
        String expireKey = getExpirationKey(toExpire);
        cacheAccessor.setDel(expireKey, session.getId());
    }

    public void onExpirationUpdated(Long originalExpirationTimeInMilli, ExpiringSession session) {
        if(originalExpirationTimeInMilli != null) {
            long originalRoundedUp = roundUpToNextMinute(originalExpirationTimeInMilli);
            String expireKey = getExpirationKey(originalRoundedUp);
            cacheAccessor.setDel(expireKey, session.getId());
        }

        long toExpire = roundUpToNextMinute(expiresInMillis(session));

        String expireKey = getExpirationKey(toExpire);
        cacheAccessor.setAdd(expireKey, session.getId());

        int sessionExpireInSeconds = session.getMaxInactiveIntervalInSeconds();
        String sessionKey = getSessionKey(session.getId());

        cacheAccessor.expire(expireKey, sessionExpireInSeconds + 60);
        cacheAccessor.expire(sessionKey, sessionExpireInSeconds);
    }

    private String getExpirationKey(long expires) {
        return EXPIRATION_BOUNDED_HASH_KEY_PREFIX + expires;
    }

    private String getSessionKey(String sessionId) {
        return RedisOperationsSessionRepository.BOUNDED_HASH_KEY_PREFIX + sessionId;
    }

    public void cleanExpiredSessions() {
        long now = System.currentTimeMillis();
        long prevMin = roundDownMinute(now);

        if(logger.isDebugEnabled()) {
            logger.debug("Cleaning up sessions expiring at " + new Date(prevMin));
        }

        String expirationKey = getExpirationKey(prevMin);
        Set<String> sessionsToExpire = cacheAccessor.setValues(expirationKey);
        cacheAccessor.delete(expirationKey);
        for(String session : sessionsToExpire) {
            String sessionKey = getSessionKey(session);
            touch(sessionKey);
        }
    }

    /**
     * By trying to access the session we only trigger a deletion if it the TTL is expired. This is done to handle
     * https://github.com/spring-projects/spring-session/issues/93
     *
     * @param key 缓存健值
     */
    private void touch(String key) {
        cacheAccessor.exists(key);
    }

    static long expiresInMillis(ExpiringSession session) {
        int maxInactiveInSeconds = session.getMaxInactiveIntervalInSeconds();
        long lastAccessedTimeInMillis = session.getLastAccessedTime();
        return lastAccessedTimeInMillis + TimeUnit.SECONDS.toMillis(maxInactiveInSeconds);
    }

    static long roundUpToNextMinute(long timeInMs) {
        Calendar date = Calendar.getInstance();
        date.setTimeInMillis(timeInMs);
        date.add(Calendar.MINUTE, 1);
        date.clear(Calendar.SECOND);
        date.clear(Calendar.MILLISECOND);
        return date.getTimeInMillis();
    }

    static long roundDownMinute(long timeInMs) {
        Calendar date = Calendar.getInstance();
        date.setTimeInMillis(timeInMs);
        date.clear(Calendar.SECOND);
        date.clear(Calendar.MILLISECOND);
        return date.getTimeInMillis();
    }
}

/** Created by Jack Chen at 11/18/2014 */
package com.ecnu.trivia.common.component.web.session;

import com.codahale.metrics.Gauge;
import com.ecnu.trivia.common.ApplicationContextHolder;
import com.ecnu.trivia.common.component.metric.MetricsHolder;
import org.springframework.session.web.http.SessionRepositoryFilter;

import javax.servlet.Filter;

/** @author Jack Chen */
public class SessionRequestFilter extends SessionRepositoryFilter<RedisOperationsSessionRepository.RedisSession> implements Filter {
    public SessionRequestFilter() {
        super(new RedisOperationsSessionRepository());

        //注册相应的会话数监控
        MetricsHolder.METRICS.register(MetricsHolder.name("session"), new Gauge<Integer>() {
            private SessionCacheAccessor sessionCacheAccessor = ApplicationContextHolder.getInstance(SessionCacheAccessor.class);

            @Override
            public Integer getValue() {
                return sessionCacheAccessor.count(RedisOperationsSessionRepository.BOUNDED_HASH_KEY_PREFIX);
            }
        });
    }
}

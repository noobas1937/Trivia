/** Created by Jack Chen at 11/19/2014 */
package com.ecnu.trivia.common.component.web.session;



import com.ecnu.trivia.common.component.cache.redis.FastJsonRedisCacheAccessor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * 用于实现session的缓存策略
 *
 * @author Jack Chen
 */
@Component
public class SessionCacheAccessor extends FastJsonRedisCacheAccessor
{

    @Override
    protected boolean keyIsMD5() {
        //缓存key不需要md5，以方便进行数据查找
        return false;
    }

    @Override
    protected int supportDbIndex() {
        return NO_DB_INDEX;
    }

    @Override
    protected String supportPrefix() {
        return "SESSION_";
    }

    @Value("${cache.redisPool.beanName:shardedJedisPool}")
    @Override
    public void setShardedJedisPoolBeanName(String shardedJedisPoolBeanName) {
        this.shardedJedisPoolBeanName = shardedJedisPoolBeanName;
    }
}

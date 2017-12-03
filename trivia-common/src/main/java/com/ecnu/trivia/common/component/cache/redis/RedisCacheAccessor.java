/** Created by Jack Chen at 3/3/2015 */
package com.ecnu.trivia.common.component.cache.redis;


import com.ecnu.trivia.common.component.cache.CacheAccessor;

import java.util.Set;

/**
 * 基本redis实现的缓存操作
 *
 * @author Jack Chen
 */
public interface RedisCacheAccessor extends CacheAccessor
{
    /** 基本set操作的添加操作 */
    <V> void setAdd(String key, V... values);

    /** 基本set操作的删除操作 */
    <V> void setDel(String key, V... values);

    /** 基本set操作的成员值操作 */
    <V> Set<V> setValues(String key);
}

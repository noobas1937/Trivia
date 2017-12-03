/** Created by Jack Chen at 2014/7/31 */
package com.ecnu.trivia.common.component.cache;

import java.util.List;
import java.util.Map;

/** @author Jack Chen */
public interface CacheAccessor
{
    /**
     * 设置失效期
     *
     * @param ttl 失效的时间秒数
     */
    void expire(String strKey, int ttl);

    /** 获取指定key的失效时间 */
    int expire(String strKey);

    /** 是否存在指定的缓存key */
    boolean exists(String strKey);

    /** 设置值 */
    <T> void put(String strKey, T t);

    /**
     * 设置值，并额外设置失效时间
     *
     * @param ttl 失效的时间秒数
     */
    <T> void put(String strKey, T t, final int ttl);

    /** 获取数据值 */
    <T> T get(String strKey);

    /** 实现hash批量获取操作 */
    <T> Map<String, T> hashGet(String key);

    /** 删除某个key */
    void delete(String strKey);

    /** 实现hash单个获取操作 */
    <T> T hashGet(String key, String fieldKey);

    /** 实现hash单个获取操作 */
    <T> List<T> hashMget(String key, String... fieldKeys);

    /** 实现hash批量设置操作 */
    <T> void hashSet(String key, Map<String, T> map);

    /** 实现hash设置操作 */
    <T> void hashSet(String key, String fieldKey, T value);

    /** 实现hash删除操作 */
    void hashDelete(String key, String... fieldKeys);

    /** 计算某个key前缀的条目数量 */
    int count(String keyPrefix);
}

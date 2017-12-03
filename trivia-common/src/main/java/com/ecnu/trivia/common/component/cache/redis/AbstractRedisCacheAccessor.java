/** Created by Jack Chen at 2014/7/31 */
package com.ecnu.trivia.common.component.cache.redis;

import com.google.common.base.Charsets;
import com.google.common.base.Function;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.ecnu.trivia.common.ApplicationContextHolder;
import com.ecnu.trivia.common.component.serialize.Serializer;
import com.ecnu.trivia.common.util.GzipUtils;
import com.ecnu.trivia.common.util.ObjectUtils;
import org.springframework.util.DigestUtils;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.ShardedJedis;
import redis.clients.jedis.ShardedJedisPool;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/** @author Jack Chen */
public abstract class AbstractRedisCacheAccessor implements RedisCacheAccessor
{
    private static final int GZIP_SIZE = 1024 * 500;//超过500K即进行数据压缩
    protected static final int NO_DB_INDEX = -1;
    private int dbIndex = NO_DB_INDEX;//redis所属的db索引
    private String prefix = "";//数据存储的前缀
    private Serializer serializer;

    protected String shardedJedisPoolBeanName;

    protected AbstractRedisCacheAccessor() {
        this.dbIndex = supportDbIndex();
        this.prefix = supportPrefix();
        this.serializer = supportJsonSerializer();
    }

    private ShardedJedisPool _shardedJedisPool;

    private ShardedJedisPool getShardedJedisPool() {
        if(_shardedJedisPool == null) {
            _shardedJedisPool = (ShardedJedisPool) ApplicationContextHolder.getApplicationContext().getBean(shardedJedisPoolBeanName);
        }

        return _shardedJedisPool;
    }

    /** 支持测试注入 */
    public void setShardedJedisPool(ShardedJedisPool shardedJedisPool) {
        this._shardedJedisPool = shardedJedisPool;
    }

    public byte[] calcKey(String key) {
        if(keyIsMD5()) {
            return (prefix + DigestUtils.md5DigestAsHex(key.getBytes(Charsets.UTF_8))).getBytes(Charsets.UTF_8);
        }
        return (prefix + key).getBytes(Charsets.UTF_8);
    }

    @SuppressWarnings("unchecked")
    private <V> byte[] calcValue(V value) {
        byte[] bytes = serializer.serializeBytes(value);
        if(bytes.length > GZIP_SIZE) {
            bytes = GzipUtils.gzip(bytes);
        }

        return bytes;
    }

    @SuppressWarnings("unchecked")
    private <V> byte[][] calcValues(V... values) {
        byte[][] byteValues = new byte[values.length][];
        for(int i = 0; i < values.length; i++) {
            byteValues[i] = calcValue(values[i]);
        }

        return byteValues;
    }

    /**
     * 保存到redis中的key是否要经过MD5加密，默认true
     */
    protected boolean keyIsMD5() {
        return true;
    }

    /** 是否应该设置此值 */
    protected <T> boolean shouldPut(@SuppressWarnings("unused") String strKey, @SuppressWarnings("unused") T t) {
        return true;
    }

    @Override
    public void expire(String strKey, final int ttl) {
        final byte[] byteKey = calcKey(strKey);
        execute(byteKey, new Function<Jedis, Object>() {
            @Override
            public Object apply(Jedis input) {
                return input.expire(byteKey, ttl);
            }
        });
    }

    @Override
    public int expire(String strKey) {
        final byte[] byteKey = calcKey(strKey);
        return execute(byteKey, new Function<Jedis, Integer>() {
            @Override
            public Integer apply(Jedis input) {
                return input.ttl(byteKey).intValue();
            }
        });
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean exists(String strKey) {
        final byte[] byteKey = calcKey(strKey);
        return execute(byteKey, new Function<Jedis, Boolean>() {
            @Override
            public Boolean apply(Jedis input) {
                return input.exists(byteKey);
            }
        });
    }

    /** 设置值 */
    @Override
    @SuppressWarnings("unchecked")
    public <T> void put(String strKey, T t) {
        if(!shouldPut(strKey, t)) {
            return;
        }

        final byte[] key = calcKey(strKey);
        final byte[] putValue = calcValue(t);

        execute(key, new Function<Jedis, Object>() {
            @Override
            public Object apply(Jedis input) {
                input.set(key, putValue);
                return null;
            }
        });
    }

    /** 设置值，并额外设置失效时间 */
    @Override
    @SuppressWarnings("unchecked")
    public <T> void put(String strKey, T t, final int ttl) {
        if(!shouldPut(strKey, t)) {
            return;
        }

        final byte[] key = calcKey(strKey);
        final byte[] putValue = calcValue(t);

        execute(key, new Function<Jedis, Object>() {
            @Override
            public Object apply(Jedis input) {
                input.set(key, putValue);
                input.expire(key, ttl);
                return null;
            }
        });
    }

    /** 是否应该获取此值 */
    protected boolean shouldGet(@SuppressWarnings("unused") String _) {
        return true;
    }

    /** 获取数据值 */
    @Override
    @SuppressWarnings("unchecked")
    public <T> T get(String strKey) {
        if(!shouldGet(strKey)) {
            return null;
        }

        final byte[] key = calcKey(strKey);
        return execute(key, new Function<Jedis, T>() {
            @Override
            public T apply(Jedis input) {
                byte[] value = input.get(key);
                value = GzipUtils.ungzip(value);
                return (T) serializer.deserialize(value);
            }
        });
    }

    @Override
    public void delete(String strKey) {
        final byte[] key = calcKey(strKey);
        execute(key, new Function<Jedis, Object>() {
            @Override
            public Object apply(Jedis input) {
                input.del(key);
                return null;
            }
        });
    }

    @Override
    public <T> Map<String, T> hashGet(String key) {
        if(!shouldGet(key)) {
            return Maps.newHashMap();
        }
        final byte[] byteKey = calcKey(key);

        return execute(byteKey, new Function<Jedis, Map<String, T>>() {
            @Override
            public Map<String, T> apply(Jedis input) {
                Map<byte[], byte[]> map = input.hgetAll(byteKey);

                Map<String, T> resultMap = Maps.newHashMapWithExpectedSize(map.size());
                for(Map.Entry<byte[], byte[]> e : map.entrySet()) {
                    String eKey = new String(e.getKey(), Charsets.UTF_8);
                    byte[] eValue = GzipUtils.ungzip(e.getValue());
                    if(eValue==null){
                        return null;
                    }
                    T value = (T) serializer.deserialize(eValue);
                    resultMap.put(eKey, value);
                }

                return resultMap;
            }
        });
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T hashGet(String key, final String fieldKey) {
        if(!shouldGet(key)) {
            return null;
        }
        final byte[] byteKey = calcKey(key);
        return execute(byteKey, new Function<Jedis, T>() {
            @Override
            public T apply(Jedis input) {
                byte[] value = input.hget(byteKey, fieldKey.getBytes(Charsets.UTF_8));
                value = GzipUtils.ungzip(value);
                if(value==null){
                    return null;
                }
                return (T) serializer.deserialize(value);
            }
        });
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> List<T> hashMget(String key, String... fieldKeys) {
        if(!shouldGet(key)) {
            return null;
        }
        return execute(key, new Function<Jedis, List<T>>() {
            @Override
            public List<T> apply(Jedis input) {
                List<String> valueList = input.hmget(key, fieldKeys);
                List<T> resultList= Lists.newArrayListWithExpectedSize(valueList.size());
                for(String value:valueList) {
                    if(value!=null) {
                        T tValue = (T) serializer.deserialize(value.getBytes());
                        resultList.add(tValue);
                    }
                }
                return resultList;
            }
        });
    }


    @Override
    @SuppressWarnings("unchecked")
    public <T> void hashSet(String key, final Map<String, T> map) {
        if(!shouldPut(key, map)) {
            return;
        }

        //如果该map为空，则不再处理 hmset不支持 empty map
        if(ObjectUtils.isNullOrEmpty(map)) {
            return;
        }

        final byte[] byteKey = calcKey(key);
        execute(byteKey, new Function<Jedis, Object>() {
            @Override
            public Object apply(Jedis input) {
                Map<byte[], byte[]> putMap = Maps.newHashMapWithExpectedSize(map.size());
                for(Map.Entry<String, T> e : map.entrySet()) {
                    byte[] eKey = e.getKey().getBytes(Charsets.UTF_8);
                    byte[] eValue = calcValue(e.getValue());
                    putMap.put(eKey, eValue);
                }

                input.hmset(byteKey, putMap);

                return null;
            }
        });
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> void hashSet(String key, String fieldKey, final T value) {
        if(!shouldPut(key, value)) {
            return;
        }

        final byte[] byteKey = calcKey(key);
        final byte[] byteFieldKey = fieldKey.getBytes(Charsets.UTF_8);
        execute(byteKey, new Function<Jedis, Object>() {
            @Override
            public Object apply(Jedis input) {
                byte[] bytes = calcValue(value);

                input.hset(byteKey, byteFieldKey, bytes);
                return null;
            }
        });
    }

    @Override
    public void hashDelete(String key, final String... fieldKeys) {
        if(fieldKeys.length == 0) {
            return;
        }
        final byte[] byteKey = calcKey(key);
        execute(byteKey, new Function<Jedis, Object>() {
            @Override
            public Object apply(Jedis input) {
                byte[][] byteFieldKeys = new byte[fieldKeys.length][];
                for(int i = 0; i < fieldKeys.length; i++) {
                    byteFieldKeys[i] = fieldKeys[i].getBytes(Charsets.UTF_8);
                }

                input.hdel(byteKey, byteFieldKeys);
                return null;
            }
        });
    }

    @SuppressWarnings("unchecked")
    @Override
    public <V> void setAdd(String key, final V... values) {
        if(ObjectUtils.isNullOrEmpty(values)) {
            return;
        }

        if(!shouldPut(key, values)) {
            return;
        }

        final byte[] byteKey = calcKey(key);
        execute(byteKey, new Function<Jedis, Void>() {
            @Override
            public Void apply(Jedis input) {
                byte[][] byteValues = calcValues(values);
                input.sadd(byteKey, byteValues);

                return null;
            }
        });
    }

    @SuppressWarnings("unchecked")
    @Override
    public <V> void setDel(String key, final V... values) {
        if(ObjectUtils.isNullOrEmpty(values)) {
            return;
        }

        final byte[] byteKey = calcKey(key);
        execute(byteKey, new Function<Jedis, Void>() {
            @Override
            public Void apply(Jedis input) {
                byte[][] byteValues = calcValues(values);
                input.srem(byteKey, byteValues);

                return null;
            }
        });
    }

    @SuppressWarnings("unchecked")
    @Override
    public <V> Set<V> setValues(String key) {
        if(!shouldGet(key)) {
            return ImmutableSet.of();
        }

        final byte[] byteKey = calcKey(key);
        return execute(byteKey, new Function<Jedis, Set<V>>() {
            @Override
            public Set<V> apply(Jedis input) {
                Set<byte[]> byteValues = input.smembers(byteKey);

                Set<V> valueSet = new HashSet<>();
                for(byte[] byteValue : byteValues) {
                    byteValue = GzipUtils.ungzip(byteValue);
                    valueSet.add((V) serializer.deserialize(byteValue));
                }

                return valueSet;
            }
        });
    }

    @Override
    public int count(String keyPrefix) {
        if(!shouldGet(keyPrefix)) {
            return 0;
        }

        if(!keyPrefix.endsWith("*")) {
            keyPrefix = keyPrefix + "*";
        }
        final byte[] byteKey = calcKey(keyPrefix);
        return execute(byteKey, new Function<Jedis, Integer>() {
            @Override
            public Integer apply(Jedis input) {
                return ((Number) input.eval("return #redis.call('keys',KEYS[1])".getBytes(), 1, byteKey)).intValue();
            }
        });
    }

    private <T> T execute(byte[] key, Function<Jedis, T> function) {
        try(ShardedJedis shardedJedis = getShardedJedisPool().getResource()){
            Jedis jedis = shardedJedis.getShard(key);
            try{
                if(dbIndex != NO_DB_INDEX) {
                    jedis.select(dbIndex);
                }
                return function.apply(jedis);
            } finally {
                if(dbIndex != NO_DB_INDEX) {
                    jedis.select(0);
                }
            }
        }
    }

    private <T> T execute(String key, Function<Jedis, T> function) {
        try(ShardedJedis shardedJedis = getShardedJedisPool().getResource()){
            Jedis jedis = shardedJedis.getShard(key);
            try{
                if(dbIndex != NO_DB_INDEX) {
                    jedis.select(dbIndex);
                }
                return function.apply(jedis);
            } finally {
                if(dbIndex != NO_DB_INDEX) {
                    jedis.select(0);
                }
            }
        }
    }


    protected abstract int supportDbIndex();

    protected abstract String supportPrefix();

    protected abstract Serializer supportJsonSerializer();

    public abstract void setShardedJedisPoolBeanName(String shardedJedisPoolBeanName);
}

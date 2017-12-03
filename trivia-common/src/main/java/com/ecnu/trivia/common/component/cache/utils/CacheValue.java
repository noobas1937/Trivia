/** Created by Jack Chen at 15-7-20 */
package com.ecnu.trivia.common.component.cache.utils;

import com.google.common.collect.ImmutableSet;

import java.util.Set;

/** @author Jack Chen */
public class CacheValue {
    /** 当次请求级别的缓存 */
    public static final String REQUEST = "request";

    /** 当次本地会话级别的缓存 */
    public static final String LOCAL_SESSION = "localSession";

    /** 当次会话级别的缓存 */
    public static final String SESSION = "session";

    /** 当次线程级别的缓存(注，需要在线程结束时清除相应值) */
    public static final String THREAD = "thread";

    /** 当次应用级别的缓存(全局) */
    public static final String APPLICATION = "application";

    /** 当前缓存层的缓存(全局临时) */
    public static final String APPLICATION_TEMP = "applicationTemp";

    /** ehcache实现的主要支持本地文件系统缓存 */
    public static final String LOCAL_FILE = "localFile";

    public static final String CUBE_CACHE = "cube-cache";

    public static final String DIMENSION_CACHE = "dimension-cache";

    public static final String OLAP_CACHE = "olap-cache";

    /** 内置的缓存列表 */
    public static final Set<String> INNER_CACHE_VALUES = ImmutableSet.of(REQUEST, LOCAL_SESSION, SESSION, THREAD, APPLICATION, APPLICATION_TEMP);
}

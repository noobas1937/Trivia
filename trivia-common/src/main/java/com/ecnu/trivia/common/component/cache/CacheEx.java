package com.ecnu.trivia.common.component.cache;

/**
 * Spring Cache扩展接口
 * @author Jack Chen
 * */
public interface CacheEx extends org.springframework.cache.Cache {

    /** 是否可用 */
    boolean isAvailable();

    /** 必要的初始化 */
    void init();
}

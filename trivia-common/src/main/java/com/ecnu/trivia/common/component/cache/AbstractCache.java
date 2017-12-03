package com.ecnu.trivia.common.component.cache;

import org.springframework.cache.Cache;

import java.io.Serializable;
import java.util.Objects;

/**
 * Spring Cache缓存抽象类
 * @author Jack Chen
 *
 **/
public abstract class AbstractCache implements CacheEx {
    private static final Object NULL_HOLDER = new NullHolder();

    @SuppressWarnings("serial")
    public static class NullHolder implements Serializable, Cloneable {
        @Override
        public int hashCode() {
            return -1;
        }

        @Override
        public Object clone() throws CloneNotSupportedException {
            return super.clone();
        }

        @Override
        public boolean equals(Object obj) {
            return this == obj || obj != null && obj.getClass() == getClass();
        }
    }

    /** 前缀 */
    protected abstract String prefix();

    @Override
    public void init() {
        //nothing to do
    }

    protected String key(Object key) {
        return key == null ? prefix() + ":_NULL" : prefix() + ":" + key.toString();
    }

    protected boolean allowNullValues = true;

    protected Object fromStoreValue(Object storeValue) {
        if(this.allowNullValues && Objects.equals(storeValue, NULL_HOLDER)) {
            return null;
        }
        return storeValue;
    }

    protected Object toStoreValue(Object userValue) {
        if(this.allowNullValues && userValue == null) {
            return NULL_HOLDER;
        }
        return userValue;
    }

    protected Cache.ValueWrapper toWrapper(Object value) {
        return (value != null ? new CloneableValueWrapper(fromStoreValue(value)) : null);
    }

}

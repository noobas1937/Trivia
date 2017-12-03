/** Created by Jack Chen at 15-7-29 */
package com.ecnu.trivia.common.component.cache;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.support.SimpleValueWrapper;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Method;

/**
 * 支持对象clone的一个缓存机制
 *
 * @author Jack Chen
 */
public class CloneableValueWrapper extends SimpleValueWrapper {
    private static final Logger logger = LoggerFactory.getLogger(CloneableValueWrapper.class);
    private static Method cloneMethod;

    static {
        cloneMethod = ReflectionUtils.findMethod(Object.class, "clone");
        cloneMethod.setAccessible(true);
    }

    public CloneableValueWrapper(Object value) {
        super(value);
    }

    @Override
    public Object get() {
        Object object = super.get();

        if(object instanceof Cloneable) {
            return ReflectionUtils.invokeMethod(cloneMethod, object);
        }

        logger.warn("clone缓存,当前对象不支持clone方法:{}", object);

        return object;
    }
}

/** Created by Jack Chen at 11/13/2014 */
package com.ecnu.trivia.common.component.exception;

import com.ecnu.trivia.common.exception.IRCloudException;
import com.ecnu.trivia.common.util.StringUtils;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/** @author Jack Chen */
public class Asserts {

    private static <T extends IRCloudException> void throwException(Class<T> clazz, String message) {
        try {
            Constructor<T> constructor = clazz.getConstructor(String.class);
            throw constructor.newInstance(message);
        } catch(InvocationTargetException | NoSuchMethodException | InstantiationException | IllegalAccessException e) {
            throw new RuntimeException("初始化NewBI异常失败:" + clazz, e);
        }
    }

    /** 逻辑断言，如果指定的条件不为true时，则抛出IRCloudException */
    public static void assertTrue(boolean condition, String message, Object... objs) {
        assertTrue(condition, IRCloudException.class, message, objs);
    }

    /** 逻辑断言，如果指定的条件不为true时，则抛出IRCloudException */
    public static void assertTrue(boolean condition, ExceptionMessage exceptionMessage, Object... objs) {
        assertTrue(condition, IRCloudException.class, exceptionMessage, objs);
    }

    /** 业务断言，如果指定的条件不为true时，则抛出指定类型的断言异常 */
    public static <T extends IRCloudException> void assertTrue(boolean condition, Class<T> clazz, String message, Object... objs) {
        if(!condition) {
            message = StringUtils.format(message, objs);
            throwException(clazz, message);
        }
    }

    public static <T extends IRCloudException> void assertTrue(boolean condition, Class<T> clazz, ExceptionMessage exceptionMessage, Object... objs) {
        if(!condition) {
            String message = exceptionMessage.toMessage(objs);
            throwException(clazz, message);
        }
    }
}

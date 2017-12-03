/** Created by Jack Chen at 2015/6/25 */
package com.ecnu.trivia.common.component.listener;

import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;
import com.ecnu.trivia.common.component.test.TestConfig;

import java.util.List;

/**
 * 维护整个系统内的事件监听器注册信息
 *
 * @author Jack Chen
 */
public class ListenerRegister {
    private static Multimap<Class<? extends Listener>, Listener> registerMap = MultimapBuilder.hashKeys().arrayListValues().build();

    /**
     * 清除所有数据
     * 仅在测试环境下使用
     */
    public static void clear() {
        if(TestConfig.getInstance().isTestFlag()) {
            registerMap.clear();
        }
    }

    /** 注册相应的监听器 */
    @SuppressWarnings("unchecked")
    public static void register(Listener listener) {
        //避免重新注册
        if(registerMap.containsValue(listener)) {
            return;
        }

        Class<?> clazz = listener.getClass();
        Class[] interfaces = clazz.getInterfaces();
        for(Class<?> interfaceClass : interfaces) {
            if(Listener.class.isAssignableFrom(interfaceClass)) {
                _register((Class<? extends Listener>) interfaceClass, listener);
            }
        }
    }

    @SuppressWarnings("unchecked")
    private static void _register(Class<? extends Listener> interfaceClass, Listener listener) {
        if(interfaceClass == Listener.class) {
            return;
        }

        registerMap.put(interfaceClass, listener);

        for(Class<?> extendInterfaceClass : interfaceClass.getInterfaces()) {
            _register((Class<? extends Listener>) extendInterfaceClass, listener);
        }
    }

    /** 获取注册指定接口的所有监听器列表 */
    public static <T extends Listener> List<T> registeredListener(Class<T> listenerInterface) {
        //返回新构建的数据，避免外部修改相应的数据
        return Lists.newArrayList((List<T>) registerMap.get(listenerInterface));
    }
}

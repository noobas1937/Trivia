package com.ecnu.trivia.common.component.listener;

import com.google.common.collect.Lists;
import com.ecnu.trivia.common.component.listener.listenerregister.ListenerA;
import com.ecnu.trivia.common.component.listener.listenerregister.ListenerAB;
import com.ecnu.trivia.common.component.listener.listenerregister.ListenerB;
import com.ecnu.trivia.common.component.test.testng.AssertEx;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.List;

/**
 * 测试对于监听器的注册支持
 * Created by Jack Chen at 2015/6/25
 */
public class ListenerRegisterTest {

    @DataProvider
    public Object[][] p4testRegisteredListener() {
        List<Object[]> objectsList = Lists.newArrayList();
        final int paramLength = 3;
        Object[] objects;

        //准备3类接口 A，B，AB其中AB继承于A，B
        //准备实现类 A1 A2 B1 B2 AB1 AB2
        ListenerA listenerA1 = new ListenerA() {
        };
        ListenerA listenerA2 = new ListenerA() {
        };
        ListenerB listenerB1 = new ListenerB() {
        };
        @SuppressWarnings("unused")
        ListenerB listenerB2 = new ListenerB() {
        };
        ListenerAB listenerAB1 = new ListenerAB() {
        };
        @SuppressWarnings("unused")
        ListenerAB listenerAB2 = new ListenerAB() {
        };

        //1 添加1个监听器，能够获取相应的列表
        objects = new Object[paramLength];
        objects[0] = Lists.newArrayList(listenerA1);
        objects[1] = ListenerA.class;
        objects[2] = Lists.newArrayList(listenerA1);
        objectsList.add(objects);

        //2 添加2个同类型的监听器，能够获取2个实现类
        objects = new Object[paramLength];
        objects[0] = Lists.newArrayList(listenerA1, listenerA2);
        objects[1] = ListenerA.class;
        objects[2] = Lists.newArrayList(listenerA1, listenerA2);
        objectsList.add(objects);

        //3 添加2个不同类型的监听器，根据1个接口只能获取1个实现类
        objects = new Object[paramLength];
        objects[0] = Lists.newArrayList(listenerA1, listenerB1);
        objects[1] = ListenerA.class;
        objects[2] = Lists.newArrayList(listenerA1);
        objectsList.add(objects);

        //4 添加1个监听器，根据不同类型不能获取到数据
        objects = new Object[paramLength];
        objects[0] = Lists.newArrayList(listenerA1);
        objects[1] = ListenerB.class;
        objects[2] = Lists.newArrayList();
        objectsList.add(objects);

        //5 添加共有实现，根据父类接口，能够获取到1个数据
        objects = new Object[paramLength];
        objects[0] = Lists.newArrayList(listenerAB1);
        objects[1] = ListenerA.class;
        objects[2] = Lists.newArrayList(listenerAB1);
        objectsList.add(objects);

        //6 添加1个监听器，1个共有实现监听器(同时实现了前者接口)，根据其中 父类接口，能够获取到2个数据
        objects = new Object[paramLength];
        objects[0] = Lists.newArrayList(listenerA1, listenerAB1);
        objects[1] = ListenerA.class;
        objects[2] = Lists.newArrayList(listenerA1, listenerAB1);
        objectsList.add(objects);

        return objectsList.toArray(new Object[objectsList.size()][]);
    }

    /** 测试获取相应的监听器信息 */
    @Test(dataProvider = "p4testRegisteredListener")
    public <T extends Listener> void testRegisteredListener(List<Listener> preRegisterList, Class<T> listenerClass, List<T> expectListenerList) throws Exception {
        //清除之前的数据，以保证测试
        ListenerRegister.clear();

        for(Listener listener : preRegisterList)
            ListenerRegister.register(listener);

        List<T> valueList = ListenerRegister.registeredListener(listenerClass);

        AssertEx.assertEqualsNoOrder(valueList, expectListenerList);
    }
}

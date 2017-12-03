package com.ecnu.trivia.common.util;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.ecnu.trivia.common.component.test.testng.AssertEx;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.List;
import java.util.Map;

public class ListUtilsTest {

    @DataProvider
    public Object[][] p4testAsList() {
        List<Object[]> objectsList = Lists.newArrayList();
        final int paramLength = 3;
        Object[] objects;

        //1 正常数据,将map中的kv转换为连接字符串
        objects = new Object[paramLength];
        Map<String, Integer> map = Maps.newHashMap();
        map.put("_username", -1);
        map.put("_password", 2);
        objects[0] = map;
        objects[1] = new Function<Map.Entry<String, Integer>, String>() {
            public String apply(Map.Entry<String, Integer> input) {
                return input.getKey() + "-" + input.getValue();
            }
        };
        objects[2] = Lists.newArrayList("_username--1", "_password-2");
        objectsList.add(objects);

        return objectsList.toArray(new Object[objectsList.size()][]);
    }

    @Test(dataProvider = "p4testAsList")
    public <T, K, V> void testAsList(Map<K, V> map, Function<Map.Entry<K, V>, T> tFunction, List<T> expectList) {
        List<T> result = ListUtils.asList(map, tFunction);

        AssertEx.assertEqualsNoOrder(result, expectList);
    }
}

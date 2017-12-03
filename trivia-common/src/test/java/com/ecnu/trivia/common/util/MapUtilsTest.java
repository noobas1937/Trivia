package com.ecnu.trivia.common.util;

import com.google.common.base.Function;
import com.google.common.base.Functions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.ecnu.trivia.common.util.maputils.TestT;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.beans.PropertyDescriptor;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class MapUtilsTest {


    @DataProvider
    public Object[][] p4testAsMap2KV() {
        List<Object[]> objectsList = Lists.newArrayList();

        //正常转换
        Object[] objects = new Object[4];
        List<String> strList = ImmutableList.of("a", "b", "c", "d");
        Function<String, String> keyFunction = new Function<String, String>() {
            @Override
            public String apply(String input) {
                return input + "Key";
            }
        };
        Function<String, String> valueFunction = new Function<String, String>() {
            @Override
            public String apply(String input) {
                return input + "Value";
            }
        };
        Map<String, String> resultMap = ImmutableMap
                .of("aKey", "aValue", "bKey", "bValue", "cKey", "cValue", "dKey", "dValue");
        objects[0] = strList;
        objects[1] = keyFunction;
        objects[2] = valueFunction;
        objects[3] = resultMap;
        objectsList.add(objects);

        return objectsList.toArray(new Object[objectsList.size()][]);
    }

    /** 测试 asMap(Collection<X> xCollection, Function<X, K> keyFunction, Function<X, V> valueFunction) */
    @Test(dataProvider = "p4testAsMap2KV")
    public void testAsMap2KV(Collection collection, Function<Object, Object> keyFunction, Function valueFunction, Map expectValue) throws Exception {
        Map resultMap = MapUtils.asMap(collection, keyFunction, valueFunction);
        Assert.assertEquals(resultMap, expectValue);
    }

    @DataProvider
    public Object[][] p4testAsMapFromObject() {
        List<Object[]> objectsList = Lists.newArrayList();
        final int paramLength = 4;
        Object[] objects;

        //1 正常的数据信息 使用testT,不处理原value, keyFunction使用默认的获取属性值F
        Date date = new Date();
        TestT testT = new TestT(-1, "username", "password", date);
        Function<PropertyDescriptor, Object> keyFunction = new Function<PropertyDescriptor, Object>() {
            public Object apply(PropertyDescriptor input) {
                return input.getName();
            }
        };
        Function<Object, Object> valueFunction = Functions.identity();
        Map<String, Object> valueMap = ImmutableMap.<String, Object>of("id", -1L, "name", "username", "password", "password", "date", date);
        objects = new Object[paramLength];
        objects[0] = testT;
        objects[1] = keyFunction;
        objects[2] = valueFunction;
        objects[3] = valueMap;
        objectsList.add(objects);

        //2 将testT中password置为null，将不会生成null
        testT = new TestT(-2, "username", null, date);
        valueMap = ImmutableMap.<String, Object>of("id", -2L, "name", "username", "date", date);
        objects = new Object[paramLength];
        objects[0] = testT;
        objects[1] = keyFunction;
        objects[2] = valueFunction;
        objects[3] = valueMap;
        objectsList.add(objects);

        //3 使用keyF在前面追加key,valueF使用toString，在前面追加value
        testT = new TestT(-3, "user1", "password1", null);
        valueMap = ImmutableMap.<String, Object>of("keyid", "value-3", "keyname", "valueuser1", "keypassword", "valuepassword1");
        keyFunction = new Function<PropertyDescriptor, Object>() {
            public Object apply(PropertyDescriptor input) {
                return "key" + input.getName();
            }
        };
        valueFunction = new Function<Object, Object>() {
            public Object apply(Object input) {
                return "value" + input.toString();
            }
        };
        objects = new Object[paramLength];
        objects[0] = testT;
        objects[1] = keyFunction;
        objects[2] = valueFunction;
        objects[3] = valueMap;
        objectsList.add(objects);

        return objectsList.toArray(new Object[objectsList.size()][]);
    }

    /** 测试 asMap(Object instance,keyFunction, valueFunction */
    @Test(dataProvider = "p4testAsMapFromObject")
    public <T, K, V> void testAsMapFromObject(T instance, Function<PropertyDescriptor, K> keyFunction, Function<Object, V> valueFunction, Map<K, V> expectValue) {
        Map<K, V> value = MapUtils.asMap(instance, keyFunction, valueFunction);

        Assert.assertEquals(value, expectValue);
    }
}

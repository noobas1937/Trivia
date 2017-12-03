package com.ecnu.trivia.common.util;

import com.google.common.collect.Lists;
import com.ecnu.trivia.common.util.domaincsvutils.Value;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.List;

public class DomainCsvUtilsTest {

    @DataProvider
    public Object[][] p4testGet() {
        List<Object[]> objectsList = Lists.newArrayList();
        final int paramLength = 3;
        Object[] objects;

        //1 v1 正常的数据 valid为false, 时间为无时分秒
        objects = new Object[paramLength];
        objects[0] = "V1";
        objects[1] = Value.class;
        objects[2] = new Value(-1, "username1", "password1", false, DateUtils.parseDate("2014-12-12"));
        objectsList.add(objects);

        //2 v2 正常的数据 valid为true, 时间带时分秒
        objects = new Object[paramLength];
        objects[0] = "V2";
        objects[1] = Value.class;
        objects[2] = new Value(-2, "username2", "password2", true, DateUtils.parseDateTime("2014-12-12 12:12:12"));
        objectsList.add(objects);

        //3 V3 带双引号，及逗号的数据
        objects = new Object[paramLength];
        objects[0] = "V3";
        objects[1] = Value.class;
        objects[2] = new Value(-3, "abc,efg", "pass\"word\"", true, DateUtils.parseDate("2015-01-01"));
        objectsList.add(objects);

        return objectsList.toArray(new Object[objectsList.size()][]);
    }

    /** 测试根据key获取数据的方法 */
    @Test(dataProvider = "p4testGet")
    public <T> void testGet(String key, Class<T> clazz, T expectValue) {
        T value = DomainCsvUtils.get(key, clazz);

        Assert.assertEquals(value, expectValue);
    }
}

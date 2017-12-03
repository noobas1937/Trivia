package com.ecnu.trivia.common.util;

import com.google.common.collect.Lists;
import com.ecnu.trivia.common.component.test.testng.AssertEx;
import com.ecnu.trivia.common.component.test.testng.BaseTest;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.List;


public class PropertiesUtilsTest extends BaseTest {
    @DataProvider
    public Object[][] p4testResolveEmbeddedValue() {
        List<Object[]> objectsList = Lists.newArrayList();

        //1 正常场景
        Object[] objects = new Object[2];
        objects[0] = "${jdbc.driverClassName}";
        objects[1] = "com.mysql.jdbc.Driver";
        objectsList.add(objects);

        //默认值，如果属性文件中不存在，去默认值
        objects = new Object[2];
        objects[0] = "${jdbc.driverClassName1:com.mysql.jdbc.Driver1}";
        objects[1] = "com.mysql.jdbc.Driver1";
        objectsList.add(objects);


        return objectsList.toArray(new Object[objectsList.size()][]);
    }

    @Test(dataProvider = "p4testResolveEmbeddedValue")
    public void testResolveEmbeddedValue(String key, String expected) {
        String value = PropertiesUtils.resolveEmbeddedValue(key);
        AssertEx.assertEquals(value, expected);

    }

    @DataProvider
    public Object[][] p4testGetProperty() {
        List<Object[]> objectsList = Lists.newArrayList();

        //1 正常场景
        Object[] objects = new Object[3];
        objects[0] = "jdbc.driverClassName";
        objects[1] = null;
        objects[2] = "com.mysql.jdbc.Driver";
        objectsList.add(objects);

        //默认值，如果属性文件中不存在，去默认值
        objects = new Object[3];
        objects[0] = "jdbc.driverClassName1";
        objects[1] = "com.mysql.jdbc.Driver1";
        objects[2] = "com.mysql.jdbc.Driver1";
        objectsList.add(objects);


        return objectsList.toArray(new Object[objectsList.size()][]);
    }

    @Test(dataProvider = "p4testGetProperty")
    public void testGetProperty(String key, String defaultValue, String expected) {
        String value = PropertiesUtils.getProperty(key, defaultValue);
        AssertEx.assertEquals(value, expected);

    }


}

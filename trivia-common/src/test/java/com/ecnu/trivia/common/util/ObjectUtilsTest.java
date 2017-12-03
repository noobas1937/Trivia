package com.ecnu.trivia.common.util;

import com.google.common.collect.Lists;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.List;

public class ObjectUtilsTest {

    @DataProvider
    public Object[][] p4testIsNullOrEmpty() {
        List<Object[]> objectsList = Lists.newArrayList();

        //1 空对象
        Object[] objects = new Object[1];
        objects[0] = null;
        objectsList.add(objects);

        //2 空字符串 双引号
        objects = new Object[1];
        objects[0] = "";
        objectsList.add(objects);

        //3 空字符串 空格串
        objects = new Object[1];
        objects[0] = "  ";
        objectsList.add(objects);

        //4 空数组,基本对象
        objects = new Object[1];
        objects[0] = new int[0];
        objectsList.add(objects);

        //5 空数组 对象
        objects = new Object[1];
        objects[0] = new String[0];
        objectsList.add(objects);

        //6 空集合
        objects = new Object[1];
        objects[0] = Lists.newArrayList();
        objectsList.add(objects);

        return objectsList.toArray(new Object[objectsList.size()][]);
    }

    @Test(dataProvider = "p4testIsNullOrEmpty")
    public void testIsNullOrEmpty(Object input) throws Exception {
        Assert.assertTrue(ObjectUtils.isNullOrEmpty(input));
    }

    @DataProvider
    public Object[][] p4testIsNotNullOrEmpty() {
        List<Object[]> objectsList = Lists.newArrayList();

        //1 普通对象,不为null
        Object[] objects = new Object[1];
        objects[0] = new Object();
        objectsList.add(objects);

        //2 不是空的字符串
        objects = new Object[1];
        objects[0] = "   _";
        objectsList.add(objects);

        //3 不是空的数组
        objects = new Object[1];
        objects[0] = new Object[]{null, new Object()};
        objectsList.add(objects);

        //4 不是空的集合
        objects = new Object[1];
        objects[0] = Lists.newArrayList(new Object());
        objectsList.add(objects);

        return objectsList.toArray(new Object[objectsList.size()][]);
    }

    /** 测试ObjectUtils.isNullOrEmpty 应该返回false的情况 */
    @Test(dataProvider = "p4testIsNotNullOrEmpty")
    public void testIsNotNullOrEmpty(Object input) throws Exception {
        Assert.assertFalse(ObjectUtils.isNullOrEmpty(input));
    }

    @DataProvider
    public Object[][] p4testIsLogicalNull() {
        List<Object[]> objectsList = Lists.newArrayList();
        int paramLength = 1;
        Object[] objects;

        //1 之前用于测试 isNullOrEmpty的数值
        objectsList.addAll(Lists.newArrayList(p4testIsNullOrEmpty()));

        //2 数值0
        objects = new Object[paramLength];
        objects[0] = 0;
        objectsList.add(objects);

        objects = new Object[paramLength];
        objects[0] = 0L;
        objectsList.add(objects);

        //3 空的数组
        objects = new Object[paramLength];
        objects[0] = new String[]{"", "   ", null};
        objectsList.add(objects);

        //4 空的集合
        objects = new Object[paramLength];
        objects[0] = Lists.newArrayList(null, 0, "", Lists.newArrayList());
        objectsList.add(objects);

        return objectsList.toArray(new Object[objectsList.size()][]);
    }

    /** 测试ObjectUtils.isLocalNull 应该返回true的情况 */
    @Test(dataProvider = "p4testIsLogicalNull")
    public void testIsLogicalNull(Object input) throws Exception {
        Assert.assertTrue(ObjectUtils.isLogicalNull(input));
    }

    @DataProvider
    public Object[][] p4testIsNotLogicalNull() {
        List<Object[]> objectsList = Lists.newArrayList();
        final int paramLength = 1;
        Object[] objects;

        //1 原isNotLogicalNull的所有参数
        objectsList.addAll(Lists.newArrayList(p4testIsNotNullOrEmpty()));

        //1 正常的数字 1 -1 等
        objects = new Object[paramLength];
        objects[0] = 1;
        objectsList.add(objects);

        objects = new Object[paramLength];
        objects[0] = -1;
        objectsList.add(objects);

        //2 true false等 boolean 不应该是逻辑false的
        objects = new Object[paramLength];
        objects[0] = false;
        objectsList.add(objects);

        objects = new Object[paramLength];
        objects[0] = true;
        objectsList.add(objects);

        //3 正常的有值数组
        objects = new Object[paramLength];
        objects[0] = new Object[]{"", null, "  _"};
        objectsList.add(objects);

        //4 正常的有值集合
        objects = new Object[paramLength];
        objects[0] = Lists.newArrayList(null, "", false);
        objectsList.add(objects);

        return objectsList.toArray(new Object[objectsList.size()][]);
    }

    @Test(dataProvider = "p4testIsNotLogicalNull")
    public void testIsNotLogicalNull(Object input) throws Exception {
        Assert.assertFalse(ObjectUtils.isLogicalNull(input));
    }

    @DataProvider
    public Object[][] p4testObject2Bytes() {
        List<Object[]> objectsList = Lists.newArrayList();
        //设置参数长度大小。
        final int paramLength = 2;

        Object[] objects = new Object[paramLength];
        //给参数设置值。
        Integer a = new Integer("2");
        objects[0] = a;
        objects[1] = ObjectUtils.object2Bytes(a);

        //将参数放到集合中，可以放置多组参数。
        objectsList.add(objects);
        return objectsList.toArray(new Object[objectsList.size()][]);
    }

    @Test(dataProvider = "p4testObject2Bytes")
    public void testObject2Bytes(Integer value, byte[] expect) throws Exception {
        Assert.assertEquals(ObjectUtils.object2Bytes(value), expect);
    }



    @DataProvider
    public Object[][] p4testBytes2Object() {
        List<Object[]> objectsList = Lists.newArrayList();
        //设置参数长度大小。
        final int paramLength = 2;

        Object[] objects = new Object[paramLength];
        //给参数设置值。
        Integer a = new Integer("2");
        objects[0] = ObjectUtils.object2Bytes(a);
        objects[1] = a;

        //将参数放到集合中，可以放置多组参数。
        objectsList.add(objects);
        return objectsList.toArray(new Object[objectsList.size()][]);
    }

    @Test(dataProvider = "p4testBytes2Object")
    public void testBytes2Object(byte[] value, Integer expect) throws Exception {
        Integer result = ObjectUtils.bytes2Object(value);
        Assert.assertEquals(result, expect);
    }

    @DataProvider
    public Object[][] p4testContaions() {
        List<Object[]> objectsList = Lists.newArrayList();
        //设置参数长度大小。
        final int paramLength = 3;

        //测试数据存在时返回true
        Object[] objects1 = new Object[paramLength];
        Object[] param1 = {1,2,3};
        Object value1 = 1;
        objects1[0] = param1;
        objects1[1] = value1;
        objects1[2] = true;

        //测试数据存在时返回false
        Object[] objects2 = new Object[paramLength];
        Object value2 = 4;
        objects2[0] = param1;
        objects2[1] = value2;
        objects2[2] = false;

        //将参数放到集合中，可以放置多组参数。
        objectsList.add(objects1);
        objectsList.add(objects2);
        return objectsList.toArray(new Object[objectsList.size()][]);
    }

    /**
     * 测试一个对象是否存在另一个集合中。
     */
    @Test(dataProvider = "p4testContaions")
    public void testContaions(Object[] param, Object value, boolean expect){
        boolean result = ObjectUtils.contaions(param, value);
        Assert.assertEquals(result, expect);
    }

}

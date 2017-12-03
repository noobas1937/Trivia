package com.ecnu.trivia.common.util;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.ecnu.trivia.common.component.test.testng.AssertEx;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.List;

public class StringUtilsTest {

    @DataProvider
    public Object[][] p4testFormat() {
        List<Object[]> objectsList = Lists.newArrayList();
        //无参数时 返回原字符串
        Object[] objects = new Object[3];
        objects[0] = "相应的原数字信息";
        objects[1] = null;
        objects[2] = objects[0];
        objectsList.add(objects);

        //有参数时，占位相应的占位符
        Object param1 = 1L;
        Object param2 = "子串";
        Object param3 = Object.class;
        Object param4 = ImmutableList.of("集合对象1", "集合对象2");
        Object param5 = null;
        objects = new Object[3];
        objects[0] = "占位一数字{},占位二字符串{},占位三类对象{},占位四普通对象{},占位五NULL{}";
        objects[1] = new Object[]{param1, param2, param3, param4, param5};
        objects[2] =
                "占位一数字" + param1.toString() + ",占位二字符串" + param2.toString() + ",占位三类对象" + param3.toString() + ",占位四普通对象" + param4.toString() + ",占位五NULL" + String.valueOf(param5);
        objectsList.add(objects);

        //有转义符时，不处理转义符
        objects = new Object[3];
        objects[0] = "转义符\\{,转义符\\},转义符\\{},转义符\\{\\},占位{}";
        objects[1] = new Object[]{"1"};
        objects[2] = "转义符\\{,转义符\\},转义符{},转义符\\{\\},占位1";
        objectsList.add(objects);

        return objectsList.toArray(new Object[objectsList.size()][]);
    }

    @Test(dataProvider = "p4testFormat")
    public void testFormat(String formatStr, Object[] args, String expectValue) throws Exception {
        String value = StringUtils.format(formatStr, args);
        Assert.assertEquals(value, expectValue);
    }

    @DataProvider
    public Object[][] p4testSplitByComma() {
        List<Object[]> objectsList = Lists.newArrayList();
        final int paramLength = 2;
        Object[] objects;

        //1 正常的数组
        objects = new Object[paramLength];
        objects[0] = "a,b,c,d";
        objects[1] = new String[]{"a", "b", "c", "d"};
        objectsList.add(objects);

        //2 末尾带空信息
        objects = new Object[paramLength];
        objects[0] = "a,b,,";
        objects[1] = new String[]{"a", "b", "", ""};
        objectsList.add(objects);

        //3 头带空信息
        objects = new Object[paramLength];
        objects[0] = ",,a,b,";
        objects[1] = new String[]{"", "", "a", "b", ""};
        objectsList.add(objects);

        return objectsList.toArray(new Object[objectsList.size()][]);
    }

    /** 测试根据逗号进行分隔 */
    @Test(dataProvider = "p4testSplitByComma")
    public void testSplitByComma(String src, String[] expectValue) throws Exception {
        String[] value = StringUtils.splitByComma(src);

        AssertEx.assertEquals(value, expectValue);
    }

    @DataProvider
    public Object[][] p4testSplitByAnd() {
        List<Object[]> objectsList = Lists.newArrayList();
        final int paramLength = 2;
        Object[] objects;

        //1 正常的数组
        objects = new Object[paramLength];
        objects[0] = "a&b&c&d";
        objects[1] = new String[]{"a", "b", "c", "d"};
        objectsList.add(objects);

        //2 末尾带空信息
        objects = new Object[paramLength];
        objects[0] = "a&b&&";
        objects[1] = new String[]{"a", "b", "", ""};
        objectsList.add(objects);

        //3 头带空信息
        objects = new Object[paramLength];
        objects[0] = "&&a&b&";
        objects[1] = new String[]{"", "", "a", "b", ""};
        objectsList.add(objects);

        return objectsList.toArray(new Object[objectsList.size()][]);
    }

    /** 测试根据&分隔 */
    @Test(dataProvider = "p4testSplitByAnd")
    public void testSplitByAnd(String src, String[] expectValue) throws Exception {
        String[] result = StringUtils.splitByAnd(src);

        Assert.assertEquals(result, expectValue);
    }

    @DataProvider
    public Object[][] p4testSplitByEq() {
        List<Object[]> objectsList = Lists.newArrayList();
        final int paramLength = 2;
        Object[] objects;

        //1 正常的数组
        objects = new Object[paramLength];
        objects[0] = "a=b";
        objects[1] = new String[]{"a", "b"};
        objectsList.add(objects);

        //2 末尾带空信息
        objects = new Object[paramLength];
        objects[0] = "a=b==";
        objects[1] = new String[]{"a", "b", "", ""};
        objectsList.add(objects);

        //3 头带空信息
        objects = new Object[paramLength];
        objects[0] = "==a=b=";
        objects[1] = new String[]{"", "", "a", "b", ""};
        objectsList.add(objects);

        return objectsList.toArray(new Object[objectsList.size()][]);
    }

    /** 测试根据=分隔 */
    @Test(dataProvider = "p4testSplitByEq")
    public void testSplitByEq(String src, String[] expectValue) throws Exception {
        String[] result = StringUtils.splitByEq(src);

        AssertEx.assertEquals(result, expectValue);
    }

    @DataProvider
    public Object[][] p4testSplitByDot() {
        List<Object[]> objectsList = Lists.newArrayList();
        final int paramLength = 2;
        Object[] objects;

        //1 正常的数组
        objects = new Object[paramLength];
        objects[0] = "a.b.c.d";
        objects[1] = new String[]{"a", "b", "c", "d"};
        objectsList.add(objects);

        //2 末尾带空信息
        objects = new Object[paramLength];
        objects[0] = "a.b..";
        objects[1] = new String[]{"a", "b", "", ""};
        objectsList.add(objects);

        //3 头带空信息
        objects = new Object[paramLength];
        objects[0] = "..a.b.";
        objects[1] = new String[]{"", "", "a", "b", ""};
        objectsList.add(objects);

        return objectsList.toArray(new Object[objectsList.size()][]);
    }

    /** 测试根据.分隔 */
    @Test(dataProvider = "p4testSplitByDot")
    public void testSplitByDot(String src, String[] expectValue) throws Exception {
        String[] result = StringUtils.splitByDot(src);

        Assert.assertEquals(result, expectValue);
    }

    @DataProvider
    public Object[][] p4testSplitByColon() {
        List<Object[]> objectsList = Lists.newArrayList();
        final int paramLength = 2;
        Object[] objects;

        //1 正常的数组
        objects = new Object[paramLength];
        objects[0] = "a:b:c:d";
        objects[1] = new String[]{"a", "b", "c", "d"};
        objectsList.add(objects);

        //2 末尾带空信息
        objects = new Object[paramLength];
        objects[0] = "a:b::";
        objects[1] = new String[]{"a", "b", "", ""};
        objectsList.add(objects);

        //3 头带空信息
        objects = new Object[paramLength];
        objects[0] = "::a:b:";
        objects[1] = new String[]{"", "", "a", "b", ""};
        objectsList.add(objects);

        return objectsList.toArray(new Object[objectsList.size()][]);
    }

    /** 测试根据:分隔 */
    @Test(dataProvider = "p4testSplitByColon")
    public void testSplitByColon(String src, String[] expectValue) throws Exception {
        String[] result = StringUtils.splitByColon(src);

        Assert.assertEquals(result, expectValue);
    }

    /**
     * 测试字符a包含在字符串集合中。
     */
    @Test
    public void testContains() {
        String param = "a";
        boolean result = StringUtils.contains(param, "a", "b", "c");
        Assert.assertTrue(result);
    }

    @DataProvider
    public Object[][] p4testRemoveQuot() {
        List<Object[]> objectsList = Lists.newArrayList();
        final int paramLength = 2;
        Object[] objects;

        //1 两边有双引号，去除之
        objects = new Object[paramLength];
        objects[0] = "\"abc\"";
        objects[1] = "abc";
        objectsList.add(objects);

        //2 两边有单引号，去除之
        objects = new Object[paramLength];
        objects[0] = "'abcdef'fgh'";
        objects[1] = "abcdef'fgh";//仍保留中间的单引号
        objectsList.add(objects);

        //3 无引号，保持原样
        objects = new Object[paramLength];
        objects[0] = "abc\"e'd";
        objects[1] = "abc\"e'd";
        objectsList.add(objects);

        //4 有一边引号，去除之
        objects = new Object[paramLength];
        objects[0] = "\"abc'd'";
        objects[1] = "abc'd";
        objectsList.add(objects);

        return objectsList.toArray(new Object[objectsList.size()][]);
    }

    /** 测试移除引号 */
    @Test(dataProvider = "p4testRemoveQuot")
    public void testRemoveQuot(String src, String expectValue) throws Exception {
        String result = StringUtils.removeQuot(src);

        Assert.assertEquals(result, expectValue);
    }

    @DataProvider
    public Object[][] p4testRemoveMiddleBracket() {
        List<Object[]> objectsList = Lists.newArrayList();
        final int paramLength = 2;
        Object[] objects;

        //1 两边有中括号，去除之
        objects = new Object[paramLength];
        objects[0] = "[中文测试]";
        objects[1] = "中文测试";
        objectsList.add(objects);

        //2 无中括号，保存原样
        objects = new Object[paramLength];
        objects[0] = "中文[]测试";//中间的不去除
        objects[1] = "中文[]测试";
        objectsList.add(objects);

        //3 只有一边有中括号，仍去除之
        objects = new Object[paramLength];
        objects[0] = "[中文测试";
        objects[1] = "中文测试";
        objectsList.add(objects);

        //4 中括号是反向的，不会去除
        objects = new Object[paramLength];
        objects[0] = "]中文测试[";
        objects[1] = "]中文测试[";
        objectsList.add(objects);

        return objectsList.toArray(new Object[objectsList.size()][]);
    }

    /** 测试移除中括号 */
    @Test(dataProvider = "p4testRemoveMiddleBracket")
    public void testRemoveMiddleBracket(String src, String expectValue) throws Exception {
        String result = StringUtils.removeMiddleBracket(src);

        Assert.assertEquals(result, expectValue);
    }

    @DataProvider
    public Object[][] p4testToString() {
        List<Object[]> objectsList = Lists.newArrayList();
        final int paramLength = 2;
        Object[] objects;

        //1 null值，返回''
        objects = new Object[paramLength];
        objects[0] = null;
        objects[1] = "";
        objectsList.add(objects);

        //2 非null值，返回原 toString实现
        objects = new Object[paramLength];
        objects[0] = 12345;
        objects[1] = "12345";
        objectsList.add(objects);

        return objectsList.toArray(new Object[objectsList.size()][]);
    }

    /** 测试toString方法 */
    @Test(dataProvider = "p4testToString")
    public void testToString(Object obj, String expectValue) throws Exception {
        String result = StringUtils.toString(obj);

        Assert.assertEquals(result, expectValue);
    }

    @DataProvider
    public Object[][] p4testCount() {
        List<Object[]> objectsList = Lists.newArrayList();
        final int paramLength = 3;
        Object[] objects;

        //1 正常的字符串及字符判定
        objects = new Object[paramLength];
        objects[0] = "abcd,e,f,g";
        objects[1] = ',';
        objects[2] = 3;//有3个逗号
        objectsList.add(objects);

        return objectsList.toArray(new Object[objectsList.size()][]);
    }

    /** 测试字符数计算 */
    @Test(dataProvider = "p4testCount")
    public void testCount(String str, char c, int expectCount) throws Exception {
        int count = StringUtils.count(str, c);

        Assert.assertEquals(count, expectCount);
    }

    @DataProvider
    public Object[][] p4testSubstring() {
        List<Object[]> objectsList = Lists.newArrayList();
        final int paramLength = 4;
        Object[] objects;

        //1 按字符，截取，数目为2，应该返回2个逗号之后的数据
        objects = new Object[paramLength];
        objects[0] = "abc,ef,gh,ijk,lmn";
        objects[1] = ',';
        objects[2] = 2;
        objects[3] = "gh,ijk,lmn";
        objectsList.add(objects);

        //2 按字符,截取，数目为10，超过字符串中字符数，应该返回null
        objects = new Object[paramLength];
        objects[0] = "abc,ef,gh,ijk,lmn";
        objects[1] = ',';
        objects[2] = 10;
        objects[3] = null;
        objectsList.add(objects);

        return objectsList.toArray(new Object[objectsList.size()][]);
    }

    /** 测试字符串按字符数截取 */
    @Test(dataProvider = "p4testSubstring")
    public void testSubstring(String src, char c, int count, String expectValue) throws Exception {
        String result = StringUtils.substring(src, c, count);

        Assert.assertEquals(result, expectValue);
    }

    @DataProvider
    public Object[][] p4testMultiply() {
        List<Object[]> objectsList = Lists.newArrayList();
        final int paramLength = 3;
        Object[] objects;

        //1 1次
        objects = new Object[paramLength];
        objects[0] = "abc";
        objects[1] = 1;
        objects[2] = "abc";
        objectsList.add(objects);

        //2 2次
        objects = new Object[paramLength];
        objects[0] = "abcd";
        objects[1] = 2;
        objects[2] = "abcdabcd";
        objectsList.add(objects);

        return objectsList.toArray(new Object[objectsList.size()][]);
    }

    /** 测试字符串多次组合 */
    @Test(dataProvider = "p4testMultiply")
    public void testMultiply(String str, int factor, String expectValue) throws Exception {
        String result = StringUtils.multiply(str, factor);

        Assert.assertEquals(result, expectValue);
    }
}

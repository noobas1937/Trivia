package com.ecnu.trivia.common.component.json;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.ecnu.trivia.common.component.json.format.JsonFormat;
import com.ecnu.trivia.common.component.json.jsonutils.*;
import com.ecnu.trivia.common.component.test.testng.AssertEx;
import com.ecnu.trivia.common.frameworkext.FastJsonAsmFactoryModifier;
import com.ecnu.trivia.common.util.DateUtils;
import jdk.nashorn.internal.ir.annotations.Ignore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.Date;
import java.util.List;

public class JsonUtilsTest {
    private static final Logger logger = LoggerFactory.getLogger(JsonUtilsTest.class);

    static {
        //保证修改器作作用
        new FastJsonAsmFactoryModifier().postProcessBeanFactory(null);
    }

    /** 测试 parseToObject方法 */
    @Test(dataProvider = "p4testParse")
    public static void testParse(String json, Object expectValue) {
        Object value = JsonUtils.parse(json);

        Assert.assertEquals(value, expectValue);
    }

    /** 测试 parse(String,Class) 方法 */
    @Test(dataProvider = "p4testParseToClazz")
    public static <T> void testParseToClazz(String json, Class<T> clazz, T expectValue) {
        T value = JsonUtils.parse(json, clazz);

        Assert.assertEquals(value, expectValue);
    }

    /** 测试 parse(String,Class,Object) 方法 */
    @Ignore
    @Test(dataProvider = "p4testParseToClazzAndDefault")
    public static <T> void testParseToClazzAndDefault(String json, Class<T> clazz, T defaultValue, T expectValue) {
        T value = JsonUtils.parse(json, clazz, defaultValue);

        Assert.assertEquals(value, expectValue);
    }

    @Test(dataProvider = "p4testToBrowserJson")
    public static void testToBrowserJson(UserDomain user, String[] limits, String[] expectContains, String[] expectNotContains) {
        String json = JsonUtils.toBrowserJson(user, limits);

        logger.debug("解析json为:" + json);

        for(String expectContain : expectContains) {
            Assert.assertTrue(json.contains(expectContain), "未包括指定的标记:" + expectContain + ",json:" + json);
        }

        for(String expectNotContain : expectNotContains) {
            Assert.assertTrue(!json.contains(expectNotContain), "包括了应该被排除的标记:" + expectNotContain + ",json:" + json);
        }
    }

    private static String _toUnicode(String s) {
        //假定相应的数据均为中文
        StringBuilder builder = new StringBuilder();
        for(char c : s.toCharArray()) {
            builder.append("\\u").append(Integer.toHexString(c).toUpperCase());
        }

        return builder.toString();
    }

    /** 测试基于jsonFormat的测试 */
    @Test(dataProvider = "p4testToBrowserJsonWithFormat")
    public static void testToBrowserJsonWithFormat(Object user, JsonFormat[] jsonLimits, String[] limits, String[] expectContains, String[] expectNotContains) {
        String json = JsonUtils.toBrowserJson(user, jsonLimits, limits);

        logger.debug("解析json为:" + json);

        for(String expectContain : expectContains) {
            Assert.assertTrue(json.contains(expectContain), "未包括指定的标记:" + expectContain + ",json:" + json);
        }

        for(String expectNotContain : expectNotContains) {
            Assert.assertTrue(!json.contains(expectNotContain), "包括了应该被排除的标记:" + expectNotContain + ",json:" + json);
        }
    }

    @DataProvider
    public Object[][] p4testParse() {
        List<Object[]> objectsList = Lists.newArrayList();
        final int paramLength = 2;
        Object[] objects;

        //1 转换为JsonObject
        objects = new Object[paramLength];
        objects[0] = "{\"username\":\"_username\",\"password\":\"_password\"}";
        objects[1] = new JSONObject(ImmutableMap.<String, Object>of("username", "_username", "password", "_password"));
        objectsList.add(objects);

        //2 转换为指定的对象,在原json中有@type标识
        objects = new Object[paramLength];
        objects[0] = "{\"@type\":\"User\",\"username\":\"_username\",\"password\":\"_password\"}";
        objects[1] = new User("_username", "_password");
        objectsList.add(objects);

        return objectsList.toArray(new Object[objectsList.size()][]);
    }

    @DataProvider
    public Object[][] p4testParseToClazz() {
        List<Object[]> objectsList = Lists.newArrayList();
        final int paramLength = 3;
        Object[] objects;

        //1 正常的数据
        String username = "_username";
        String password = "_pasword";
        objects = new Object[paramLength];
        objects[0] = "{\"username\":\"" + username + "\",\"password\":\"" + password + "\"}";
        objects[1] = User.class;
        objects[2] = new User(username, password);
        objectsList.add(objects);

        return objectsList.toArray(new Object[objectsList.size()][]);
    }

    @DataProvider
    public Object[][] p4testParseToClazzAndDefault() {
        List<Object[]> objectsList = Lists.newArrayList();
        final int paramLength = 4;
        Object[] objects;

        //1 默认值 null，在解析失败的情况下
        objects = new Object[paramLength];
        objects[0] = "_";
        objects[1] = User.class;
        objects[2] = null;
        objects[3] = null;
        objectsList.add(objects);

        //2 默认值 指定的user，在解析失败的情况下
        User user = new User("_username", "_password");
        objects = new Object[paramLength];
        objects[0] = "_";
        objects[1] = User.class;
        objects[2] = user;
        objects[3] = user;
        objectsList.add(objects);

        //3 解析成功，返回正确的数据
        objects = new Object[paramLength];
        String username = "_username";
        String password = "_pasword";
        objects[0] = "{\"username\":\"" + username + "\",\"password\":\"" + password + "\"}";
        objects[1] = User.class;
        objects[2] = null;
        objects[3] = new User(username, password);
        objectsList.add(objects);

        return objectsList.toArray(new Object[objectsList.size()][]);
    }

    @DataProvider
    public Object[][] p4testToBrowserJson() {
        List<Object[]> objectsList = Lists.newArrayList();
        final int paramLength = 4;
        Object[] objects;

        //1 正常的数据
        UserDomain user = new UserDomain("_username", "_password");
        user.attr("_a", "a");
        user.attr("_b", 1L);
        user.attr("_username", 1L);
        user.attr("_c", Lists.newArrayList("a", "b", "c"));
        objects = new Object[paramLength];
        objects[0] = user;
        objects[1] = null;
        objects[2] = new String[]{"_username"};//包括attr中的属性
        objects[3] = new String[]{"attr"};//不包括attr
        objectsList.add(objects);

        //2 针对limit测试
        user = new UserDomain("_username", "_password");
        user.attr("_username", 1L);
        objects = new Object[paramLength];
        objects[0] = user;
        objects[1] = new String[]{"username"};//限制只输出用户名
        objects[2] = new String[]{"_username"};//应该在限制下仍包括attr属性
        objects[3] = new String[]{"password"};//不再包括password属性
        objectsList.add(objects);

        //3 针对limit正则匹配
        user = new UserDomain("_username", "_password");
        user.attr("_username", 1L);
        user.setProperty(new UserDomainProperty(2));
        objects = new Object[paramLength];
        objects[0] = user;
        objects[1] = new String[]{"property.*", "username"};//限制只输出property下的id信息
        objects[2] = new String[]{"_username", "id"};//应该在限制下仍包括attr属性,包括property下的id属性
        objects[3] = new String[]{"password"};//不再包括password属性
        objectsList.add(objects);

        //4 针对特定的*.id正则匹配
        user = new UserDomain("_username", "_password");
        user.attr("_user", 1L);
        user.setProperty(new UserDomainProperty(2));
        objects = new Object[paramLength];
        objects[0] = user;
        objects[1] = new String[]{"*.id", "password"};//限制只输出property下的id信息,及输出password属性
        objects[2] = new String[]{"id", "_user"};//应该在限制下仍包括attr属性,包括property下的id属性
        objects[3] = new String[]{"username"};//不再包括username属性
        objectsList.add(objects);

        return objectsList.toArray(new Object[objectsList.size()][]);
    }

    @SuppressWarnings("unused")
    @JsonFormat("date")
    private void _p4testToBrowserJsonWithFormat1() {
    }

    @SuppressWarnings("unused")
    @JsonFormat(value = "date", time = true)
    private void _p4testToBrowserJsonWithFormat2() {
    }

    @SuppressWarnings("unused")
    @JsonFormat(value = "date", time = false)
    private void _p4testToBrowserJsonWithFormat3_1() {
    }

    @SuppressWarnings("unused")
    @JsonFormat(value = "userDate", time = false)
    private void _p4testToBrowserJsonWithFormat3_2() {
    }

    @DataProvider
    public Object[][] p4testToBrowserJsonWithFormat() throws Exception {
        List<Object[]> objectsList = Lists.newArrayList();
        final int paramLength = 5;
        Object[] objects;

        //1 准备1个数据，单独测试jsonFormat，能够输出特定的时间要求
        objects = new Object[paramLength];
        objects[0] = new DateUser("_username", "_password", new Date());
        objects[1] = new JsonFormat[]{JsonUtilsTest.class.getDeclaredMethod("_p4testToBrowserJsonWithFormat1").getAnnotation(JsonFormat.class)};
        objects[2] = new String[0];
        objects[3] = new String[]{_toUnicode("今天")}; //应该包括今天
        objects[4] = new String[]{DateUtils.formatDate(new Date())};//不应该包括类似今天的日期格式化串
        objectsList.add(objects);

        //2 准备1个数据，测试jsonFormat和limit，能够输出指定的要求数据
        objects = new Object[paramLength];
        DateUser dateUser = new DateUser("_username", "_password", org.apache.commons.lang3.time.DateUtils.addDays(new Date(), -1));//给出数据为昨天的数据;
        objects[0] = dateUser;
        objects[1] = new JsonFormat[]{JsonUtilsTest.class.getDeclaredMethod("_p4testToBrowserJsonWithFormat2").getAnnotation(JsonFormat.class)};
        objects[2] = new String[]{"username", "date"};
        objects[3] = new String[]{_toUnicode("昨天"), DateUtils.formatTime(dateUser.getDate()), "username", "_username"};//包含昨天,时间的格式化信息, 以及相应的username的格式化信息
        objects[4] = new String[]{"password", "_password"};//不包括password等属性
        objectsList.add(objects);

        //3 准备2个数据，测试jsonFormat和limit,能够输出指定的要求数据
        objects = new Object[paramLength];
        dateUser = new DateUser("_username", "_password", org.apache.commons.lang3.time.DateUtils.addDays(new Date(), 1));//给出明天的数据
        DateUserDomain dateUserDomain = new DateUserDomain("_username2", "_password2", org.apache.commons.lang3.time.DateUtils.addDays(new Date(), 4));//给出4天之后的数据，即需要直接相应的格式化
        objects[0] = Lists.newArrayList(dateUser, dateUserDomain);
        objects[1] = new JsonFormat[]{JsonUtilsTest.class.getDeclaredMethod("_p4testToBrowserJsonWithFormat3_1").getAnnotation(JsonFormat.class), JsonUtilsTest.class.getDeclaredMethod("_p4testToBrowserJsonWithFormat3_2").getAnnotation(JsonFormat.class)};
        objects[2] = new String[]{"username", "date", "userDate"};
        objects[3] = new String[]{_toUnicode("明天"), DateUtils.formatDate(dateUserDomain.getUserDate()), "_username2"};//包含相应的时间信息，以及包含的username信息
        objects[4] = new String[]{"_password", "_password2", DateUtils.formatTime(dateUser.getDate()), DateUtils.formatTime(dateUserDomain.getUserDate())};//不包括password及时间信息
        objectsList.add(objects);

        return objectsList.toArray(new Object[objectsList.size()][]);
    }

    @DataProvider
    public Object[][] p4testParse2List() {
        List<Object[]> objectsList = Lists.newArrayList();
        final int paramLength = 3;
        Object[] objects;

        //1 普通的字符串对象，能够进行反序列化
        objects = new Object[paramLength];
        objects[0] = "[1,2,3,0]";
        objects[1] = Integer.class;
        objects[2] = Lists.newArrayList(1, 2, 3, 0);
        objectsList.add(objects);

        //2 javaBean对象，能够进行反序列化 使用user对象进行处理
        objects = new Object[paramLength];
        objects[0] = "[{\"username\":\"用户名1\"},{\"password\":\"密码2\",\"username\":\"用户名2\"}]";
        objects[1] = User.class;
        User user1 = new User("用户名1", null);
        User user2 = new User("用户名2", "密码2");
        objects[2] = Lists.newArrayList(user1, user2);
        objectsList.add(objects);

        return objectsList.toArray(new Object[objectsList.size()][]);
    }

    @Test(dataProvider = "p4testParse2List")
    @SuppressWarnings("unchecked")
    public void testParse2List(String json, Class<?> clazz, List<Object> expectValueList) throws Exception {
        List<Object> resultList = (List) JsonUtils.parse2List(json, clazz);

        AssertEx.assertEqualsNoOrder(resultList, expectValueList);
    }
}

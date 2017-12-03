package com.ecnu.trivia.common.util;

import com.google.common.base.Charsets;
import com.google.common.collect.ImmutableMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Map;

public class HttpClientUtilsTest {
    private static final Logger logger = LoggerFactory.getLogger(HttpClientUtilsTest.class);

    /** 测试 doGet方法 */
    @Test
    public void testDoGet() throws IOException {
        //保证不会出错
        String url = "http://www.baidu.com/s";//baidu搜索地址
        String keyword = "xyz";
        Map<String, String> param = ImmutableMap.of("wd", keyword);//搜索关键字
        Charset charset = Charsets.UTF_8;//编码为utf8编码

        String result = HttpClientUtils.doGet(url, param, charset);

//        logger.debug("testDoGet返回数据:{}", result);
        //返回的数据中包含搜索词即可
        Assert.assertTrue(result.contains(keyword));
    }

    /** 测试 doPost方法 */
    @Test
    public void testDoPost() throws IOException {
        //保证不会出错
        String url = "https://passport.baidu.com/v2/api/?login";
        Map<String, String> param = ImmutableMap.of("username", "a_bcd1234@163.com", "password", "_wrongpassword");//随机的用户名密码
        Charset charset = Charsets.UTF_8;

        String result = HttpClientUtils.doPost(url, param, charset);

//        logger.debug("testDoPost返回数据:{}", result);
        //返回的数据有值
        Assert.assertTrue(result != null);
    }
}

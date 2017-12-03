package com.ecnu.trivia.common.util;

import com.google.common.base.Charsets;
import com.google.common.collect.Lists;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.util.List;

public class DigestUtilsTest {

    @DataProvider
    public Object[][] p4testHmacSha1Hex() throws Exception {
        List<Object[]> objectsList = Lists.newArrayList();
        final int paramLength = 3;
        Object[] objects;

        //1 正常的准备数据，期望值使用jdk自带算法获取
        objects = new Object[paramLength];
        String secret = "0123456789abcdefghij";
        String message = "Newbi测试中文123456";
        objects[0] = secret;
        objects[1] = message;

        //采用jdk算法获取数值
        SecretKey secretKey = new SecretKeySpec(secret.getBytes(Charsets.UTF_8), "HmacSHA1");
        Mac mac = Mac.getInstance(secretKey.getAlgorithm());
        mac.init(secretKey);
        byte[] bytes = mac.doFinal(message.getBytes(Charsets.UTF_8));
        String hex = byte2hex(bytes);
        objects[2] = hex;

        objectsList.add(objects);

        return objectsList.toArray(new Object[objectsList.size()][]);
    }

    private static String byte2hex(byte[] bytes) {
        StringBuilder sign = new StringBuilder();
        for(byte aByte : bytes) {
            String hex = Integer.toHexString(aByte & 0xFF);
            if(hex.length() == 1) {
                sign.append("0");
            }
            sign.append(hex);
        }
        return sign.toString();
    }

    /** 测试hmacSha1Hex结果，查看是否与预期的一致 */
    @Test(dataProvider = "p4testHmacSha1Hex")
    public void testHmacSha1Hex(String secret, String message, String expectValue) {
        String value = DigestUtils.hmacSha1Hex(secret, message);

        Assert.assertEquals(value, expectValue);
    }

    @DataProvider
    public Object[][] p4testMd5Hex() throws Exception {
        List<Object[]> objectsList = Lists.newArrayList();
        final int paramLength = 2;
        Object[] objects;

        //1 正常的数据准备，结果采用jdk算法实现
        String message = "NewBIcommon实现md5Hex实现";
        objects = new Object[paramLength];
        objects[0] = message;

        MessageDigest md5 = MessageDigest.getInstance("MD5");
        byte[] bytes = md5.digest(message.getBytes(Charsets.UTF_8));
        String hex = byte2hex(bytes);
        objects[1] = hex;

        objectsList.add(objects);

        return objectsList.toArray(new Object[objectsList.size()][]);
    }

    /** 测试md5Hex结果，查看是否与预期的一致 */
    @Test(dataProvider = "p4testMd5Hex")
    public void testMd5Hex(String message, String expectValue) throws Exception {
        String value = DigestUtils.md5Hex(message);

        Assert.assertEquals(value, expectValue);
    }

    @DataProvider
    public Object[][] p4testMd5() throws Exception {
        List<Object[]> objectsList = Lists.newArrayList();
        final int paramLength = 2;
        Object[] objects;

        //1 正常的数据准备，结果采用jdk算法实现
        String message = "NewBIcommon实现md5Hex实现";
        objects = new Object[paramLength];
        objects[0] = message;

        MessageDigest md5 = MessageDigest.getInstance("MD5");
        byte[] bytes = md5.digest(message.getBytes(Charsets.UTF_8));
        objects[1] = bytes;

        objectsList.add(objects);

        return objectsList.toArray(new Object[objectsList.size()][]);
    }

    /** 测试md5方法 */
    @Test(dataProvider = "p4testMd5")
    public void testMd5(String message, byte[] expectValue) throws Exception {
        byte[] value = DigestUtils.md5(message);

        Assert.assertEquals(value, expectValue);
    }

    @DataProvider
    public Object[][] p4testEncodeBase64() {
        List<Object[]> objectsList = Lists.newArrayList();
        final int paramLength = 3;
        Object[] objects;

        //准备数据 正常的数据，采用预先计算方法得出值
        objects = new Object[paramLength];
        String message = "NewBI编码测试encodeBase64";
        objects[0] = message;
        objects[1] = Charsets.UTF_8;
        objects[2] = "TmV3QknnvJbnoIHmtYvor5VlbmNvZGVCYXNlNjQ=";//采用在线查值得出
        objectsList.add(objects);

        return objectsList.toArray(new Object[objectsList.size()][]);
    }

    /** 测试Base64编码 */
    @Test(dataProvider = "p4testEncodeBase64")
    public void testEncodeBase64(String message, Charset charset, String expectValue) throws Exception {
        String value = DigestUtils.encodeBase64(message, charset);

        Assert.assertEquals(value, expectValue);
    }

    @DataProvider
    public Object[][] p4testDecodeBase64() {
        List<Object[]> objectsList = Lists.newArrayList();
        final int paramLength = 3;
        Object[] objects;

        //准备数据 正确的数据，采用预先计算方法得出值
        objects = new Object[paramLength];
        String message = "TmV3QknnvJbnoIHmtYvor5VlbmNvZGVCYXNlNjQ=";
        objects[0] = message;
        objects[1] = Charsets.UTF_8;
        objects[2] = "NewBI编码测试encodeBase64";
        objectsList.add(objects);

        return objectsList.toArray(new Object[objectsList.size()][]);
    }

    /** 测试对base64解码 */
    @Test(dataProvider = "p4testDecodeBase64")
    public void testDecodeBase64(String message, Charset charset, String expectValue) throws Exception {
        String value = DigestUtils.decodeBase64(message, charset);

        Assert.assertEquals(value, expectValue);
    }
}

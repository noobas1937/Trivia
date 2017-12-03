package com.ecnu.trivia.common.util;

import com.google.common.base.Charsets;
import com.google.common.collect.Lists;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.List;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

/** Created by Jack Chen at 15-11-13 */
public class CryptoUtilsTest {

    @DataProvider
    public Object[][] p4testEncryptAndDecrypt() {
        List<Object[]> objectsList = Lists.newArrayList();
        final int paramLength = 2;
        Object[] objects;

        //1 正常的数据信息
        objects = new Object[paramLength];
        objects[0] = "abcdefg1234efg".getBytes();
        objects[1] = "password";
        objectsList.add(objects);

        //2 带中文的数组
        objects = new Object[paramLength];
        objects[0] = "中文测试NewBI.io".getBytes();
        objects[1] = "newbipassword.yunat.com";
        objectsList.add(objects);

        //3 超长的密码(超过50位)
        objects = new Object[paramLength];
        objects[0] = "中文测试NewBI.io".getBytes();
        objects[1] = "newbipassword.yunat.com_________________1234234234____________________________newbi.com_Jack Chen.yao";
        objectsList.add(objects);

        return objectsList.toArray(new Object[objectsList.size()][]);
    }

    /** 测试加密和解密的联合应用,采用byte[] */
    @Test(dataProvider = "p4testEncryptAndDecrypt")
    public void testEncryptAndDecrypt(byte[] source, String password) {
        byte[] bytes = CryptoUtils.aesEncrypt(password, source);
        assertNotNull(bytes);

        bytes = CryptoUtils.aesDecrypt(password, bytes);
        assertNotNull(bytes);

        //验证必须与原来相同
        assertEquals(bytes, source);
    }

    @DataProvider
    public Object[][] p4testEncryptAndDecryptString() {
        List<Object[]> objectsList = Lists.newArrayList();
        final int paramLength = 2;
        Object[] objects;

        //1 正常的数据信息
        objects = new Object[paramLength];
        objects[0] = "abcdefg1234efg";
        objects[1] = "password";
        objectsList.add(objects);

        //2 带中文的数组
        objects = new Object[paramLength];
        objects[0] = "中文测试NewBI.io";
        objects[1] = "newbipassword.yunat.com";
        objectsList.add(objects);

        //3 超长的密码(超过50位)
        objects = new Object[paramLength];
        objects[0] = "中文测试NewBI.io";
        objects[1] = "newbipassword.yunat.com_________________1234234234____________________________newbi.com_Jack Chen.yao";
        objectsList.add(objects);

        return objectsList.toArray(new Object[objectsList.size()][]);
    }

    /** 测试加密和解密的联合应用,采用字符串 */
    @Test(dataProvider = "p4testEncryptAndDecryptString")
    public void testEncryptAndDecryptString(String source, String password) {
        String message = CryptoUtils.aesBase64EncryptHex(password, source, Charsets.UTF_8);
        assert !ObjectUtils.isLogicalNull(message);

        message = CryptoUtils.aesHexDecryptBase64(password, message, Charsets.UTF_8);
        assert !ObjectUtils.isLogicalNull(message);

        //验证必须与原来相同
        assertEquals(message, source);
    }
}

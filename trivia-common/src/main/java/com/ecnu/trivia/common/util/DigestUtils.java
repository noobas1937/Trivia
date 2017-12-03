/** Created by Jack Chen at 12/5/2014 */
package com.ecnu.trivia.common.util;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.HmacUtils;

import java.nio.charset.Charset;

/**
 * 摘要类工具
 *
 * @author Jack Chen
 */
public class DigestUtils {

    /**
     * 对指定的message使用secret进行hmacsha1运算，并返回相应的hex值
     * 字符串使用utf8编码
     */
    public static String hmacSha1Hex(String secret, String message) {
        return HmacUtils.hmacSha1Hex(secret, message);
    }

    /** 对指定的message进行md5处理，并返回相应的hex值 */
    public static String md5Hex(String message) {
        return org.apache.commons.codec.digest.DigestUtils.md5Hex(message);
    }

    /** 对指定的message进行md5处理，并返回相应的byte数组 */
    public static byte[] md5(String message) {
        return org.apache.commons.codec.digest.DigestUtils.md5(message);
    }

    /** 对指定的message进行base64编码 */
    public static String encodeBase64(String message, Charset charset) {
        return new String(Base64.encodeBase64(message.getBytes(charset), false), charset);
    }

    /** 对指定的message进行base64解码 */
    public static String decodeBase64(String message, Charset charset) {
        return new String(Base64.decodeBase64(message.getBytes(charset)), charset);
    }
}

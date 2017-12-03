package com.ecnu.trivia.web.utils.encode;

import gherkin.deps.net.iharder.Base64;

import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.spec.PKCS8EncodedKeySpec;

/**
 * @description 签名
 * @author Jack Chen
 * @date 2017/12/2
 */
public class Signaturer {

    public static byte[] sign(byte[] priKeyText, String plainText) {

        try {

            PKCS8EncodedKeySpec priPKCS8 = new PKCS8EncodedKeySpec(Base64.decode(priKeyText));
            KeyFactory keyf = KeyFactory.getInstance("RSA");
            PrivateKey prikey = keyf.generatePrivate(priPKCS8);

            // 用私钥对信息生成数字签名
            Signature signet = java.security.Signature.getInstance("MD5withRSA");
            signet.initSign(prikey);
            signet.update(plainText.getBytes());
            byte[] signed = Base64.encodeBytesToBytes(signet.sign());
            return signed;

        } catch (java.lang.Exception e) {
            System.out.println("签名失败");
            e.printStackTrace();

        }

        return null;

    }

}

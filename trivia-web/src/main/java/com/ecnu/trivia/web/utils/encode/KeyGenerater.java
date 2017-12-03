package com.ecnu.trivia.web.utils.encode;

import java.security.*;
import java.util.Base64;

/**
 * @description Key 生成
 * @author Jack Chen
 * @date 2017/12/2
 */
public class KeyGenerater {

    private byte[] priKey;

    private byte[] pubKey;

    public static void main(String[] args) {
        KeyGenerater keyGenerater = new KeyGenerater();
        keyGenerater.generater();
    }

    public void generater() {
        try {

            KeyPairGenerator keygen = KeyPairGenerator.getInstance("RSA");
            SecureRandom secrand = new SecureRandom();
            secrand.setSeed(("www.ecnu.cn").getBytes()); // 初始化随机产生器
            keygen.initialize(1024, secrand);
            KeyPair keys = keygen.genKeyPair();
            PublicKey pubkey = keys.getPublic();
            PrivateKey prikey = keys.getPrivate();
            pubKey = Base64.getEncoder().encode(pubkey.getEncoded());
            priKey = Base64.getEncoder().encode(prikey.getEncoded());
            System.out.println("pubKey = " + new String(pubKey));
            System.out.println("priKey = " + new String(priKey));

        } catch (java.lang.Exception e) {
            System.out.println("生成密钥对失败");
            e.printStackTrace();
        }
    }

    public byte[] getPriKey() {
        return priKey;
    }

    public byte[] getPubKey() {
        return pubKey;
    }

}

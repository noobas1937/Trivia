/** Created by Jack Chen at 15-11-12 */
package com.ecnu.trivia.common.util;

import com.ecnu.trivia.common.exception.IRCloudException;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;
import org.bouncycastle.crypto.BufferedBlockCipher;
import org.bouncycastle.crypto.engines.AESEngine;
import org.bouncycastle.crypto.modes.CBCBlockCipher;
import org.bouncycastle.crypto.paddings.BlockCipherPadding;
import org.bouncycastle.crypto.paddings.PKCS7Padding;
import org.bouncycastle.crypto.paddings.PaddedBufferedBlockCipher;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.Charset;
import java.security.Provider;

/**
 * 加密/解密类工具
 *
 * @author Jack Chen
 */
public class CryptoUtils {
    private static final byte[] salt = "newbi".getBytes();

    private static Provider provider = new BouncyCastleProvider();

    /** 使用aes算法进行信息加密,先base64编码，再加密，最后进行Hex编码，以保证数据完整性以及aes正确运行 */
    public static String aesBase64EncryptHex(String password, String message, Charset charset) {
        byte[] bytes = Base64.encodeBase64(message.getBytes(charset), false);
        bytes = aesEncrypt(password, bytes);

        return new String(Hex.encodeHex(bytes));
    }

    /** 使用aes算法进行信息加密 */
    public static byte[] aesEncrypt(String password, byte[] bytes) {
        try{
            return aes(password, bytes, true);
        } catch(Exception e) {
            throw new IRCloudException(e.getMessage(), e);
        }
    }

    /** 使用aes算法进行信息解密 */
    public static byte[] aesDecrypt(String password, byte[] bytes) {
        try{
            return aes(password, bytes, false);
        } catch(Exception e) {
            throw new IRCloudException(e.getMessage(), e);
        }
    }

    /** 先将密文进行Hex解码,使用aes算法进行信息解密,按base64解码,然后再返回为字符串 */
    public static String aesHexDecryptBase64(String password, String message, Charset charset) {
        byte[] bytes;
        try{
            bytes = Hex.decodeHex(message.toCharArray());
        } catch(DecoderException e) {
            throw new IRCloudException(e.getMessage(), e);
        }

        bytes = aesDecrypt(password, bytes);
        bytes = Base64.decodeBase64(bytes);
        return new String(bytes, charset);
    }

    private static byte[] aes(String password, byte[] bytes, boolean encrypt) throws Exception {
        //这里不使用加盐处理,并且强制导出长度为256,以保证aes密钥长度正确性
        PBEKeySpec pbeKeySpec = new PBEKeySpec(password.toCharArray(), salt, 1, 256);
        SecretKeyFactory keyFactory = SecretKeyFactory
                .getInstance("PBEWithSHA256And256BitAES-CBC-BC", provider);
        SecretKeySpec secretKey = new SecretKeySpec(keyFactory.generateSecret(pbeKeySpec).getEncoded(), "AES");
        byte[] key = secretKey.getEncoded();

        // 这里不使用向量处理，虽然每次加密文相同，但不需要处理 使用 ParametersWithIV 作为向量处理
        KeyParameter keyParam = new KeyParameter(key);

        // setup AES cipher in CBC mode with PKCS7 padding
        BlockCipherPadding padding = new PKCS7Padding();
        BufferedBlockCipher cipher = new PaddedBufferedBlockCipher(new CBCBlockCipher(new AESEngine()), padding);
        cipher.reset();

        cipher.init(encrypt, keyParam);

        byte[] buf = new byte[cipher.getOutputSize(bytes.length)];
        int len = cipher.processBytes(bytes, 0, bytes.length, buf, 0);
        len += cipher.doFinal(buf, len);

        if(encrypt) {
            return buf;
        }

        //remove padding
        byte[] out = new byte[len];
        System.arraycopy(buf, 0, out, 0, len);

        return out;
    }
}

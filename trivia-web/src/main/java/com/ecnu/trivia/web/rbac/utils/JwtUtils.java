package com.ecnu.trivia.web.rbac.utils;

import com.alibaba.fastjson.JSONObject;
import com.ecnu.trivia.web.rbac.domain.User;
import com.ecnu.trivia.web.utils.Constants;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Date;

/**
* @description JWT token验证工具类
* @author Jack Chen
* @date 2017/11/10
*/
@Component
public class JwtUtils {
    @Value("${jwt.secretKey}")
    private static String profiles;

    /**
     * 由字符串生成加密key
     * @return
     */
    public static SecretKey generalKey(){
        String stringKey = profiles+ Constants.JWT_SECRET;
        byte[] encodedKey = Base64.decodeBase64(stringKey);
        SecretKey key = new SecretKeySpec(encodedKey, 0, encodedKey.length, "AES");
        return key;
    }

    /**
     * 创建jwt
     * @param id
     * @param subject
     * @param ttlMillis
     * @return
     * @throws Exception
     */
    public static String createJWT(String id, String subject, long ttlMillis) throws Exception {
        SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;
        long nowMillis = System.currentTimeMillis();
        Date now = new Date(nowMillis);
        SecretKey key = generalKey();
        JwtBuilder builder = Jwts.builder()
                .setId(id)
                .setIssuedAt(now)
                .setSubject(subject)
                .signWith(signatureAlgorithm, key);
        if (ttlMillis >= 0) {
            long expMillis = nowMillis + ttlMillis;
            Date exp = new Date(expMillis);
            builder.setExpiration(exp);
        }
        return builder.compact();
    }

    /**
     * 解密jwt
     * @param jwt
     * @return
     * @throws Exception
     */
    public static Claims parseJWT(String jwt) throws Exception{
        SecretKey key = generalKey();
        Claims claims = Jwts.parser()
                .setSigningKey(key)
                .parseClaimsJws(jwt).getBody();
        return claims;
    }

    /**
     * 生成subject信息
     * @param user
     * @return
     */
    public static String generalSubject(User user){
        JSONObject jo = new JSONObject();
        jo.put("userId", user.getId());
        jo.put("roleId", user.getUserType());
        return jo.toJSONString();
    }

    /**
     * 获取subject信息
     * @return
     */
    public static User generalSubject(Claims claims){
        JSONObject jo= JSONObject.parseObject(claims.getSubject());
        User user = new User();
        user.setId(jo.getInteger("userId"));
        user.setUserType(jo.getInteger("roleId"));
        return user;
    }
}

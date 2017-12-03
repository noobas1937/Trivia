/** Created by Jack Chen at 2014/5/12 */
package com.ecnu.trivia.common.util;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.*;

/**
 * 用于处理对象操作的工具类
 *
 * @author Jack Chen
 */
public class ObjectUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(ObjectUtils.class);

    /** 对象或字符串不为空 或 null */
    public static <T> boolean isNotNullOrEmpty(T t) {
        return !isNullOrEmpty(t);
    }

    /**
     * 判断一个对象是否为null或empty对象
     * empty对象包括空字符串(包括空格串)，空数组,字集合等
     */
    public static <T> boolean isNullOrEmpty(T t) {
        //null对象
        if(t == null) {
            return true;
        }
        //序列串
        if(t instanceof CharSequence) {
            CharSequence cs = (CharSequence) t;
            for(int i = 0;i < cs.length();i++) {
                if(!Character.isWhitespace(cs.charAt(i))) {
                    return false;
                }
            }
            return true;
        }
        //数组
        if(t.getClass().isArray()) {
            return Array.getLength(t) == 0;
        }
        //集合
        if(t instanceof Collection) {
            return ((Collection) t).isEmpty();
        }
        //map
        if(t instanceof Map) {
            return ((Map) t).isEmpty();
        }
        //默认为false,即非null
        return false;
    }

    /**
     * 判断一个对象在业务逻辑上是否是null
     * 包括 isNullOrEmpty判断，以及数字0, 空值数组，空值集合等
     */
    public static <T> boolean isLogicalNull(T t) {
        if(isNullOrEmpty(t)) {
            return true;
        }

        //数字
        if(t instanceof Number) {
            return ((Number) t).doubleValue() == .0;
        }

        //数组
        if(t.getClass().isArray()) {
            for(int i = 0, j = Array.getLength(t);i < j;i++) {
                Object object = Array.get(t, i);
                if(!isLogicalNull(object)) {
                    return false;
                }
            }

            return true;
        }

        //空值集合
        if(t instanceof Collection) {
            for(Object obj : (Collection) t) {
                if(!isLogicalNull(obj)) {
                    return false;
                }
            }

            return true;
        }

        return false;
    }

    /**
     * 将字节数据转换化对象
     * @param bytes 字节数组
     * @return Object 对象
     */
    public static <T> T bytes2Object(byte[] bytes) {
        ByteArrayInputStream byteArrayInputStream = null;
        ObjectInputStream objectInputStream = null;
        try {
            byteArrayInputStream = new ByteArrayInputStream(bytes);
            objectInputStream = new ObjectInputStream(byteArrayInputStream);
            return (T) objectInputStream.readObject();
        } catch (Exception e) {
            LOGGER.error("将字节组转换为对象失败!", e.fillInStackTrace());
        } finally {
            try {
                byteArrayInputStream.close();
                objectInputStream.close();
            } catch (IOException e) {
                LOGGER.error("将字节组转换为对象时,关闭流失败!", e.fillInStackTrace());
            }
        }
        return null;
    }

    /**
     * 将对象转换为字节数组
     * @param obj 对象
     * @return bytes[] 字节数组
     */
    public static byte[] object2Bytes(Object obj) {
        byte[] bytes = null;
        ByteArrayOutputStream byteArrayOutputStream = null;
        ObjectOutputStream objectOutputStream = null;
        try {
            byteArrayOutputStream = new ByteArrayOutputStream();
            objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
            objectOutputStream.writeObject(obj);
            bytes = byteArrayOutputStream.toByteArray();
        } catch (Exception e) {
            LOGGER.error("将对象转换为字节数组失败!", e.fillInStackTrace());
        } finally {
            try {
                byteArrayOutputStream.close();
                objectOutputStream.close();
            } catch (IOException e) {
                LOGGER.error("将对象转换为字节数组时,关才流失败!", e.fillInStackTrace());
            }
        }
        return bytes;
    }

    public static Map<String, Object> object2Map(Object obj) {
        if(obj == null){
            return null;
        }

        Map<String, Object> map = Maps.newHashMap();
        try {
            Field[] declaredFields = obj.getClass().getDeclaredFields();
            for (Field field : declaredFields) {
                field.setAccessible(true);
                map.put(field.getName(), field.get(obj));
            }
        }catch (Exception e){
            LOGGER.error("Fail to transfer to Map:"+obj,e);
        }
        return map;
    }

    public static List<Map<String, Object>> objectList2Map(List<Object> objectList) {
        if(objectList == null){
            return null;
        }

        List<Map<String, Object>> list= Lists.newArrayList();
        for(Object obj:objectList) {
            Map<String, Object> map = Maps.newHashMap();
            try {
                Field[] declaredFields = obj.getClass().getDeclaredFields();
                for (Field field : declaredFields) {
                    field.setAccessible(true);
                    map.put(field.getName(), field.get(obj));
                }
            }catch (Exception e){
                LOGGER.error("Fail to transfer to Map:"+obj,e);
            }
            list.add(map);
        }
        return list;
    }

    /**
     * 判断对象数组中是否包含另外一个对象。
     * @param objs 对象集合。
     * @param o 要匹配的对象。
     * @return 是否包含。
     */
    public static boolean contaions(Object[] objs,Object o){
        for (Object obj: objs){
            if(obj.equals(o)) {
                return true;
            }
        }
        return false;
    }
}

/** Created by Jack Chen at 12/5/2014 */
package com.ecnu.trivia.common.util;

import java.util.Collection;
import java.util.Map;

/**
 * 连接工具，用于输出字符串信息
 *
 * @author Jack Chen
 */
public class JoinerUtils {
    private static final String comma = ",";

    /** 使用特定分隔符连接数组 */
    public static String joinByComma(Object[] objects, String nullValue,String comma) {
        if(objects == null || objects.length == 0) {
            return "";
        }

        StringBuilder builder = new StringBuilder();

        for(int i = 0; i < objects.length; i++) {
            if(i != 0) {
                builder.append(comma);
            }

            Object object = objects[i];
            String str = object == null ? nullValue : object.toString();
            builder.append(str);
        }

        return builder.toString();
    }

    /** 使用逗号连接数组 */
    public static String joinByComma(Object[] objects, String nullValue) {
        if(objects == null || objects.length == 0) {
            return "";
        }

        StringBuilder builder = new StringBuilder();

        for(int i = 0; i < objects.length; i++) {
            if(i != 0) {
                builder.append(comma);
            }

            Object object = objects[i];
            String str = object == null ? nullValue : object.toString();
            builder.append(str);
        }

        return builder.toString();
    }

    /** 使用逗号连接int数组 */
    public static String joinByComma(int[] ints) {
        if(ints == null || ints.length == 0) {
            return "";
        }

        StringBuilder builder = new StringBuilder();
        for(int i = 0; i < ints.length; i++) {
            if(i != 0) {
                builder.append(comma);
            }

            builder.append(ints[i]);
        }

        return builder.toString();
    }

    /** 使用逗号连接long数组 */
    public static String joinByComma(long[] longs) {
        if(longs == null || longs.length == 0) {
            return "";
        }

        StringBuilder builder = new StringBuilder();
        for(int i = 0; i < longs.length; i++) {
            if(i != 0) {
                builder.append(comma);
            }

            builder.append(longs[i]);
        }

        return builder.toString();
    }

    /** 使用逗号连接collection集合 */
    public static <T> String joinByComma(Collection<T> collection, String nullValue) {
        if(collection == null || collection.isEmpty()) {
            return "";
        }

        StringBuilder builder = new StringBuilder();
        boolean notStart = false;
        for(T t : collection) {
            if(notStart) {
                builder.append(comma);
            }

            builder.append(t == null ? nullValue : t.toString());

            notStart = true;
        }

        return builder.toString();
    }

    /** 使用特定的连接符连接map集合 */
    public static String join(Map<String, String> map, String nullValue, String kvConnector, String entryConnector) {
        if(map == null || map.isEmpty()) {
            return "";
        }

        StringBuilder builder = new StringBuilder();
        boolean start = true;
        for(Map.Entry<String, String> e : map.entrySet()) {
            if(!start) {
                builder.append(entryConnector);
            }

            String value = e.getValue();
            value = value == null ? nullValue : value;

            builder.append(e.getKey()).append(kvConnector).append(value);

            start = false;
        }

        return builder.toString();
    }
}

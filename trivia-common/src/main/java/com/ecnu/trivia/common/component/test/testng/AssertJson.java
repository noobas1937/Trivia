package com.ecnu.trivia.common.component.test.testng;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.primitives.Ints;
import com.ecnu.trivia.common.exception.IRCloudException;

import java.util.Objects;
import java.util.Set;
import java.util.stream.IntStream;

/**
 * 我们在做开发的时候，通常需比较两个JSON不一样的地方，分析原因，找出错误。
 * API只是比较是否一样，但不输出哪个Key不一样。所以自己写了一个工具。
 */
public class AssertJson {

    public static void assertEquals(Object actual, Object expected) {
        compareJson(actual, expected, "$");
    }

    private static void compareJSONObject(JSONObject json1, JSONObject json2, String key) {
        Set<String> strings1 = json1.keySet();
        Set<String> strings2 = json2.keySet();
        Set<String> set = strings1.size() >= strings2.size() ? strings1 : strings1;
        for(String s : set) {
            Object value1 = json1.get(s);
            Object value2 = json2.get(s);
            String newKey = key.concat(".").concat(s);

            if(Objects.isNull(value1) && Objects.isNull(value2)) {
                break;
            }

            if(Objects.isNull(value1) || Objects.isNull(value2)) {
                String msg = "key:" + newKey + ",actual:" + value1 + ",expected:" + value2;
                throw new IRCloudException("json比较失败，失败信息：" + msg);
            }

            compareJson(value1, value2, newKey);
        }
    }

    private static void compareJson(Object json1, Object json2, String key) {
        if(json1 instanceof JSONObject) {
            compareJSONObject((JSONObject) json1, (JSONObject) json2, key);
        } else if(json1 instanceof JSONArray) {
            compareJSONArray((JSONArray) json1, (JSONArray) json2, key);
        } else if(json1 instanceof String) {
            compareString((String) json1, (String) json2, key);
        } else {
            compareJson(json1.toString(), json2.toString(), key);
        }
    }

    private static void compareString(String str1, String str2, String key) {
        if(!Objects.equals(str1, str2)) {
            String msg = "key:" + key + ",actual:" + str1 + ",expected:" + str2;
            throw new IRCloudException("json比较失败，失败信息：" + msg);
        }
    }

    private static void compareJSONArray(JSONArray json1, JSONArray json2, String key) {
        int size1 = json1.size();
        int size2 = json2.size();
        int size = Ints.max(size1, size2);
        IntStream.range(0, size).forEach(i -> {
            String newKey = key.concat("[").concat(String.valueOf(i)).concat("]");
            Object next = i <= size1 - 1 ? json1.getJSONObject(i) : null;
            Object next1 = i <= size2 - 1 ? json2.getJSONObject(i) : null;
            if(Objects.isNull(next) || Objects.isNull(next1)) {
                String msg = "key:" + newKey + ",actual:" + next + ",expected:" + next1;
                throw new IRCloudException("json比较失败，失败信息：" + msg);
            }

            compareJson(next, next1, newKey);
        });

    }

}

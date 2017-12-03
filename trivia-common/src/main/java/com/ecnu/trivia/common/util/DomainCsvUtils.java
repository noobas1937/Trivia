/** Created by Jack Chen at 2014/6/24 */
package com.ecnu.trivia.common.util;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.ecnu.trivia.common.exception.ArgumentException;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * 用于处理由csv中获取domain格式数据的工具
 * csv中存储domain的内似格式如下所示：
 * <p/>
 * filename:Domain.csv.txt
 * <p/>
 * Key,propertyA,propertyB,propertyC
 * key1,valueA1,valueB1,valueC1
 * key2,valueA2,valueB2,valueC2
 * key3,valueA3,valueB3,valueC3
 * <p/>
 * 使用key值来惟一定义相应的数据信息,并通过类型转换将字符串转换为相应的字段信息
 *
 * @author Jack Chen
 */
public class DomainCsvUtils {
    private static Pattern dotPattern = Pattern.compile("\\.");

    private static <T> String class2Resource(Class<T> clazz) {
        String name = clazz.getName();
        name = dotPattern.matcher(name).replaceAll("/");
        return name + ".csv.txt";
    }

    public static <T> List<T> get(String[] keys, Class<T> clazz) {
        List<T> lists = Lists.newArrayList();
        for(String key : keys) {
            lists.add(DomainCsvUtils.get(key, clazz));
        }
        return lists;
    }

    /** 根据key值从数据文件中获取相应的数据 */
    public static <T> T get(String key, Class<T> clazz) {
        String resourceName = "/" + class2Resource(clazz);
        URL url = clazz.getResource(resourceName);
        try{
            String[] header = CsvUtils.readHeader(url);
            String[] data = CsvUtils.readLine(url, key);

            if(data == null) {
                throw new ArgumentException("相应资源没有数据信息:" + resourceName + ",key->" + key);
            }

            //准备进行数据转换
            header = org.apache.commons.lang3.ArrayUtils.remove(header, 0);
            data = org.apache.commons.lang3.ArrayUtils.removeAll(data, 0);
            int dataLength = data.length;

            //转换为map
            Map<String, String> map = Maps.newHashMap();
            for(int i = 0; i < header.length; i++) {
                String headerName = header[i];
                String dataValue = i >= dataLength ? null : data[i];
                //如果dataValue为<null>,则视为 null
                if("null".equalsIgnoreCase(dataValue)) {
                    dataValue = null;
                }
                map.put(headerName, dataValue);
            }

            return ClassUtils.copyInstance(map, clazz);
        } catch(IOException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }
}

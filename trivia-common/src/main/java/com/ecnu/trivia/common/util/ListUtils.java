/** Created by Jack Chen at 12/9/2014 */
package com.ecnu.trivia.common.util;

import com.google.common.base.Function;
import com.google.common.collect.Lists;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 集合工具类
 *
 * @author Jack Chen
 */
public class ListUtils {

    /** 将map转换为list,使用指定的处理器 */
    public static <T, K, V> List<T> asList(Map<K, V> map, Function<Map.Entry<K, V>, T> tFunction) {
        return map.entrySet().stream().map(tFunction::apply).collect(Collectors.toList());
    }

    /** 验证两个集合是否在内容上是相等的 */
    public static <T> boolean equals(List<T> aList, List<T> bList) {
        if(aList == bList) {
            return true;
        }

        if(aList.size() != bList.size()) {
            return false;
        }

        List<T> tempList = Lists.newArrayList(aList);
        tempList.removeAll(bList);

        return tempList.isEmpty();
    }
    
    public static String listToString(List list) {  
        StringBuilder sb = new StringBuilder();  
        if (list != null && list.size() > 0) {  
            for (int i = 0; i < list.size(); i++) {  
                if (i < list.size() - 1) {  
                    sb.append(list.get(i) + ",");  
                } else {  
                    sb.append(list.get(i));  
                }  
            }  
        }  
        return sb.toString();  
    }  

}

/** Created by Jack Chen at 12/14/2014 */
package com.ecnu.trivia.common.component.json.limit;

import com.alibaba.fastjson.serializer.JSONSerializer;

/**
 * 用于描述参与限制的属性
 *
 * @author Jack Chen
 */
public interface LimitProperty<T extends LimitProperty> extends Comparable<T> {
    /**
     * 是否允许特定的调用路径
     *
     * @param path        当前对象的路径信息
     * @param pathLength  路径步长长度
     * @param source      上层待处理的对象
     * @param name        当前处理对象的name值
     * @param resultValue 当前对象的值
     */
    public boolean allowPath(String path, int pathLength, Object source, String name, Object resultValue, JSONSerializer jsonSerializer);

    /** 获取匹配长度,即只能针对特定的长度作匹配 */
    public int getPathLength();
}

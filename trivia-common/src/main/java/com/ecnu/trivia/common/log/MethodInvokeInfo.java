/** Created by Jack Chen at 11/17/2014 */
package com.ecnu.trivia.common.log;

import java.lang.reflect.Method;
import java.util.Map;

/**
 * 记录调用的方法信息
 *
 * @author Jack Chen
 */
public class MethodInvokeInfo {
    /** 调用的方法名 */
    private final Method method;
    /** 传递的参数信息 */
    private final Map<String, Object> paramMap;
    /** 调用的层次 */
    private int level;

    public MethodInvokeInfo(Method method, Map<String, Object> paramMap) {
        this.method = method;
        this.paramMap = paramMap;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public Method getMethod() {
        return method;
    }

    public Map<String, Object> getParamMap() {
        return paramMap;
    }

    public String toDetailMessage() {
        StringBuilder builder = new StringBuilder();
        builder.append("Level ").append(level).append(";");
        builder.append("调用方法:").append(method.getDeclaringClass().getSimpleName()).append(".").append(method.getName());
        builder.append("\n");
        builder.append("参数信息:\n");
        for(Map.Entry<String, Object> e : paramMap.entrySet()) {
            builder.append("\t");
            builder.append(e.getKey());
            builder.append(":");
            builder.append(e.getValue());
            builder.append("\n");
        }
        return builder.toString();
    }
}

/** Created by Jack Chen at 2015/7/6 */
package com.ecnu.trivia.web.common.interceptor.permission;

import com.google.common.base.Objects;
import org.springframework.core.MethodParameter;

import java.lang.reflect.Method;

/**
 * 用于描述拦截的方法参数信息
 *
 * @author Jack Chen
 */
public class MethodParam {
    private final Method method;
    private final MethodParameter[] parameters;
    private final Object[] args;

    public MethodParam(Method method, MethodParameter[] parameters, Object[] args) {
        this.method = method;
        this.parameters = parameters;
        this.args = args;
    }

    /** 第1个参数 */
    @SuppressWarnings("unchecked")
    public <T> T first() {
        return (T) args[0];
    }

    /** 第2个参数 */
    @SuppressWarnings("unchecked")
    public <T> T second() {
        return (T) args[1];
    }

    /** 第3个参数 */
    @SuppressWarnings("unchecked")
    public <T> T third() {
        return (T) args[2];
    }

    /** 第指定下标的参数 */
    @SuppressWarnings("unchecked")
    public <T> T param(int index) {
        return (T) args[index];
    }

    /** 获取相应的参数信息 */
    public Object[] args() {
        return args;
    }

    /** 返回相应的方法 */
    public Method method() {
        return method;
    }

    /** 获取相应的参数描述信息 */
    public MethodParameter[] parameters() {
        return parameters;
    }

    /** 获取指定参数名的参数信息 */
    public MethodParameter parameterByName(String parameterName) {
        for(MethodParameter parameter : parameters) {
            if(Objects.equal(parameter.getParameterName(), parameterName)) {
                return parameter;
            }
        }

        return null;
    }
}

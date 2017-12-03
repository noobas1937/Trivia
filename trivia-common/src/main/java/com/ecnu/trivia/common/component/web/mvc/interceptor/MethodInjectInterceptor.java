/** Created by Jack Chen at 12/12/2014 */
package com.ecnu.trivia.common.component.web.mvc.interceptor;

import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 用于在请求中注册当前所调用的方法信息
 *
 * @author Jack Chen
 */
public class MethodInjectInterceptor extends HandlerInterceptorAdapter {
    public static final String HANDLER_METHOD_ATTRIBUTE = "handlerMethod";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if(handler instanceof HandlerMethod) {
            request.setAttribute(HANDLER_METHOD_ATTRIBUTE, handler);
        }

        return true;
    }
}

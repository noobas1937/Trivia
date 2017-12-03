/** Created by Jack Chen at 12/11/2014 */
package com.ecnu.trivia.common.component.web.servlet;

import com.ecnu.trivia.common.component.exception.Asserts;
import com.ecnu.trivia.common.exception.DataException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 用于描述http请求上下文
 *
 * @author Jack Chen
 */
public class HttpServletContext {
    private static class Holder {
        private final HttpServletRequest request;
        private final HttpServletResponse response;

        public Holder(HttpServletRequest request, HttpServletResponse response) {
            this.request = request;
            this.response = response;
        }
    }

    private static ThreadLocal<Holder> holderThreadLocal = new ThreadLocal<>();

    static void setContext(HttpServletRequest request, HttpServletResponse response) {
        holderThreadLocal.set(new Holder(request, response));
    }

    static void removeContext() {
        holderThreadLocal.remove();
    }

    /** 获取request对象 */
    public static HttpServletRequest getRequest() {
        Holder holder = holderThreadLocal.get();
        Asserts.assertTrue(holder != null, DataException.class, "获取request失败");
        assert holder != null;

        return holder.request;
    }

    /** 获取request对象 */
    public static HttpServletRequest getRequestOrDefault(HttpServletRequest defaultValue) {
        Holder holder = holderThreadLocal.get();
        if(holder == null) {
            return defaultValue;
        }

        return holder.request;
    }

    /** 获取response对象 */
    public static HttpServletResponse getResponse() {
        Holder holder = holderThreadLocal.get();
        Asserts.assertTrue(holder != null, DataException.class, "获取response失败");
        assert holder != null;

        return holder.response;
    }

    /** 获取response对象 */
    public static HttpServletResponse getResponseOrDefault(HttpServletResponse defaultValue) {
        Holder holder = holderThreadLocal.get();
        if(holder == null) {
            return defaultValue;
        }

        return holder.response;
    }
}

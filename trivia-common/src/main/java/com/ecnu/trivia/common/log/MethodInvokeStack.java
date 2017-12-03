/** Created by Jack Chen at 11/17/2014 */
package com.ecnu.trivia.common.log;

import java.util.ArrayDeque;
import java.util.Deque;

/** @author Jack Chen */
public class MethodInvokeStack {
    private static ThreadLocal<Deque<MethodInvokeInfo>> stackThreadLocal = new ThreadLocal<Deque<MethodInvokeInfo>>() {
        @Override
        protected Deque<MethodInvokeInfo> initialValue() {
            return new ArrayDeque<>();
        }
    };

    public static void push(MethodInvokeInfo loggerMethodInfo) {
        Deque<MethodInvokeInfo> stack = stackThreadLocal.get();
        if(loggerMethodInfo.getLevel() == 0) {
            MethodInvokeInfo top = stack.peek();
            loggerMethodInfo.setLevel(top == null ? 0 : top.getLevel() + 1);
        }
        stack.push(loggerMethodInfo);
    }

    public static MethodInvokeInfo peek() {
        Deque<MethodInvokeInfo> stack = stackThreadLocal.get();
        return stack.peek();
    }

    public static MethodInvokeInfo pop() {
        Deque<MethodInvokeInfo> stack = stackThreadLocal.get();
        return stack.poll();
    }
}

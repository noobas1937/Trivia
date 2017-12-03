/** Created by Jack Chen at 11/15/2014 */
package com.ecnu.trivia.common.log;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.Map;

/** @author Jack Chen */
@Aspect
public class LoggerAdvice {
    private static final Logger logger = LoggerFactory.getLogger(LoggerAdvice.class);

    @Pointcut("execution(@com.ecnu.trivia.common.log.NonLogged public * com.ecnu.trivia..*.*(..))")
    private static void invokeNonLogMethod() {
    }

    @Pointcut("execution(@com.ecnu.trivia.common.log.Logged public * com.ecnu.trivia..*.*(..))")
    private static void invokeLogMethod() {
    }

    @Pointcut("invokeLogMethod() || (execution(public * com.ecnu.trivia..Logable+.*(..)) && !invokeNonLogMethod())")
    private static void invokeMethod() {
    }

    @Before("invokeMethod()")
    public void beforeMethod(JoinPoint joinPoint) throws Throwable {
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        Method method = methodSignature.getMethod();
        String[] params = methodSignature.getParameterNames();
        Object[] objs = joinPoint.getArgs();

        Map<String, Object> paramMap;
        if(params != null && params.length > 0) {
            paramMap = Maps.newLinkedHashMap();
            for(int i = 0; i < params.length; i++) {
                paramMap.put(params[i], objs[i]);
            }
        } else {
            paramMap = ImmutableMap.of();
        }

        MethodInvokeInfo methodInvokeInfo = new MethodInvokeInfo(method, paramMap);

        MethodInvokeStack.push(methodInvokeInfo);

        //记录参数调用
        if(logger.isDebugEnabled()) {
            logger.debug("LoggerAdvice:"+methodInvokeInfo.toDetailMessage());
        }
    }

    @AfterReturning("invokeMethod()")
    public void afterMethod() {
        MethodInvokeStack.pop();
    }

}

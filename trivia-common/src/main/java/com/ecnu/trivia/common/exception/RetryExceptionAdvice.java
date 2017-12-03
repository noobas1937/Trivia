package com.ecnu.trivia.common.exception;

import com.ecnu.trivia.common.util.ObjectUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;

/**
 * 当发生指定异常后,自动进行重试动作.
 * @author Jack Chen
 * @date 2015/7/8.
 */
@Aspect
public class RetryExceptionAdvice {

    private static final Logger logger = LoggerFactory.getLogger(RetryExceptionAdvice.class);

    @Pointcut("execution(@com.ecnu.trivia.common.exception.RetryAnnotation public * com.ecnu.trivia..*.*(..))")
    private static void invokeMethod(){

    }

    @Around("invokeMethod()")
    public Object aroundRetryMethod(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        Method method = methodSignature.getMethod();
        RetryAnnotation retryAnnotation = method.getAnnotation(RetryAnnotation.class);
        int retry = retryAnnotation.retryTimes();
        long sleep = retryAnnotation.sleep();
        Class[] exceptionClazz = retryAnnotation.exceptions();

        boolean flag = true;
        int count = 1;
        while(flag){
            try {
                Object obj = joinPoint.proceed();
                return obj;
            } catch (Throwable throwable) {
                Class clazz = throwable.getClass();
                if(ObjectUtils.contaions(exceptionClazz, clazz)){
                    logger.debug("开始进行重试方法调用...");
                    if(count > retry){
                        flag = false;
                    }else{
                        logger.warn("调用失败，开始重新调用：第{}次，调用{},间隔{}ms,异常信息：{}！", count, method, sleep, throwable.getMessage());
                        count++;
                        try {
                            Thread.sleep(sleep);
                        } catch (InterruptedException e) {
                        }
                        continue;
                    }
                }
                throw throwable;
            }
        }
        logger.debug("方法调用完成！");
        return null;
    }
}

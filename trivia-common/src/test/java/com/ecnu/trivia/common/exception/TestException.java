package com.ecnu.trivia.common.exception;

import org.springframework.stereotype.Component;

/**
 * @author wangwei
 * @date 2015/7/8.
 */
@Component
public class TestException {

    @RetryAnnotation(retryTimes = 3,exceptions = NullPointerException.class)
    public void execute(){
        throw new NullPointerException("测试空指针异常！");
    }

    @RetryAnnotation(retryTimes = 3,sleep = 1000,exceptions = NullPointerException.class)
    public void executeSleep(){
        throw new NullPointerException("测试空指针异常！");
    }

    @RetryAnnotation(retryTimes = 3,sleep = 1000,exceptions = BusinessException.class)
    public void executeNoException(){
        throw new NullPointerException("测试空指针异常！");
    }

    @RetryAnnotation(retryTimes = 3,sleep = 1000,exceptions = NullPointerException.class)
    public String executeNormal(){
        return "你调用成功了！";
    }
}

package com.ecnu.trivia.common.exception;

import com.ecnu.trivia.common.component.test.testng.BaseTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * 方法重试测试。
 * @author wangwei
 * @date 2015/7/8.
 */
public class TestExceptionTest extends BaseTest{

    @Autowired
    private TestException testException;

    /**
     * 测试发生期待异常生，重试3次。
     * @throws Exception
     */
    @Test(expectedExceptions = NullPointerException.class)
    public void testExecute() throws Exception {
        testException.execute();
    }

    /**
     * 测试发生异常后间隔多少再重试。
     * @throws Exception
     */
    @Test(expectedExceptions = NullPointerException.class)
    public void testExecuteSleep() throws Exception {
        testException.executeSleep();
    }

    /**
     * 测试异常不在重试范围，不进行重试。
     * @throws Exception
     */
    @Test(expectedExceptions = NullPointerException.class)
    public void testExecuteNoException() throws Exception {
        testException.executeNoException();
    }

    /**
     * 正常任务调用成功。
     * @throws Exception
     */
    @Test
    public void testExecuteNormal() throws Exception {
        String result = testException.executeNormal();
        Assert.assertEquals(result, "你调用成功了！");
    }
}

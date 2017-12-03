/** Created by Jack Chen at 12/25/2014 */
package com.ecnu.trivia.common.component.test.testng;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.TestExecutionListener;
import org.springframework.transaction.annotation.Transactional;
import org.testng.IHookCallBack;
import org.testng.IHookable;
import org.testng.ITestResult;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

/** @author Jack Chen */
@Transactional
public class SpringContextTests implements IHookable {
    private static final Logger logger = LoggerFactory.getLogger(SpringContextTests.class);

    private static List<TestExecutionListener> testExecutionListenerList;
    private static List<TestExecutionListener> reversedTestExecutionListeners;

    private final TestContextImpl testContext;

    private Throwable testException;

    public SpringContextTests() {
        //获取spring上下文
        testContext = new TestContextImpl(SpringStartupListener.applicationContext);
        testContext.setTestClass(this.getClass());

        testExecutionListenerList = SpringStartupListener.testExecutionListenerList;
        reversedTestExecutionListeners = SpringStartupListener.reversedTestExecutionListeners;
    }

    @BeforeClass(alwaysRun = true)
    protected void springTestContextBeforeTestClass() throws Exception {
        testContext.updateState(null, null, null);

        for(TestExecutionListener testExecutionListener : testExecutionListenerList) {
            try {
                testExecutionListener.beforeTestClass(testContext);
            } catch(Exception ex) {
                logger.warn(ex.getMessage(), ex);
                throw ex;
            }
        }

    }

    @BeforeClass(alwaysRun = true, dependsOnMethods = "springTestContextBeforeTestClass")
    protected void springTestContextPrepareTestInstance() throws Exception {
        testContext.updateState(this, null, null);

        for(TestExecutionListener testExecutionListener : testExecutionListenerList) {
            try {
                testExecutionListener.prepareTestInstance(testContext);
            } catch(Exception ex) {
                logger.error(ex.getMessage(), ex);
                throw ex;
            }
        }
    }

    @BeforeMethod(alwaysRun = true)
    protected void springTestContextBeforeTestMethod(Method testMethod) throws Exception {
        testContext.updateState(this, testMethod, null);

        for(TestExecutionListener testExecutionListener : testExecutionListenerList) {
            try {
                testExecutionListener.beforeTestMethod(testContext);
            } catch(Exception ex) {
                logger.warn(ex.getMessage(), ex);
                throw ex;
            }
        }
    }

    @Override
    public void run(IHookCallBack callBack, ITestResult testResult) {
        callBack.runTestMethod(testResult);

        Throwable testResultException = testResult.getThrowable();
        if(testResultException instanceof InvocationTargetException) {
            testResultException = testResultException.getCause();
        }
        this.testException = testResultException;
    }

    @AfterMethod(alwaysRun = true)
    protected void springTestContextAfterTestMethod(Method testMethod) throws Exception {
        try {
            testContext.updateState(this, testMethod, this.testException);

            Exception afterTestMethodException = null;
            for(TestExecutionListener testExecutionListener : reversedTestExecutionListeners) {
                try {
                    testExecutionListener.afterTestMethod(testContext);
                } catch(Exception ex) {
                    logger.error(ex.getMessage(), ex);

                    if(afterTestMethodException == null) {
                        afterTestMethodException = ex;
                    }
                }
            }
            if(afterTestMethodException != null) {
                throw afterTestMethodException;
            }
        } finally {
            this.testException = null;
        }
    }

    @AfterClass(alwaysRun = true)
    protected void springTestContextAfterTestClass() throws Exception {
        testContext.updateState(null, null, null);

        Exception afterTestClassException = null;
        for(TestExecutionListener testExecutionListener : reversedTestExecutionListeners) {
            try {
                testExecutionListener.afterTestClass(testContext);
            } catch(Exception ex) {
                logger.warn(ex.getMessage(), ex);

                if(afterTestClassException == null) {
                    afterTestClassException = ex;
                }
            }
        }
        if(afterTestClassException != null) {
            throw afterTestClassException;
        }
    }
}

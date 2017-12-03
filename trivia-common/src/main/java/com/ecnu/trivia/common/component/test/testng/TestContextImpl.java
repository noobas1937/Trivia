/** Created by Jack Chen at 12/25/2014 */
package com.ecnu.trivia.common.component.test.testng;

import org.springframework.context.ApplicationContext;
import org.springframework.core.AttributeAccessorSupport;
import org.springframework.core.style.ToStringCreator;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.*;
import org.springframework.util.Assert;

import java.lang.reflect.Method;

/** @author Jack Chen */
public class TestContextImpl extends AttributeAccessorSupport implements TestContext {
    private static final long serialVersionUID = -5827157174866681233L;

    private final ApplicationContext applicationContext;

    private Class<?> testClass;

    private Object testInstance;

    private Method testMethod;

    private Throwable testException;


    public TestContextImpl(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Override
    public ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    @Override
    public void markApplicationContextDirty(DirtiesContext.HierarchyMode hierarchyMode) {
        //nothing to do
    }

    @Override
    public final Class<?> getTestClass() {
        return testClass;
    }

    @Override
    public final Object getTestInstance() {
        return testInstance;
    }

    @Override
    public final Method getTestMethod() {
        return testMethod;
    }

    @Override
    public final Throwable getTestException() {
        return testException;
    }

    public void setTestClass(Class<?> testClass) {
        this.testClass = testClass;
    }

    @Override
    public void updateState(Object testInstance, Method testMethod, Throwable testException) {
        this.testInstance = testInstance;
        this.testMethod = testMethod;
        this.testException = testException;
    }

    @Override
    public String toString() {
        return "TestContextImpl{" +
                "testClass=" + testClass +
                ", testInstance=" + testInstance +
                ", testMethod=" + testMethod +
                ", testException=" + testException +
                '}';
    }
}

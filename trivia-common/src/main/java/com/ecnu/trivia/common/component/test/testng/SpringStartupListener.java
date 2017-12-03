/** Created by Jack Chen at 12/27/2014 */
package com.ecnu.trivia.common.component.test.testng;

import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.core.OrderComparator;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListener;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;
import org.testng.IExecutionListener;

import java.util.Collections;
import java.util.List;

/**
 * 用于在testng启动时创建spring上下文
 *
 * @author Jack Chen
 */
public class SpringStartupListener implements IExecutionListener {
    private static final Logger logger = LoggerFactory.getLogger(SpringStartupListener.class);

    protected static List<TestExecutionListener> testExecutionListenerList = Lists.newArrayList();
    protected static List<TestExecutionListener> reversedTestExecutionListeners = Lists.newArrayList();

    protected static ApplicationContext applicationContext;

    @Override
    public void onExecutionStart() {
        ContextConfiguration configuration = AnnotationUtils.findAnnotation(this.getClass(), ContextConfiguration.class);
        if(configuration != null && applicationContext == null) {
            applicationContext = new ClassPathXmlApplicationContext(configuration.locations());
        }

        //处理监听器
        testExecutionListenerList.add(new TransactionalTestExecutionListener());
        testExecutionListenerList.add(new DependencyInjectionTestExecutionListener());
        testExecutionListenerList.add(new ThreadCacheTestExecutionListener());
        Collections.sort(testExecutionListenerList, OrderComparator.INSTANCE);
        reversedTestExecutionListeners = Lists.newArrayList(testExecutionListenerList);
        Collections.reverse(reversedTestExecutionListeners);
    }

    @Override
    public void onExecutionFinish() {
        if(applicationContext != null && applicationContext instanceof DisposableBean) {
            try{
                ((DisposableBean) applicationContext).destroy();
            } catch(Exception e) {
                logger.error(e.getMessage(), e);
            }
        }
    }
}

/** Created by Jack Chen at 9/11/2014 */
package com.ecnu.trivia.common.frameworkext;

import com.ecnu.trivia.common.frameworkext.javassist.ClassPools;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;

/**
 * 用于修改默认的mybatis结果处理逻辑实现
 *
 * @author Jack Chen
 */
public class FastResultSetHandlerModifier implements BeanFactoryPostProcessor {
    private static boolean modified = false;

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        if(modified) {
            return;
        }
        modified = true;
        try {
            modified();
        } catch(Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    private void modified() throws Exception {
        ClassPool classPool = ClassPools.getDefault();
        CtClass ctClass = classPool.get("org.apache.ibatis.executor.resultset.DefaultResultSetHandler");
        CtClass insteadClass = classPool.get("org.apache.ibatis.executor.resultset.FastResultSetHandlerInstead");

        //修改结果匹配规则
        CtMethod ctMethod = ctClass.getDeclaredMethod("applyAutomaticMappings");
        CtMethod modifiedMethod = insteadClass.getDeclaredMethod("applyAutomaticMappings");
        ctMethod.setBody(modifiedMethod, null);

        //修改结果对象创建规则
        CtClass[] params = {classPool.get("org.apache.ibatis.executor.resultset.ResultSetWrapper"), classPool.get("org.apache.ibatis.mapping.ResultMap"), classPool
                .get("java.util.List"), classPool.get("java.util.List"), classPool.get("java.lang.String")};
        ctMethod = ctClass.getDeclaredMethod("createResultObject", params);
        modifiedMethod = insteadClass.getDeclaredMethod("createResultObject", params);
        ctMethod.setBody(modifiedMethod, null);

        ctClass.toClass();
    }
}

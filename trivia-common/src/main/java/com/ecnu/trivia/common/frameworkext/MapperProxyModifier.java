/** Created by Jack Chen at 11/30/2014 */
package com.ecnu.trivia.common.frameworkext;

import com.ecnu.trivia.common.frameworkext.javassist.ClassPools;
import javassist.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;

/**
 * 用于修改默认的mapper调用规则的处理
 *
 * @author Jack Chen
 */
public class MapperProxyModifier implements BeanFactoryPostProcessor {

    private static final Logger logger = LoggerFactory.getLogger(MapperProxyModifier.class);
    private static boolean modified = false;

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        if(modified) {
            return;
        }
        modified = true;

        try {
            modify();
        } catch(Exception e) {
            logger.error("调用MapperProxyModifier失败:{}", e.getMessage(), e);
        }
    }

    private void modify() throws Exception {
        ClassPool classPool = ClassPools.getDefault();

        CtClass ctClass = classPool.get("org.apache.ibatis.binding.MapperProxy");
        CtClass insteadClass = classPool.get("MapperProxyInstead");

        CtMethod ctMethod = ctClass.getDeclaredMethod("invoke");
        CtMethod insteadMethod = insteadClass.getDeclaredMethod("invoke");

        ctMethod.setBody(insteadMethod, null);

        ctClass.toClass();
    }
}

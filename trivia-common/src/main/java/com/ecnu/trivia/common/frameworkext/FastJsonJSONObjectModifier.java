/** Created by Jack Chen at 2014/7/18 */
package com.ecnu.trivia.common.frameworkext;

import com.ecnu.trivia.common.frameworkext.javassist.ClassPools;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;

/**
 * 用于修改fastjson中的jsonObject的默认map
 *
 * @author Jack Chen
 */
public class FastJsonJSONObjectModifier implements BeanFactoryPostProcessor {
    private static final Logger logger = LoggerFactory.getLogger(FastJsonJSONObjectModifier.class);
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
            logger.error("调用FastJsonJSONObjectModifier失败:{}", e.getMessage(), e);
        }
    }

    private void modify() throws Exception {
        ClassPool classPool = ClassPools.getDefault();

        CtClass ctClass = classPool.get("com.alibaba.fastjson.JSONObject");
        CtConstructor ctConstructor = ctClass.getConstructor("(IZ)V");

        //将默认的ordered参数修改为true，即使用LinkedHashMap
        ctConstructor.insertBeforeBody("{$2 = true;}");

        ctClass.toClass();
    }
}

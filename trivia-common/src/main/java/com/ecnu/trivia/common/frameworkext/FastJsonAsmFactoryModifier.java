/** Created by Jack Chen at 12/14/2014 */
package com.ecnu.trivia.common.frameworkext;

import com.ecnu.trivia.common.frameworkext.javassist.ClassPools;
import javassist.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;

/**
 * 用于修改fastjson中的AsmFactory生成的类,在其中增加标记接口
 *
 * @author Jack Chen
 */
public class FastJsonAsmFactoryModifier implements BeanFactoryPostProcessor {
    private static final Logger logger = LoggerFactory.getLogger(FastJsonAsmFactoryModifier.class);
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
            logger.error("调用FastJsonAsmFactoryModifier失败:{}", e.getMessage(), e);
        }
    }

    private void modify() throws Exception {
        ClassPool classPool = ClassPools.getDefault();

        CtClass ctClass =      classPool.get("com.alibaba.fastjson.parser.deserializer.JavaBeanDeserializer");
        CtClass insteadClass = classPool.get("com.alibaba.fastjson.parser.deserializer.JavaBeanDeserializerInstead");

        CtClass[] params2 = {
                classPool.get("com.alibaba.fastjson.parser.DefaultJSONParser"),
                classPool.get("java.lang.reflect.Type"),
                classPool.get("java.lang.Object"), classPool.get("java.lang.Object")
        };

        CtMethod ctMethod = ctClass.getDeclaredMethod("deserialze", params2);
        CtMethod insteadMethod = insteadClass.getDeclaredMethod("deserialze", params2);

        //更改 JavaBeanDeserializer.deserialze 方法，在枚举对象为空串，直接忽略
        ctMethod.setBody(insteadMethod, null);

        ctClass.toClass();
    }

}

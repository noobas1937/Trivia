/**
 * Created by shilian.peng at 16/6/20
 */
package com.ecnu.trivia.common.util;

import com.ecnu.trivia.common.ApplicationContextHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.ConfigurableApplicationContext;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import java.util.Properties;

/**
 * @author shilian.peng
 */
public class PropertiesUtils {

    private static Logger logger = LoggerFactory.getLogger(PropertiesUtils.class);

    public static String getProperty(String key) {
        return PropertiesUtils.resolveEmbeddedValue("${" + key + "}");
    }

    public static String getProperty(String key, String defaultValue) {
        if (Objects.isNull(defaultValue)) {
            return PropertiesUtils.getProperty(key);
        }

        return PropertiesUtils.resolveEmbeddedValue("${" + key + ":" + defaultValue + "}");
    }

    /**
     * 从spring容器中获取属性信息 ,${key:defaultValue}
     */
    public static String resolveEmbeddedValue(String value) {
        ConfigurableApplicationContext applicationContext = (ConfigurableApplicationContext) ApplicationContextHolder.getApplicationContext();
        ConfigurableListableBeanFactory beanFactory = applicationContext.getBeanFactory();
        return beanFactory.resolveEmbeddedValue(value);
    }

    public static Properties getProperties(String propertiesFile) {
        Properties props = new Properties();
        InputStream resource = PropertiesUtils.class.getResourceAsStream(propertiesFile);
        try {
            props.load(resource);
            return props;
        } catch (IOException e) {
            logger.error("", e);
        }
        return null;
    }


}

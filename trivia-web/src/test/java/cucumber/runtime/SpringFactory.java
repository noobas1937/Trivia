package cucumber.runtime;

import com.ecnu.trivia.common.ApplicationContextHolder;
import com.ecnu.trivia.web.ApplicationContextListener;
import cucumber.runtime.java.ObjectFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.support.AbstractApplicationContext;

import java.util.Collection;
import java.util.HashSet;

/** 用于支持cucumber和spring相整合 */
public class SpringFactory implements ObjectFactory {

    private AbstractApplicationContext beanFactory;

    private final Collection<Class<?>> stepClasses = new HashSet<Class<?>>();

    @Override
    public void addClass(final Class<?> stepClass) {
        if(!stepClasses.contains(stepClass)) {
            stepClasses.add(stepClass);
            if(beanFactory != null) {
                registerStepClassBeanDefinition(beanFactory.getBeanFactory(), stepClass);
            }
        }
    }

    @Override
    public void start() {
        boolean newCreated = beanFactory == null;
        if(beanFactory == null) {
            beanFactory = (AbstractApplicationContext) ApplicationContextHolder.getApplicationContext();
            if(beanFactory == null) {
                new ApplicationContextListener().onExecutionStart();
                beanFactory = (AbstractApplicationContext) ApplicationContextHolder.getApplicationContext();
            }
        }
        if(newCreated) {
            beanFactory.getBeanFactory().registerScope(GlueCodeScope.NAME, new GlueCodeScope());
            for(Class<?> stepClass : stepClasses) {
                registerStepClassBeanDefinition(beanFactory.getBeanFactory(), stepClass);
            }
        }

        GlueCodeContext.INSTANCE.start();
    }


    private void registerStepClassBeanDefinition(ConfigurableListableBeanFactory beanFactory, Class<?> stepClass) {
        BeanDefinitionRegistry registry = (BeanDefinitionRegistry) beanFactory;
        BeanDefinition beanDefinition = BeanDefinitionBuilder
                .genericBeanDefinition(stepClass)
                .setScope(GlueCodeScope.NAME)
                .getBeanDefinition();
        registry.registerBeanDefinition(stepClass.getName(), beanDefinition);
    }

    @Override
    public void stop() {
        GlueCodeContext.INSTANCE.stop();
    }

    @Override
    public <T> T getInstance(final Class<T> type) {
        try{
            return beanFactory.getBean(type);
        } catch(BeansException e) {
            throw new CucumberException(e.getMessage(), e);
        }
    }

}

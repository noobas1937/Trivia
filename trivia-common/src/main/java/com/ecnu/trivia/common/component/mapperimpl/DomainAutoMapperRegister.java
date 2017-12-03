/** Created by Jack Chen at 2014/6/25 */
package com.ecnu.trivia.common.component.mapperimpl;


import com.ecnu.trivia.common.ApplicationContextHolder;
import com.ecnu.trivia.common.component.ClassFinder;
import com.ecnu.trivia.common.component.domain.Table;
import com.ecnu.trivia.common.component.domain.TableType;
import com.ecnu.trivia.common.component.domain.utils.DomainUtils;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.stream.Collectors;

/** @author Jack Chen */
@Component
public class DomainAutoMapperRegister {

    @Value("${business.sqlSessionFactory:sqlSessionFactory}")
    private String sqlSessionFactoryBeanName;


    @PostConstruct
    @SuppressWarnings("unchecked")
    public void init() {
        autoMapperRegister(sqlSessionFactoryBeanName);
    }

    private void autoMapperRegister(String sqlSessionFactoryBeanName){
        //初始化检测，只有提供了相应的业务项，才进行处理
        if(!StringUtils.hasText(sqlSessionFactoryBeanName)) {
            return;
        }

        SqlSessionFactory sqlSessionFactory = (SqlSessionFactory) ApplicationContextHolder.getApplicationContext().getBean(sqlSessionFactoryBeanName);
        @SuppressWarnings("unchecked")
        List<Class<Object>> domainClassList = ClassFinder.getInstance().findClassByAnnotation(Table.class).stream().filter(t -> t.getAnnotation(Table.class).type() == TableType.standard).collect(Collectors.toList());
        Configuration configuration = sqlSessionFactory.getConfiguration();

        for(Class<?> domainClass : domainClassList) {
            DomainUtils.addDomainClass(domainClass);
            MybatisHelper.addAutoGenerateStatement(domainClass, configuration);
        }
    }
}

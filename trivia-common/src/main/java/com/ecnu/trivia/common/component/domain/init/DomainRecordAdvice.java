/** Created by Jack Chen at 15-7-27 */
package com.ecnu.trivia.common.component.domain.init;

import com.ecnu.trivia.common.component.domain.Domain;
import com.ecnu.trivia.common.component.domain.DomainTable;
import com.ecnu.trivia.common.component.domain.utils.DomainUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;

/** @author Jack Chen */
@Aspect
public class DomainRecordAdvice {
    @Pointcut("execution(public void com.ecnu.trivia.common.component.domain.Domain+.set*(*))")
    private static void invokeSetter() {
    }

    @Before("invokeSetter() && this(self)")
    public void beforeSetter(JoinPoint joinPoint, Domain self) throws Exception {
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        Class clazz = self.getClass();

        String fieldName = methodSignature.getParameterNames()[0];

        DomainTable domainTable = DomainUtils.getDomainTable(clazz);
        self.addModifiedRecord(domainTable, fieldName, joinPoint.getArgs()[0]);
    }
}

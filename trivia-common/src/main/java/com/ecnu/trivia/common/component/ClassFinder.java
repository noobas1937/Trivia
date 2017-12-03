/** Created by Jack Chen at 2014/6/25 */
package com.ecnu.trivia.common.component;

import com.google.common.collect.Lists;
import org.reflections.Reflections;

import java.lang.annotation.Annotation;
import java.lang.reflect.Modifier;
import java.util.List;
import java.util.Set;

/**
 * 用于实现项目内的文件查找和处理
 *
 * @author Jack Chen
 */
public class ClassFinder {
    private static final String findPath = "com.ecnu.trivia";
    private static ClassFinder classFinder = new ClassFinder();

    private Reflections reflections;

    private ClassFinder() {
        this.reflections = new Reflections(findPath);
    }

    public static ClassFinder getInstance() {
        return classFinder;
    }

    @SuppressWarnings("unchecked")
    private static <T> List<Class<? extends T>> filterPublicClass(Set<Class<? extends T>> classSet) {
        List<Class<? extends T>> classList = Lists.newArrayList();
        for(Class<? extends T> clazz : classSet) {
            //不能为抽象类，不能为接口，只能为公共类
            if(clazz.isInterface()) {
                continue;
            }
            int modifier = clazz.getModifiers();
            if(!Modifier.isPublic(modifier)) {
                continue;
            }
            if(Modifier.isAbstract(modifier)) {
                continue;
            }

            classList.add(clazz);
        }
        return classList;
    }

    /** 找到所有继承指定类或实现指定接口的公共非接口子类 */
    @SuppressWarnings("unchecked")
    public <T> List<Class<? extends T>> findSubClass(Class<T> superClassInterface) {
        Set<Class<? extends T>> classSet = reflections.getSubTypesOf(superClassInterface);
        return filterPublicClass(classSet);
    }

    /** 找到所有具有指定注解的的公共非接口子类 */
    @SuppressWarnings("unchecked")
    public <S, T extends Annotation> List<Class<S>> findClassByAnnotation(Class<T> type) {
        Set<Class<?>> classSet = reflections.getTypesAnnotatedWith(type, true);
        return (List) ClassFinder.filterPublicClass(classSet);
    }

    /** 查找更底层的类查找器 */
    public Reflections returnLowerFinder() {
        return reflections;
    }
}

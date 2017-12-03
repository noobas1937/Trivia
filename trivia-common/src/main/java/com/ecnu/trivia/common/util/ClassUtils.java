/** Created by Jack Chen at 2014/4/28 */
package com.ecnu.trivia.common.util;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.ecnu.trivia.common.component.exception.Asserts;
import org.springframework.beans.BeanUtils;

import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

/** @author Jack Chen */
public class ClassUtils {

    /** class.forName的无异常版 */
    @SuppressWarnings("unchecked")
    public static <T> Class<T> forName(String clazz) {
        try{
            return (Class<T>) Class.forName(clazz);
        } catch(ClassNotFoundException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    /** 对指定的类进行初始化 */
    public static <T> T newInstance(Class<T> clazz) {
        try{
            return clazz.newInstance();
        } catch(Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    /** 对批量的类进行初始化 */
    public static <T> List<T> newInstance(List<Class<? extends T>> clazzList) {
        List<T> tList = Lists.newArrayList();
        for(Class<? extends T> clazz : clazzList) {
            tList.add(newInstance(clazz));
        }
        return tList;
    }

    /** 将指定类型的对象向下转型为子类的对象 */
    @SuppressWarnings("unchecked")
    public static <S, T extends S> T subInstance(S s, Class<T> clazz) {
        if(s == null) {
            return null;
        }

        Asserts.assertTrue(s.getClass().isAssignableFrom(clazz), "目标类{}不是当前对象类{}的子类", clazz, s.getClass());

        if(clazz == s.getClass()) {
            return (T) s;
        }
        T t = newInstance(clazz);
        try{
            BeanUtils.copyProperties(s, t);
        } catch(Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
        return t;
    }

    /** 将指定类型的对象copy为另一个类的对象,按照属性名进行对称copy,如果可以进行转换，则进行可能的转换 */
    public static <T, S> T copyInstance(S s, Class<T> clazz) {
        Map<String, Object> map = Maps.newHashMap();
        @SuppressWarnings("unchecked")
        Class<S> sourceClass = (Class<S>) s.getClass();
        try{
            PropertyDescriptor[] sourceDescriptors = BeanUtils.getPropertyDescriptors(sourceClass);
            for(PropertyDescriptor descriptor : sourceDescriptors) {
                Method readMethod = descriptor.getReadMethod();
                if(readMethod == null) {
                    continue;
                }
                map.put(descriptor.getName(), readMethod.invoke(s));
            }
        } catch(IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
        return copyInstance(map, clazz);
    }

    /** 使用map中的数据转换为另一个类的对象信息,按照属性名进行对称copy,如果可以进行转换，则进行可能的转换 */
    public static <T> T copyInstance(Map<String, ?> map, Class<T> clazz) {
        T t = newInstance(clazz);
        PropertyDescriptor[] destDescriptors = BeanUtils.getPropertyDescriptors(clazz);
        for(PropertyDescriptor descriptor : destDescriptors) {
            String property = descriptor.getName();

            //目标没有可写方法，则略过
            Method writeMethod = descriptor.getWriteMethod();
            if(writeMethod == null) {
                continue;
            }

            //源没有相应属性，略过
            if(!map.containsKey(property)) {
                continue;
            }

            //源值为null，略过
            Object value = map.get(property);
            if(value == null) {
                continue;
            }

            Class<?> propertyType = descriptor.getPropertyType();
            Class<?> valueClazz = value.getClass();

            Object convertValue;
            //如果本身就是类型相同的，则不需要进行转换
            if(propertyType.isAssignableFrom(valueClazz)) {
                convertValue = value;
            } else {
                convertValue = com.ecnu.trivia.common.util.ConvertUtils.convert(value, propertyType);
            }

            //转换值为null，则略过，表示无需进行设置值
            if(convertValue == null) {
                continue;
            }

            try{
                writeMethod.invoke(t, convertValue);
            } catch(IllegalAccessException | InvocationTargetException e) {
                throw new RuntimeException(e.getMessage(), e);
            }
        }
        return t;
    }

    /** 查找当前对象所在的类的第1个实现的泛型类型 */
    @SuppressWarnings("unchecked")
    public static <T> Class<T> findImplementedParameterizedType(Object obj) {
        Class<?> clazz = obj.getClass();

        ParameterizedType type = (ParameterizedType) clazz.getGenericSuperclass();
        Type actualTypeParam = type.getActualTypeArguments()[0];

        if(actualTypeParam instanceof ParameterizedType) {
            return (Class) ((ParameterizedType) actualTypeParam).getRawType();
        }
        if(actualTypeParam instanceof Class) {
            return (Class) actualTypeParam;
        }
        throw new UnsupportedOperationException("暂时不支持的类:" + clazz);
    }

    /**
     * 获取指定对象在工程类的真实类名(前提是此类为项目中的类)
     * <p/>
     * 此类的作用在于避免在某些场景中需要获取原始类的反射信息时，由于对象被子类化(使用javassist或cglib)之后，相应的信息被丢失了
     * 这里即去除相应的生成信息
     */
    public static <T> Class<?> getRealClass(T obj) {
        Class sourceClass = obj.getClass();
        String newbiPrefix = "com.ecnu.trivia";
        Class clazz = sourceClass;
        for(; clazz != Object.class; clazz = clazz.getSuperclass()) {
            String name = clazz.getName();

            //如果是cglib生成的，则忽略
            if(name.contains("CGLIB")) {
                continue;
            }

            if(name.startsWith(newbiPrefix)) {
                return clazz;
            }
        }

        return sourceClass;
    }
}

package com.ecnu.trivia.common.frameworkext.instead;

import com.ecnu.trivia.common.ApplicationContextHolder;

import com.ecnu.trivia.common.component.cache.CacheAspect;
import com.ecnu.trivia.common.component.mapper.Mapper;
import com.ecnu.trivia.common.component.mapperimpl.BaseMapperImpl;
import org.apache.ibatis.binding.MapperMethod;
import org.apache.ibatis.reflection.ExceptionUtil;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSession;

import java.lang.reflect.Method;

/**
 * 替换原mybatis的结果处理方式，以支持驼峰式字段
 * 同时支持通用mapper的 CRUD
 *
 * @author Jack Chen
 */
@SuppressWarnings({"unchecked", "unused"})
public class MapperProxyInstead<T> {
    private SqlSession sqlSession;

    public Object invoke(Object proxy, final Method method, final Object[] args) throws Throwable {
        Class<?> clazz = method.getDeclaringClass();
        if(Object.class.equals(clazz)) {
            try{
                return method.invoke(this, args);
            } catch(Throwable t) {
                throw ExceptionUtil.unwrapThrowable(t);
            }
        }

        CacheAspect cacheAspect = ApplicationContextHolder.getInstance(CacheAspect.class);
        final BaseMapperImpl baseMapperImpl = BaseMapperImpl.getInstance();

        //mapper接口
        if(clazz == Mapper.class) {
            final String methodName = method.getName();
            return cacheAspect.execute(baseMapperImpl.getCacheOperationInvoker(methodName, sqlSession, args), baseMapperImpl, method, args);
        }

        final MapperMethod mapperMethod = cachedMapperMethod(method);
        return cacheAspect.execute(baseMapperImpl.getDelegatedCacheOperationInvoker(mapperMethod, sqlSession, args), proxy, method, args);
    }

    private MapperMethod cachedMapperMethod(Method method) {
        //以下的实现没有任何作用，仅为防止出现此方法返回null的情况
        return new MapperMethod(Mapper.class, method, new Configuration());
    }
}

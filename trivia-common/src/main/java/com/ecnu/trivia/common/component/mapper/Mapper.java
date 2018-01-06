/**
 * Created by Jack Chen at 11/28/2014
 */
package com.ecnu.trivia.common.component.mapper;

import com.google.common.base.Function;
import com.ecnu.trivia.common.component.Page;
import com.ecnu.trivia.common.component.cache.utils.CacheClazz;
import com.ecnu.trivia.common.component.cache.utils.CacheValue;
import com.ecnu.trivia.common.component.domain.Domain;
import com.ecnu.trivia.common.component.domain.Key;
import org.apache.ibatis.session.SqlSession;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;

import java.util.List;

/**
 * 描述通用的数据查询接口
 *
 * @author Jack Chen
 */
public interface Mapper<T extends Domain> {
    String METHOD_SAVE = "save";//save方法 C
    String METHOD_GET = "get";//get方法 R
    String METHOD_LIST = "list";//list方法 R
    String METHOD_UPDATE = "update";//save方法 U
    String METHOD_DELETE = "delete";//delete方法 D
    String METHOD_DO_IN_SESSION = "doInSession";//doInSession方法

    /**
     * 保存对象 C
     */
    T save(T t);

    /**
     * 获取对象 R
     */
    T get(Key key, Class<T> clazz);

    /**
     * 获取所有对象,并进行分页
     */
    List<T> list(Class<T> clazz, Page page);

    /**
     * 更新对象,该对象必须存在被更新的值，即t.recorded 返回true U
     */
    T update(T t);

    /**
     * 删除对象 D
     */
    void delete(T t);

    /**
     * 在sqlSession中执行指定的操作
     */
    <X> X doInSession(Function<SqlSession, X> function);
}

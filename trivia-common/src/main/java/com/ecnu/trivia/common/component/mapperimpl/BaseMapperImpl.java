/** Created by Jack Chen at 11/30/2014 */
package com.ecnu.trivia.common.component.mapperimpl;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.ecnu.trivia.common.component.Page;
import com.ecnu.trivia.common.component.domain.*;
import com.ecnu.trivia.common.component.domain.utils.DomainUtils;
import com.ecnu.trivia.common.component.exception.Asserts;
import com.ecnu.trivia.common.component.mapper.Mapper;
import com.ecnu.trivia.common.exception.DataException;
import com.ecnu.trivia.common.util.ObjectUtils;
import org.apache.ibatis.binding.MapperMethod;
import org.apache.ibatis.session.SqlSession;
import org.springframework.cache.interceptor.CacheOperationInvoker;

import java.util.List;
import java.util.Map;
import java.util.Set;

/** @author Jack Chen */
public class BaseMapperImpl {
    private static final BaseMapperImpl instance = new BaseMapperImpl();
    private static final String NAME_SPACE_PREFIX = "com.ecnu.trivia.common.component.mapper.";

    public static final String FULL_STATEMENT_SAVE = NAME_SPACE_PREFIX + "BaseMapper.save";
    public static final String FULL_STATEMENT_SAVE_WITH_ID = NAME_SPACE_PREFIX + "BaseMapper.saveWithId";
    public static final String FULL_STATEMENT_DELETE = NAME_SPACE_PREFIX + "BaseMapper.delete";
    public static final String FULL_STATEMENT_GET = NAME_SPACE_PREFIX + "BaseMapper.get";
    public static final String FULL_STATEMENT_LIST = NAME_SPACE_PREFIX + "BaseMapper.list";
    public static final String FULL_STATEMENT_UPDATE = NAME_SPACE_PREFIX + "BaseMapper.update";

    public static final String STATEMENT_SAVE = "save";
    public static final String STATEMENT_SAVE_WITH_ID = "saveWithId";

    private BaseMapperImpl() {
    }

    public static BaseMapperImpl getInstance() {
        return instance;
    }

    /** 实现save语义 */
    public <T extends Domain> void save(T entity, SqlSession sqlSession) {
        Class<?> domainClass = DomainUtils.getDomainClass(entity.getClass());
        if(domainClass == null) {
            throw new IllegalArgumentException("该对象类不是domain配置对象:" + entity.getClass());
        }

        DomainTable domainTable = DomainUtils.getDomainTable(domainClass);
        assert domainTable != null;

        boolean shouldAutoGenerate = domainTable.getGeneratedKeyPropertyColumn() != null;

        if(shouldAutoGenerate) {
            Object generatedKey = DomainUtils.getGeneratedKeyValue(entity);
            shouldAutoGenerate = ObjectUtils.isLogicalNull(generatedKey);
        }

        String key = shouldAutoGenerate ? MybatisHelper.getDefaultStatementName(domainClass, STATEMENT_SAVE) : MybatisHelper
                .getDefaultStatementName(domainClass, STATEMENT_SAVE_WITH_ID);

        Map<String, Object> param = ImmutableMap.of("dt", domainTable, "p", entity);
        sqlSession.update(key, param);

        //验证保证之后的主键不为null
        Asserts.assertTrue(entity.key().isValid(), DataException.class, "保存之后主键不正确:{}", entity.key());
    }

    /** 实现delete语义 */
    public <T extends Domain> void delete(T t, SqlSession sqlSession) {
        Key key = t.key();
        Asserts.assertTrue(key.isValid(), "当前类没有主键信息:{}", key);

        @SuppressWarnings("unchecked")
        Class<T> tClass = (Class<T>) t.getClass();

        Map<String, Object> param = _generateKeyMap(key, tClass);

        sqlSession.delete(FULL_STATEMENT_DELETE, param);
    }

    /** 实现 doInSession 语义 */
    public <T> T doInSession(Function<SqlSession, T> function, SqlSession sqlSession) {
        return function.apply(sqlSession);
    }

    /** 实现 get 语义 */
    public <T extends Domain> T get(Key key, Class<T> clazz, SqlSession sqlSession) {
        Asserts.assertTrue(key.isValid(), "主键信息不完整:{}", key);

        //先判断子类是否实现了该接口,如果存在,则直接使用子类的实现

        Map<String, Object> param = _generateKeyMap(key, clazz);

        //追加实际的返回类型
        param.put("_clazz", clazz);

        return sqlSession.selectOne(FULL_STATEMENT_GET, param);
    }

    /** 实现 list 语义 */
    public <T extends Domain> List<T> list(Class<T> clazz, Page page, SqlSession sqlSession) {

        Map<String, Object> param = Maps.newHashMap();
        param.put("dt", DomainUtils.getDomainTable(clazz));

        //追加实际的返回类型
        param.put("_clazz", clazz);

        return sqlSession.selectList(FULL_STATEMENT_LIST, param, page);
    }

    /** 实现update 语义 */
    @SuppressWarnings("unchecked")
    public <T extends Domain> void update(T t, SqlSession sqlSession) {
        //如果此类未修改，则直接忽略
        if(!t.recorded()) {
            return;
        }

        Key key = t.key();
        Asserts.assertTrue(key.isValid(), "当前类没有主键信息:{}", key);

        @SuppressWarnings("unchecked")
        Class<T> tClass = (Class<T>) t.getClass();

        Map<String, Object> param = _generateKeyMap(key, tClass);

        Set<FieldModifiedRecord<?>> recordSet = t.recordData();
        List<FieldRecord<Comparable>> ucList = Lists.newArrayListWithCapacity(recordSet.size());//uc updateComparable 与pc相对应
        for(FieldModifiedRecord<?> fieldModifiedRecord : recordSet) {
            ucList.add((FieldRecord<Comparable>) fieldModifiedRecord.toNewRecord());
        }
        param.put("ucList", ucList);

        sqlSession.update(FULL_STATEMENT_UPDATE, param);
    }

    private static <T> Map<String, Object> _generateKeyMap(Key key, Class<T> clazz) {
        Class<?> domainClass = DomainUtils.getDomainClass(clazz);
        if(domainClass == null) {
            throw new IllegalArgumentException("该对象类不是domain配置对象:" + clazz);
        }

        DomainTable domainTable = DomainUtils.getDomainTable(domainClass);
        assert domainTable != null;

        List<PropertyColumn> keyColumnList = domainTable.getKeyColumnList();
        int keyColumnLength = keyColumnList.size();
        Comparable[] ids = key.getIds();

        List<FieldRecord<Comparable>> pcList = Lists.newArrayListWithCapacity(keyColumnList.size());
        for(int i = 0; i < keyColumnLength; i++) {
            pcList.add(new FieldRecord<>(keyColumnList.get(i), ids[i]));
        }

        Map<String, Object> param = Maps.newHashMap();
        param.put("dt", domainTable);
        param.put("pcList", pcList);

        return param;
    }

    /** 获取一个缓存调用执行器 */
    public CacheOperationInvoker getCacheOperationInvoker(final String methodName, final SqlSession sqlSession, final Object[] args) {
        return new CacheOperationInvoker() {
            @Override
            @SuppressWarnings("unchecked")
            public Object invoke() throws ThrowableWrapper {
                switch(methodName) {
                    case Mapper.METHOD_SAVE: {
                        save((Domain) args[0], sqlSession);
                        return args[0];
                    }
                    case Mapper.METHOD_DO_IN_SESSION: {
                        return doInSession((Function) args[0], sqlSession);
                    }
                    case Mapper.METHOD_DELETE: {
                        delete((Domain) args[0], sqlSession);
                        return null;
                    }
                    case Mapper.METHOD_GET: {
                        return get((Key) args[0], (Class) args[1], sqlSession);
                    }
                    case Mapper.METHOD_UPDATE: {
                        update((Domain) args[0], sqlSession);
                        return args[0];
                    }
                    case Mapper.METHOD_LIST: {
                        return list((Class) args[0], (Page) args[1], sqlSession);
                    }
                    default: {
                        throw new RuntimeException("还没有实现的方法名:" + methodName);
                    }
                }
            }
        };
    }

    /** 获取一个委拖的缓存调用执行器，将相应的调用委拖给指定的mapperMethod */
    public CacheOperationInvoker getDelegatedCacheOperationInvoker(final MapperMethod mapperMethod, final SqlSession sqlSession, final Object[] args) {
        return new CacheOperationInvoker() {
            @Override
            public Object invoke() throws ThrowableWrapper {
                return mapperMethod.execute(sqlSession, args);
            }
        };
    }
}

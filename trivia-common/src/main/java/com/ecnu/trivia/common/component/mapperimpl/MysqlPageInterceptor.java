package com.ecnu.trivia.common.component.mapperimpl;

import com.google.common.collect.Lists;
import com.ecnu.trivia.common.component.Page;
import com.ecnu.trivia.common.util.JoinerUtils;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.*;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

/**
 * 实现mysql自动分页
 *
 * @author Jack Chen
 */
@Intercepts({@Signature(type = Executor.class, method = "query",
        args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class})})
public class MysqlPageInterceptor implements Interceptor {
    private static final Logger logger = LoggerFactory.getLogger(MysqlPageInterceptor.class);

    private static final String START_INDEX = "startIndex_";
    private static final String END_INDEX = "endIndex_";
    private static final String PAGE_RESULT_MAP = "_page";

    static int MAPPED_STATEMENT_INDEX = 0;
    static int PARAMETER_INDEX = 1;
    static int ROWBOUND_INDEX = 2;
    static int RESULT_HANDLER_INDEX = 3;

    /** 不分页,或针对已经分页查询的数据 */
    private static final RowBounds NO_ROW_BOUNDS = RowBounds.DEFAULT;
    /** 是否已加载分页的resultMap */
    private boolean pageResultMapLoaded;

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        processIntercept((Executor) invocation.getTarget(), invocation.getArgs());
        return invocation.proceed();
    }

    void processIntercept(Executor executor, final Object[] args) throws SQLException {
        final RowBounds rowBounds = (RowBounds) args[ROWBOUND_INDEX];
        //只有提供分页信息时,才处理
        if(rowBounds == null) {
            return;
        }
        if(rowBounds.getLimit() == RowBounds.NO_ROW_LIMIT) {
            return;
        }
        boolean isPage = rowBounds instanceof Page;

        MappedStatement ms = (MappedStatement) args[MAPPED_STATEMENT_INDEX];
        Object parameter = args[PARAMETER_INDEX];
        BoundSql oldBoundSql = ms.getBoundSql(parameter);
        String oldSql = oldBoundSql.getSql().trim();

        //处理总记录数
        if(isPage) {
            Page page = (Page) rowBounds;
            //只有需要查询总数的时候才查询,避免重复查询
            if(page.getTotalCount() == 0) {
                String countSql = getCountSql(oldSql);
                BoundSql countBoundSql = buildNewBoundSql(ms, oldBoundSql, countSql, NO_ROW_BOUNDS);
                //这时将要resultType设置为long类型,以取得总记录数信息
                ResultMap pageResultMap = fetchPageResultMap(ms.getConfiguration());
                MappedStatement countMappedStatement = buildNewStatement(ms, new BoundSqlSqlSource(countBoundSql), Collections.singletonList(pageResultMap));
                List<Integer> list = executor.query(countMappedStatement, parameter, NO_ROW_BOUNDS, Executor.NO_RESULT_HANDLER);
                int totalCount = list.get(0);
                page.setTotalCount(totalCount);
            }
        }

        //避免结果集再处理一次
        args[ROWBOUND_INDEX] = NO_ROW_BOUNDS;

        //处理记录信息
        String pageSql = getPageSql(oldSql);
        BoundSql pageBoundSql = buildNewBoundSql(ms, oldBoundSql, pageSql, rowBounds);
        MappedStatement newMs = buildNewStatement(ms, new BoundSqlSqlSource(pageBoundSql), ms.getResultMaps());
        args[MAPPED_STATEMENT_INDEX] = newMs;
    }

    private ResultMap fetchPageResultMap(Configuration configuration) {
        if(!pageResultMapLoaded) {
            pageResultMapLoaded = true;
            //构建分页resultMap

            ResultMap.Builder resultMapBuilder = new ResultMap.Builder(configuration, PAGE_RESULT_MAP, int.class, Lists.<ResultMapping>newArrayList(), false);
            ResultMap resultMap = resultMapBuilder.build();

            try {
                configuration.addResultMap(resultMap);
            } catch(IllegalArgumentException ignore) {
                logger.warn("添加resultMap时出现重复性添加");
            }
        }
        return configuration.getResultMap(PAGE_RESULT_MAP);
    }

    private BoundSql buildNewBoundSql(MappedStatement ms, BoundSql boundSql, String sql, RowBounds rowBounds) {
        List<ParameterMapping> parameterMapping = Lists.newArrayList(boundSql.getParameterMappings());

        BoundSql newBoundSql = new BoundSql(ms.getConfiguration(), sql, parameterMapping, boundSql.getParameterObject());
        for(ParameterMapping mapping : boundSql.getParameterMappings()) {
            String prop = mapping.getProperty();
            if(boundSql.hasAdditionalParameter(prop)) {
                newBoundSql.setAdditionalParameter(prop, boundSql.getAdditionalParameter(prop));
            }
        }

        //处理额外的分页参数
        if(rowBounds.getLimit() != RowBounds.NO_ROW_LIMIT) {
            parameterMapping = newBoundSql.getParameterMappings();
            parameterMapping.add(new ParameterMapping.Builder(ms.getConfiguration(), START_INDEX, int.class).build());
            parameterMapping.add(new ParameterMapping.Builder(ms.getConfiguration(), END_INDEX, int.class).build());

            newBoundSql.setAdditionalParameter(START_INDEX, rowBounds.getOffset());
            newBoundSql.setAdditionalParameter(END_INDEX, rowBounds.getLimit());
        }
        return newBoundSql;
    }

    private MappedStatement buildNewStatement(MappedStatement ms, SqlSource newSqlSource, List<ResultMap> resultMapList) {
        final Configuration configuration = ms.getConfiguration();

        MappedStatement.Builder builder = new MappedStatement.Builder(configuration, ms.getId(), newSqlSource, ms.getSqlCommandType());

        builder.resource(ms.getResource());
        builder.fetchSize(ms.getFetchSize());
        builder.statementType(ms.getStatementType());
        builder.keyGenerator(ms.getKeyGenerator());
        builder.keyProperty(JoinerUtils.joinByComma(ms.getKeyProperties(), ""));
        builder.timeout(ms.getTimeout());
        builder.parameterMap(ms.getParameterMap());
        builder.resultMaps(resultMapList);
        builder.resultSetType(ms.getResultSetType());
        builder.cache(ms.getCache());
        builder.flushCacheRequired(ms.isFlushCacheRequired());
        builder.useCache(ms.isUseCache());

        return builder.build();
    }

    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    @Override
    public void setProperties(Properties properties) {
        //nothing to do
    }

    public static class BoundSqlSqlSource implements SqlSource {
        BoundSql boundSql;

        public BoundSqlSqlSource(BoundSql boundSql) {
            this.boundSql = boundSql;
        }

        @Override
        public BoundSql getBoundSql(Object parameterObject) {
            return boundSql;
        }
    }

    private static String getCountSql(String sql) {
        return "select count(1) from (" + sql + ") cnt_";
    }

    private static String getPageSql(String sql) {
        return sql + " limit ?,?";
    }
}

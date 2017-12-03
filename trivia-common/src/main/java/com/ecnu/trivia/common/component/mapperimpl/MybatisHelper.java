/** Created by Jack Chen at 2014/6/24 */
package com.ecnu.trivia.common.component.mapperimpl;

import com.ecnu.trivia.common.component.domain.DomainTable;
import com.ecnu.trivia.common.component.domain.PropertyColumn;
import com.ecnu.trivia.common.component.domain.utils.DomainUtils;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.session.Configuration;

/** @author Jack Chen */
public class MybatisHelper {

    /** 向mybatis configure添加自动生成的statement */
    public static void addAutoGenerateStatement(Class<?> domainClass, Configuration configuration) {
        //Base.saveWithId
        String baseSaveWithIdName = BaseMapperImpl.FULL_STATEMENT_SAVE_WITH_ID;
        String domainSaveWithIdName = getDefaultStatementName(domainClass, BaseMapperImpl.STATEMENT_SAVE_WITH_ID);
        MappedStatement mappedStatement = configuration.getMappedStatement(baseSaveWithIdName);
        mappedStatement = copyStatement(mappedStatement, domainSaveWithIdName).build();
        configuration.addMappedStatement(mappedStatement);

        //Base.save
        DomainTable domainTable = DomainUtils.getDomainTable(domainClass);
        PropertyColumn generatedKeyPropertyColumn = domainTable.getGeneratedKeyPropertyColumn();
        if(generatedKeyPropertyColumn != null) {
            String baseSaveName = BaseMapperImpl.FULL_STATEMENT_SAVE;
            String domainSaveName = getDefaultStatementName(domainClass, BaseMapperImpl.STATEMENT_SAVE);
            mappedStatement = configuration.getMappedStatement(baseSaveName);
            MappedStatement.Builder mappedStatementBuilder = copyStatement(mappedStatement, domainSaveName);
            //追加key column, keyProperty
            mappedStatementBuilder.keyColumn(generatedKeyPropertyColumn.getColumn());
            mappedStatementBuilder.keyProperty("p." + generatedKeyPropertyColumn.getProperty());
            mappedStatement = mappedStatementBuilder.build();
            configuration.addMappedStatement(mappedStatement);
        }
    }

    public static String getDefaultStatementName(Class<?> domainClass, String sourceStatementName) {
        return domainClass.getName() + ".DEFAULT." + sourceStatementName;
    }

    private static MappedStatement.Builder copyStatement(MappedStatement mappedStatement, String id) {
        MappedStatement.Builder statementBuilder = new MappedStatement.Builder(mappedStatement.getConfiguration(), id, mappedStatement.getSqlSource(),
                mappedStatement.getSqlCommandType());

        statementBuilder.resource(mappedStatement.getResource());
        statementBuilder.fetchSize(mappedStatement.getFetchSize());
        statementBuilder.statementType(mappedStatement.getStatementType());
        statementBuilder.keyGenerator(mappedStatement.getKeyGenerator());
        statementBuilder.databaseId(mappedStatement.getDatabaseId());
        statementBuilder.lang(mappedStatement.getLang());
        statementBuilder.resultOrdered(mappedStatement.isResultOrdered());

        return statementBuilder;
    }
}

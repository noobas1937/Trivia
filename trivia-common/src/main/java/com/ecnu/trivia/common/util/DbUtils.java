/** Created by Jack Chen at 12/1/2014 */
package com.ecnu.trivia.common.util;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * 数据库工具类
 *
 * @author Jack Chen
 */
public class DbUtils {

    /** 是否存在指定的数据表(视图) */
    public static boolean existsTable(Connection connection, String schema, String table) throws SQLException {
        try(ResultSet resultSet = connection.getMetaData().getTables(null, schema, table, null)){
            return resultSet.next();
        }
    }

    /** 删除数据表 */
    public static void dropTable(Connection connection, String schema, String table) throws SQLException {
        String dropTableDdl = "drop table ";
        if(org.springframework.util.StringUtils.hasText(schema)) {
            dropTableDdl += schema + ".";
        }
        dropTableDdl += table;

        try(Statement statement = connection.createStatement()){
            statement.executeUpdate(dropTableDdl);
        }
    }

    /** 根据指定的ddl语句创建数据表(或视图) */
    public static void createTable(Connection connection, String schema, String table, String createDdl) throws SQLException {
        if(existsTable(connection, schema, table)) {
            return;
        }

        try(Statement statement = connection.createStatement()){
            statement.executeUpdate(createDdl);
        }
    }

    /** 查询指定数据表的容量 */
    public static long countTable(Connection connection, String schema, String table) throws SQLException {
        String sql = "select count(1) from ";
        if(!ObjectUtils.isLogicalNull(schema)) {
            sql += schema + ".";
        }
        sql += table;

        try(Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery(sql)){
            rs.next();
            return rs.getLong(1);
        }
    }

    /** 查询指定语句，并期望返回相应的数量 */
    public static long countData(Connection connection, String sql) throws SQLException {
        try(Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery(sql)){
            rs.next();
            return rs.getLong(1);
        }
    }
}

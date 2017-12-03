/** Created by Jack Chen at 12/1/2014 */
package com.ecnu.trivia.common.util;

import com.ecnu.trivia.common.component.test.testng.BaseTest;
import org.testng.Assert;
import org.testng.annotations.Test;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

/** @author Jack Chen */
public class DbUtilsTest extends BaseTest {

    @Resource
    private DataSource dataSource;

    @Test
    public void testExistsTable() throws SQLException {
        //1 不存在的数据表
        String table = "_t";
        try(Connection connection = dataSource.getConnection()){
            Assert.assertFalse(DbUtils.existsTable(connection, null, table));
        }

        //2 存在的数据表
        table = "_exist_t";
        try(Connection connection = dataSource.getConnection()){
            String tableDdl = "create table " + table + "(id bigint)";
            connection.createStatement().executeUpdate(tableDdl);

            Assert.assertTrue(DbUtils.existsTable(connection, null, table));
        } finally {
            String dropDdl = "drop table " + table;
            try(Connection connection = dataSource.getConnection()){
                connection.createStatement().executeUpdate(dropDdl);
            }
        }
    }

    @Test
    public void testCreateTable() throws SQLException {
        //创建一个新表
        String table = "_exist_t2";
        String tableDdl = "create table " + table + "(id bigint)";
        try(Connection connection = dataSource.getConnection()){
            DbUtils.createTable(connection, null, table, tableDdl);
            Assert.assertTrue(DbUtils.existsTable(connection, null, table));
        } finally {
            //删除之
            String dropDdl = "drop table " + table;
            try(Connection connection = dataSource.getConnection()){
                connection.createStatement().executeUpdate(dropDdl);
            }
        }
    }

    @Test
    public void testDropTable() throws SQLException {
        //先创建一个新表，然后再删除之，判断其肯定不存在
        String table = "_drop_t";
        String tableDdl = "create table " + table + "(id bigint)";
        try(Connection connection = dataSource.getConnection()){
            DbUtils.createTable(connection, null, table, tableDdl);
            Assert.assertTrue(DbUtils.existsTable(connection, null, table));//刚创建，肯定存在

            //删除之后不存在
            DbUtils.dropTable(connection, null, table);
            Assert.assertFalse(DbUtils.existsTable(connection, null, table));
        }
    }

    @Test
    public void testCountTable() throws Exception {
        //先创建一个新的数据，则其数据量0

        String table = "_tmp_t";

        //先删除相应的数据表
        try(Connection connection = dataSource.getConnection()){
            DbUtils.dropTable(connection, null, table);
        } catch(Exception ignore) {
        }

        try(Connection connection = dataSource.getConnection()){
            String createDdl = "create table " + table + "(id bigint)";
            DbUtils.createTable(connection, null, table, createDdl);
        }

        try(Connection connection = dataSource.getConnection()){
            long count = DbUtils.countTable(connection, null, table);
            Assert.assertEquals(count, 0, "新创建表数据量不为0");
        }

        //新添加一条数据，则在之后其数据应该为1
        String insertSql = "insert into " + table + " values(-1)";
        try(Connection connection = dataSource.getConnection();
            Statement statement = connection.createStatement()){
            statement.executeUpdate(insertSql);
        }

        try(Connection connection = dataSource.getConnection()){
            long count = DbUtils.countTable(connection, null, table);
            Assert.assertEquals(count, 1, "新建数据表添加数据后数据不为1");
        }

        //删除相应的数据表
        try(Connection connection = dataSource.getConnection()){
            DbUtils.dropTable(connection, null, table);
        }
    }

    /** 测试查数量信息 */
    @Test
    public void testCountData() throws Exception {
        //先创建新的数据库 则新表查询数为1,然后，插入一条记录，相应的数据量为1,再插入记录，追加查询过滤条件，再查询数量

        String table = "_tmp_t";

        //先删除相应的数据表
        try(Connection connection = dataSource.getConnection()){
            DbUtils.dropTable(connection, null, table);
        } catch(Exception ignore) {
        }

        try(Connection connection = dataSource.getConnection()){
            String createDdl = "create table " + table + "(id bigint)";
            DbUtils.createTable(connection, null, table, createDdl);
        }

        //初始数据量为0
        String countSql = "select count(1) from " + table;
        try(Connection connection = dataSource.getConnection()){
            Assert.assertEquals(DbUtils.countData(connection, countSql), 0);
        }

        //插入新数据，再查询
        //新添加一条数据，则在之后其数据应该为1
        String insertSql = "insert into " + table + " values(-1)";
        try(Connection connection = dataSource.getConnection();
            Statement statement = connection.createStatement()){
            statement.executeUpdate(insertSql);
        }
        try(Connection connection = dataSource.getConnection()){
            Assert.assertEquals(DbUtils.countData(connection, countSql), 1);
        }

        //再插入新数据，追加条件查询
        insertSql = "insert into " + table + " values(-2)";
        try(Connection connection = dataSource.getConnection();
            Statement statement = connection.createStatement()){
            statement.executeUpdate(insertSql);
        }
        try(Connection connection = dataSource.getConnection()){
            Assert.assertEquals(DbUtils.countData(connection, countSql), 2);

            countSql = "select count(1) from " + table + " where id = -1";
            Assert.assertEquals(DbUtils.countData(connection, countSql), 1);
        }

        //最后删除相应的数据表
        try(Connection connection = dataSource.getConnection()){
            DbUtils.dropTable(connection, null, table);
        }
    }
}

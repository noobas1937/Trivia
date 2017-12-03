/** Created by Jack Chen at 11/30/2014 */
package com.ecnu.trivia.common.component.mapper;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.ecnu.trivia.common.component.Page;
import com.ecnu.trivia.common.component.domain.DomainTable;
import com.ecnu.trivia.common.component.domain.Key;
import com.ecnu.trivia.common.component.domain.utils.DomainUtils;
import com.ecnu.trivia.common.component.mapper.testmapper.TestT;
import com.ecnu.trivia.common.component.mapper.testmapper.mapper.TestTMapper;
import com.ecnu.trivia.common.component.test.testng.AssertEx;
import com.ecnu.trivia.common.component.test.testng.BaseTest;
import com.ecnu.trivia.common.util.DbUtils;
import com.ecnu.trivia.common.util.DomainCsvUtils;
import org.apache.ibatis.session.SqlSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import javax.annotation.Resource;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;


/**
 * 对通用mapper实现的测试
 *
 * @author Jack Chen
 */
public class MapperTest extends BaseTest {
    private static final Logger logger = LoggerFactory.getLogger(MapperTest.class);

    @Resource
    private TestTMapper testTMapper;

    @DataProvider
    public Object[][] p4testSave() {
        List<Object[]> objectsList = Lists.newArrayList();
        final int paramLength = 1;
        Object[] objects;

        //1 没有主键的对象
        objects = new Object[paramLength];
        TestT testT = new TestT();
        testT.setName("测试1");

        objects[0] = testT;
        objectsList.add(objects);

        //2 有主键的对象
        objects = new Object[paramLength];
        testT = new TestT();
        testT.setId(-1);
        testT.setName("测试2");

        objects[0] = testT;
        objectsList.add(objects);

        return objectsList.toArray(new Object[objectsList.size()][]);
    }

    private void _guaranteeTableExists() {
        testTMapper.doInSession(new Function<SqlSession, Void>() {
            public Void apply(SqlSession sqlSession) {
                DomainTable domainTable = DomainUtils.getDomainTable(TestT.class);
                assert domainTable != null;

                try{
                    DbUtils.createTable(sqlSession.getConnection(), null, domainTable.getTable(), TestT.TABLE_DDL);
                } catch(SQLException e) {
                    throw new RuntimeException(e.getMessage(), e);
                }
                return null;
            }
        });
    }

    /** 测试保存的情况 */
    @Test(dataProvider = "p4testSave")
    public void testSave(TestT testT) {
        //先保证指定的数据表存在
        _guaranteeTableExists();

        testTMapper.save(testT);

        logger.debug("testSave:插入成功:{}", testT.getId());

        AssertEx.assertTrue(testT.getId() != 0, "不能正确地保存对象");
    }

    @DataProvider
    public Object[][] p4testDelete() {
        List<Object[]> objectsList = Lists.newArrayList();
        final int paramLength = 1;
        Object[] objects;

        //1 正常的对象
        objects = new Object[paramLength];
        TestT testT = new TestT();
        testT.setName("_姓名");
        objects[0] = testT;
        objectsList.add(objects);

        return objectsList.toArray(new Object[objectsList.size()][]);
    }

    /** 测试删除的情况 */
    @Test(dataProvider = "p4testDelete")
    public void testDelete(final TestT testT) {
        _guaranteeTableExists();

        //先保证数据存在
        testTMapper.save(testT);
        Assert.assertTrue(testT.getId() != 0, "保存对象失败");

        testTMapper.delete(testT);

        testTMapper.doInSession(new Function<SqlSession, Void>() {
            public Void apply(SqlSession sqlSession) {
                try(PreparedStatement ps = sqlSession.getConnection().prepareStatement(TestT.SQL_FIND_BY_ID)){
                    ps.setLong(1, testT.getId());
                    ResultSet rs = ps.executeQuery();

                    Assert.assertTrue(!rs.next());//保证没有数据
                } catch(SQLException e) {
                    throw new RuntimeException(e.getMessage(), e);
                }

                return null;
            }
        });
    }

    @Test
    public void testDoInSession() {
        //测试内容 在回调中能获取到相应的对象
        testTMapper.doInSession(new Function<SqlSession, Void>() {
            public Void apply(SqlSession sqlSession) {
                Assert.assertTrue(sqlSession != null);
                Assert.assertTrue(sqlSession.getConnection() != null);

                return null;
            }
        });
    }

    @DataProvider
    public Object[][] p4testGet() {
        List<Object[]> objectsList = Lists.newArrayList();
        final int paramLength = 3;
        Object[] objects;

        //1 正常的数据集
        objects = new Object[paramLength];
        TestT t1 = DomainCsvUtils.get("T1", TestT.class);
        objects[0] = Lists.newArrayList(t1, DomainCsvUtils.get("T2", TestT.class));
        objects[1] = t1.key();
        objects[2] = t1;
        objectsList.add(objects);

        return objectsList.toArray(new Object[objectsList.size()][]);
    }

    /** 测试get */
    @Test(dataProvider = "p4testGet")
    public void testGet(List<TestT> insertList, Key key, TestT expectValue) {
        //先保证指定的数据表存在
        _guaranteeTableExists();

        for(TestT testT : insertList) {
            testTMapper.save(testT);
        }

        TestT value = testTMapper.get(key, TestT.class);

        Assert.assertEquals(value, expectValue);
    }

    @DataProvider
    public Object[][] p4testList() {
        List<Object[]> objectsList = Lists.newArrayList();
        final int paramLength = 3;
        Object[] objects;

        //准备数据 T1 T2
        TestT testT_T1 = DomainCsvUtils.get("T1", TestT.class);
        TestT testT_T2 = DomainCsvUtils.get("T2", TestT.class);

        //1 准备数据 t1 t2，列出所有数据能获取相应的数据
        objects = new Object[paramLength];
        objects[0] = Lists.newArrayList(testT_T1, testT_T2);
        objects[1] = Page.NO_PAGE;
        objects[2] = Lists.newArrayList(testT_T1.clone(), testT_T2.clone());
        objectsList.add(objects);

        //2 准备数据 t1 t2，每页1个，列出第1页能获取t1
        objects = new Object[paramLength];
        objects[0] = Lists.newArrayList(testT_T1, testT_T2);
        objects[1] = new Page(1, 1);
        objects[2] = Lists.newArrayList(testT_T2.clone());//因为这里id值是负数，因此第1条为-2即T2
        objectsList.add(objects);

        return objectsList.toArray(new Object[objectsList.size()][]);
    }

    /** 测试list */
    @Test(dataProvider = "p4testList")
    public void testList(List<TestT> insertList, Page page, List<TestT> expectValueList) {
        //先保证指定的数据表存在
        _guaranteeTableExists();

        save(insertList, testTMapper);

        List<TestT> resultList = testTMapper.list(TestT.class, page);

        AssertEx.assertEqualsNoOrder(resultList, expectValueList);
    }

    @DataProvider
    public Object[][] p4testUpdate() {
        List<Object[]> objectsList = Lists.newArrayList();
        final int paramLength = 2;
        Object[] objects;

        //准备数据 T1
        TestT testT = DomainCsvUtils.get("T1", TestT.class);

        //1 已存在1条数据，对数据作修改，则应当被更新
        objects = new Object[paramLength];
        objects[0] = Lists.newArrayList(testT);
        TestT updateT = testT.clone();
        updateT.record(true);//修改标记启动
        updateT.setName("__newName");
        objects[1] = updateT;
        objectsList.add(objects);

        return objectsList.toArray(new Object[objectsList.size()][]);
    }

    /** 测试update */
    @Test(dataProvider = "p4testUpdate")
    public void testUpdate(List<TestT> insertList, final TestT updateTestT) {
        _guaranteeTableExists();
        save(insertList, testTMapper);

        testTMapper.update(updateTestT);

        //验证数据库中值是否已更新
        testTMapper.doInSession(new Function<SqlSession, Object>() {
            @Override
            public Object apply(SqlSession input) {
                try(PreparedStatement ps = input.getConnection().prepareStatement(TestT.SQL_FIND_BY_ID)){
                    ps.setLong(1, updateTestT.getId());
                    ResultSet rs = ps.executeQuery();
                    Assert.assertTrue(rs.next());//保证有数据

                    String name = rs.getString("name");
                    Assert.assertEquals(updateTestT.getName(), name);
                } catch(SQLException e) {
                    throw new RuntimeException(e.getMessage(), e);
                }

                return null;
            }
        });
    }
}

package com.ecnu.trivia.common.util;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class JoinerUtilsTest {

    @DataProvider
    public Object[][] p4testJoinByCommaObject() {
        List<Object[]> objectsList = Lists.newArrayList();
        final int paramLength = 3;
        final Object[] param1 = new Object[]{"a", 1, "b", null};
        Object[] objects;

        //1 null使用默认null
        objects = new Object[paramLength];
        objects[0] = param1;
        objects[1] = null;
        objects[2] = "a,1,b,null";
        objectsList.add(objects);

        //2 null使用""
        objects = new Object[paramLength];
        objects[0] = param1;
        objects[1] = "";
        objects[2] = "a,1,b,";
        objectsList.add(objects);

        return objectsList.toArray(new Object[objectsList.size()][]);
    }

    /** 测试对对象数据进行连接 */
    @Test(dataProvider = "p4testJoinByCommaObject")
    public void testJoinByCommaObject(Object[] objects, String nullValue, String expectValue) {
        String value = JoinerUtils.joinByComma(objects, nullValue);

        Assert.assertEquals(value, expectValue);
    }

    @DataProvider
    public Object[][] p4testJoinByCommaInt() {
        List<Object[]> objectsList = Lists.newArrayList();
        final int paramLength = 2;
        Object[] objects;

        //1 正常的数据
        objects = new Object[paramLength];
        objects[0] = new int[]{-1, 0, 1, 2};
        objects[1] = "-1,0,1,2";
        objectsList.add(objects);

        return objectsList.toArray(new Object[objectsList.size()][]);
    }

    @Test(dataProvider = "p4testJoinByCommaInt")
    public void testJoinByCommaInt(int[] ints, String expectValue) {
        String value = JoinerUtils.joinByComma(ints);

        Assert.assertEquals(value, expectValue);
    }

    @DataProvider
    public Object[][] p4testJoinByCommaLong() {
        List<Object[]> objectsList = Lists.newArrayList();
        final int paramLength = 2;
        Object[] objects;

        //1 正常的数据
        objects = new Object[paramLength];
        objects[0] = new long[]{-1L, 0L, 1L, 2L};
        objects[1] = "-1,0,1,2";
        objectsList.add(objects);

        return objectsList.toArray(new Object[objectsList.size()][]);
    }

    @Test(dataProvider = "p4testJoinByCommaLong")
    public void testJoinByCommaLong(long[] longs, String expectValue) {
        String value = JoinerUtils.joinByComma(longs);

        Assert.assertEquals(value, expectValue);
    }

    @DataProvider
    public Object[][] p4testJoinByCommaCollection() {
        List<Object[]> objectsList = Lists.newArrayList();
        final int paramLength = 3;
        Object[] objects;

        //1 null使用null填充
        objects = new Object[paramLength];
        objects[0] = Lists.newArrayList("a", 2, null, "b");
        objects[1] = null;
        objects[2] = "a,2,null,b";
        objectsList.add(objects);

        //2 null使用""填充
        objects = new Object[paramLength];
        objects[0] = Lists.newArrayList("a", 2, null, "b");
        objects[1] = "";
        objects[2] = "a,2,,b";
        objectsList.add(objects);

        //3 其它collection类
        objects = new Object[paramLength];
        Set<Object> sets = Sets.newLinkedHashSet();//使用linked保证顺序
        sets.add("a");
        sets.add(2);
        sets.add(null);
        sets.add("b");
        objects[0] = sets;
        objects[1] = "";
        objects[2] = "a,2,,b";
        objectsList.add(objects);

        return objectsList.toArray(new Object[objectsList.size()][]);
    }

    @Test(dataProvider = "p4testJoinByCommaCollection")
    public <T> void testJoinByCommaCollection(Collection<T> collection, String nullValue, String expectValue) {
        String value = JoinerUtils.joinByComma(collection, nullValue);

        Assert.assertEquals(value, expectValue);
    }

    @DataProvider
    public Object[][] p4testJoinMap() {
        List<Object[]> objectsList = Lists.newArrayList();
        final int paramLength = 5;
        Object[] objects;

        //1 正常的数据，空值为双引号， 连接符均为双引号,能够正常连接(为保证顺序，采用treeMap)
        objects = new Object[paramLength];
        Map<String, String> map = Maps.newTreeMap();
        map.put("a", "a12");
        map.put("b", "b34");
        map.put("c", null);
        objects[0] = map;
        objects[1] = "";
        objects[2] = "";
        objects[3] = "";
        objects[4] = "aa12bb34c";
        objectsList.add(objects);

        //2 正常数据，这值为 -,kv连接为=,节点连接采用&
        objects = new Object[paramLength];
        map = Maps.newTreeMap();
        map.put("a_", "a12");
        map.put("b_", "b34");
        map.put("c_", null);
        map.put("d_", "d56");
        objects[0] = map;
        objects[1] = "-";
        objects[2] = "=";
        objects[3] = "&";
        objects[4] = "a_=a12&b_=b34&c_=-&d_=d56";
        objectsList.add(objects);

        return objectsList.toArray(new Object[objectsList.size()][]);
    }

    /** 测试连接map串的情况 */
    @Test(dataProvider = "p4testJoinMap")
    public void testJoinMap(Map<String, String> map, String nullValue, String kvConnector, String entryConnector, String expectValue) throws Exception {
        String result = JoinerUtils.join(map, nullValue, kvConnector, entryConnector);

        Assert.assertEquals(result, expectValue);
    }
}

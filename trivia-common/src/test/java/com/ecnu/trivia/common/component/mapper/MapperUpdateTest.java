/** Created by Jack Chen at 12/25/2014 */
package com.ecnu.trivia.common.component.mapper;

import com.google.common.collect.Lists;
import com.ecnu.trivia.common.ApplicationContextHolder;
import com.ecnu.trivia.common.component.test.testng.BaseTest;
import com.ecnu.trivia.common.util.DomainCsvUtils;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.lang.reflect.Method;
import java.util.List;

/**
 * 实现对mappe.update的测试
 * 此方法内的所有对象均使用反射，因避免在生成class时在class文件中保留class 引用信息，从而在进行测试进提前对domain类进行解析
 *
 * @author Jack Chen
 */
public class MapperUpdateTest extends BaseTest {
    @DataProvider
    public Object[][] p4testUpdate() throws Exception {
        Class<?> testTClass = Class.forName("com.ecnu.trivia.common.component.mapper.testmapper.TestT");
        List<Object[]> objectsList = Lists.newArrayList();
        final int paramLength = 3;
        Object[] objects;

        Object testTT1 = DomainCsvUtils.get("T1", testTClass);
        Object testTT2 = DomainCsvUtils.get("T2", testTClass);
        Method cloneMethod = testTClass.getMethod("clone");
        Method recordMethod = testTClass.getMethod("record", boolean.class);
        Method setNameMethod = testTClass.getMethod("setName", String.class);


        //1 正常的数据，数据T2 将name从test2修改为 __name
        objects = new Object[paramLength];
        objects[0] = Lists.newArrayList(testTT1, testTT2);
        Object testT = cloneMethod.invoke(testTT2);
        recordMethod.invoke(testT, true);
        setNameMethod.invoke(testT, "__name");

        objects[1] = testT;
        Object expectValue = cloneMethod.invoke(testTT2);
        setNameMethod.invoke(expectValue, "__name");
        objects[2] = expectValue;
        objectsList.add(objects);

        //1 正常的数据，数据T2 将name从test2修改为 __name,再修改为 __name2，再修改为 __name3,能够最终更新为正确的数据
        objects = new Object[paramLength];
        objects[0] = Lists.newArrayList(testTT1, testTT2);
        testT = cloneMethod.invoke(testTT2);
        recordMethod.invoke(testT, true);
        setNameMethod.invoke(testT, "__name");
        setNameMethod.invoke(testT, "__name2");
        setNameMethod.invoke(testT, "__name3");
        objects[1] = testT;
        expectValue = cloneMethod.invoke(testTT2);
        setNameMethod.invoke(expectValue, "__name3");
        objects[2] = expectValue;
        objectsList.add(objects);

        return objectsList.toArray(new Object[objectsList.size()][]);
    }

    /**
     * 测试update
     * <p/>
     * 使用object避免提前对domain对象解析
     */
    @Test(dataProvider = "p4testUpdate")
    public void testUpdate(List<Object> insertObjectList, Object testTObject, Object expectValue) throws Exception {
        Class<?> testTMapperClass = Class.forName("com.ecnu.trivia.common.component.mapper.testmapper.mapper.TestTMapper");
        Class<?> keyClass = Class.forName("com.ecnu.trivia.common.component.domain.Key");
        Class<?> testTClass = Class.forName("com.ecnu.trivia.common.component.mapper.testmapper.TestT");
        Class<?> domainClass = Class.forName("com.ecnu.trivia.common.component.domain.Domain");
        Mapper testTMapper = (Mapper) ApplicationContextHolder.getInstance(testTMapperClass);

        @SuppressWarnings("unchecked")
        List insertList = (List) insertObjectList;
        Method saveMethod = testTMapperClass.getMethod("save", domainClass);

        //先保存相应的数据值
        for(Object insert : insertList) {
            saveMethod.invoke(testTMapper, insert);
        }

        Method updateMethod = testTMapperClass.getMethod("update", domainClass);
        updateMethod.invoke(testTMapper, testTObject);

        Method keyMethod = testTClass.getMethod("key");
        Object key = keyMethod.invoke(testTObject);

        Method getMethod = testTMapperClass.getMethod("get", keyClass, Class.class);
        Object getValue = getMethod.invoke(testTMapper, key, testTClass);
        Assert.assertEquals(getValue, expectValue);
    }
}

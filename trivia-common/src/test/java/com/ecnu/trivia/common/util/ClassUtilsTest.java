/** Created by Jack Chen at 2014/4/29 */
package com.ecnu.trivia.common.util;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.ecnu.trivia.common.util.classutils.*;
import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.NoOp;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.Date;
import java.util.List;
import java.util.Map;

/** @author Jack Chen */
public class ClassUtilsTest {

    /** 当有公共无参构建函数时，应该返回数据信息 */
    @Test
    public void testNewInstanceNormal() {
        Class clazz = Date.class;
        Object object = ClassUtils.newInstance(clazz);
        Assert.assertNotNull(object);
    }

    /** 当无公共无参构建函数时，应该报错 */
    @Test(expectedExceptions = RuntimeException.class)
    public void testNewInstanceNoPublicConstructor() {
        Class clazz = Runtime.class;
        ClassUtils.newInstance(clazz);
    }

    /** 当有构建函数，但并不是无参构建时，应该报错 */
    @Test(expectedExceptions = RuntimeException.class)
    public void testNewInstanceNoEmptyParamConstructor() {
        Class clazz = NotEmptyParamConstructorClass.class;
        ClassUtils.newInstance(clazz);
    }

    /** 当为接口时，应该报错 */
    @Test(expectedExceptions = RuntimeException.class)
    public void testNewInstanceWithInterface() {
        Class clazz = List.class;
        ClassUtils.newInstance(clazz);
    }

    /** 正常测试通过 */
    @Test
    public void testSubInstanceNormal() {
        SubClassA subClassA = new SubClassA();
        subClassA.setName("测试名称");

        SubClassA1 subClassA1 = ClassUtils.subInstance(subClassA, SubClassA1.class);
        //转换结果的name值应该相同
        Assert.assertEquals(subClassA1.getName(), subClassA.getName());
    }

    /** 当不是子类时，应该报错 */
    @Test(expectedExceptions = RuntimeException.class)
    public void testSubInstanceNotSubClass() {
        SubClassA subClassA = new SubClassA();
        ClassUtils.subInstance(subClassA, (Class) SubClassA1Not.class);
    }

    /** 当是子类但没有无类结构函数时，应该报错 */
    @Test(expectedExceptions = RuntimeException.class)
    public void testSubInstanceNoEmptyConstructor() {
        SubClassA subClassA = new SubClassA();
        ClassUtils.subInstance(subClassA, SubClassA2NoEmptyConstructor.class);
    }

    @DataProvider
    public Object[][] p4testCopyInstanceByMap() {
        List<Object[]> objectsList = Lists.newArrayList();
        final int paramLength = 3;
        Object[] objects;

        //1 正常的数据，使用类InstanceA,属性id,username,password,editFlag
        objects = new Object[paramLength];
        Map<String, Object> map = Maps.newHashMap();
        map.put("id", -1L);
        map.put("username", "_username");
        map.put("password", "_password");
        map.put("editFlag", "true");//带类型转换
        objects[0] = map;
        objects[1] = InstanceA.class;
        objects[2] = new InstanceA(-1L, "_username", "_password", true);
        objectsList.add(objects);

        return objectsList.toArray(new Object[objectsList.size()][]);
    }

    /** 测试使用map进行对象构建 */
    @Test(dataProvider = "p4testCopyInstanceByMap")
    public <T> void testCopyInstanceByMap(Map<String, Object> map, Class<T> clazz, T expectValue) {
        T value = ClassUtils.copyInstance(map, clazz);

        Assert.assertEquals(value, expectValue);
    }

    @DataProvider
    public Object[][] p4testCopyInstance() {
        List<Object[]> objectsList = Lists.newArrayList();
        final int paramLength = 3;
        Object[] objects;

        //1 正常的数据，由instanceA转换为instanceB
        objects = new Object[paramLength];
        objects[0] = new InstanceA(-1L, "_username", "2014-12-12", true);
        objects[1] = InstanceB.class;
        //id 类型转换 password类型转换 额外的属性忽略
        objects[2] = new InstanceB(-1, "_username", DateUtils.buildDate(2014, 11, 12));
        objectsList.add(objects);

        return objectsList.toArray(new Object[objectsList.size()][]);
    }

    /** 测试使用对象copy进行对象构建 */
    @Test(dataProvider = "p4testCopyInstance")
    public <S, D> void testCopyInstance(S source, Class<D> dClass, D expectValue) {
        D value = ClassUtils.copyInstance(source, dClass);

        Assert.assertEquals(value, expectValue);
    }

    @DataProvider
    public Object[][] p4testFindImplementedParameterizedType() {
        List<Object[]> objectsList = Lists.newArrayList();
        final int paramLength = 2;
        Object[] objects;

        //1 一个泛型接口，其有对应的泛型实现
        objects = new Object[paramLength];
        objects[0] = new Interface1Impl();
        objects[1] = List.class;//interfaceImpl泛型参数为List类
        objectsList.add(objects);

        //2 自实现的list类
        objects = new Object[paramLength];
        objects[0] = new ListImpl();
        objects[1] = String.class;//ListImpl泛型参数为String类
        objectsList.add(objects);

        return objectsList.toArray(new Object[objectsList.size()][]);
    }

    /** 测试查询实现的泛型接口的泛型类 */
    @Test(dataProvider = "p4testFindImplementedParameterizedType")
    public void testFindImplementedParameterizedType(Object object, Class expectType) {
        Class<?> type = ClassUtils.findImplementedParameterizedType(object);

        Assert.assertEquals(type, expectType);
    }

    @DataProvider
    public Object[][] p4testGetRealClass() {
        List<Object[]> objectsList = Lists.newArrayList();
        final int paramLength = 2;
        Object[] objects;

        //1 如果原类即为真实的newbi类，则返回类本身
        objects = new Object[paramLength];
        objects[0] = new InstanceB();
        objects[1] = InstanceB.class;

        //2 如果类为cglib生成的类，则返回父类
        objects = new Object[paramLength];
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(InstanceA.class);
        enhancer.setCallback(NoOp.INSTANCE);
        Object proxy = enhancer.create();
        objects[0] = proxy;
        objects[1] = InstanceA.class;
        objectsList.add(objects);

        return objectsList.toArray(new Object[objectsList.size()][]);
    }

    /** 测试获取真实的类 */
    @Test(dataProvider = "p4testGetRealClass")
    public void testGetRealClass(Object obj, Class<?> expectClass) throws Exception {
        Class<?> result = ClassUtils.getRealClass(obj);

        Assert.assertEquals(result, expectClass);
    }
}

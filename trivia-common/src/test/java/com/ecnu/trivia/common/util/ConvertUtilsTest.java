package com.ecnu.trivia.common.util;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.ecnu.trivia.common.util.converter.EnumTest;
import org.springframework.core.convert.converter.Converter;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.Date;
import java.util.List;
import java.util.Map;

public class ConvertUtilsTest {

    @Test
    public void testCanConvert() {
        //添加一个从list到map的转换，期望能够进行转换
        ConvertUtils.addConverter(new Converter<List, Map>() {
            @Override
            public Map convert(List source) {
                return null;
            }
        });

        Assert.assertTrue(ConvertUtils.canConvert(List.class, Map.class));
    }

    @DataProvider
    public Object[][] p4testConvert() {
        List<Object[]> objectsList = Lists.newArrayList();
        final int paramLength = 4;
        Object[] objects;

        //1 已注册的转换，从字符串转为日期
        String strSource = "2014-12-12";
        objects = new Object[paramLength];
        objects[0] = null;
        objects[1] = strSource;
        objects[2] = Date.class;
        objects[3] = DateUtils.parseDate(strSource);
        objectsList.add(objects);

        //2 自定义转换值,从List到Map
        final Map<String, String> expectValue = Maps.newHashMap();
        expectValue.put("_a", "_b");
        Converter<List, Map> listMapConverter = new Converter<List, Map>() {
            @Override
            public Map convert(List source) {
                Map<String, String> map = Maps.newHashMap();
                map.putAll(expectValue);
                return map;
            }
        };
        objects = new Object[paramLength];
        objects[0] = listMapConverter;
        objects[1] = Lists.newArrayList();
        objects[2] = Map.class;
        objects[3] = expectValue;
        objectsList.add(objects);

        //3
        objects = new Object[paramLength];
        objects[0] = null;
        objects[1] = "A";
        objects[2] = EnumTest.class;
        objects[3] = EnumTest.A;
        objectsList.add(objects);

        return objectsList.toArray(new Object[objectsList.size()][]);
    }

    @Test(dataProvider = "p4testConvert")
    public <S, D> void testConvert(Converter<S, D> converter, S source, Class<D> dClass, D expectValue) {
        if(converter != null)
            ConvertUtils.addConverter(converter);

        D value = ConvertUtils.convert(source, dClass);

        Assert.assertEquals(value, expectValue);
    }
}

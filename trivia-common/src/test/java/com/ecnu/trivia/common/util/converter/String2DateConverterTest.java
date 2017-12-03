package com.ecnu.trivia.common.util.converter;

import com.google.common.collect.Lists;
import com.ecnu.trivia.common.util.DateUtils;
import org.springframework.core.convert.converter.Converter;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.Date;
import java.util.List;

public class String2DateConverterTest {

    @DataProvider
    public Object[][] p4testConvert() {
        List<Object[]> objectsList = Lists.newArrayList();
        final int paramLength = 2;
        Object[] objects;

        //1 null或""转换为null
        objects = new Object[paramLength];
        objects[0] = null;
        objects[1] = null;
        objectsList.add(objects);

        //2 2014-12-12转换为2014-12-12 00:00:00
        objects = new Object[paramLength];
        objects[0] = "2014-12-12";
        objects[1] = DateUtils.buildDateTime(2014, 11, 12, 0, 0, 0);
        objectsList.add(objects);

        //3 12:12:12 转换为 1970-01-01 12:12:12
        objects = new Object[paramLength];
        objects[0] = "12:12:12";
        objects[1] = DateUtils.buildDateTime(1970, 0, 1, 12, 12, 12);
        objectsList.add(objects);

        //4 2014-12-12 12:12:12转换为相对应的时间
        objects = new Object[paramLength];
        objects[0] = "2014-12-12 12:12:12";
        objects[1] = DateUtils.buildDateTime(2014, 11, 12, 12, 12, 12);
        objectsList.add(objects);

        return objectsList.toArray(new Object[objectsList.size()][]);
    }

    /** 对字符串转时间的测试 */
    @Test(dataProvider = "p4testConvert")
    public void testConvert(String source, Date expectDate) throws Exception {
        Converter<String, Date> converter = new String2DateConverter();
        Date value = converter.convert(source);

        Assert.assertEquals(value, expectDate);
    }
}

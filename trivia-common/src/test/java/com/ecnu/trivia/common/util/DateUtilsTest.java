/** Created by Jack Chen at 13-3-4 */
package com.ecnu.trivia.common.util;


import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/** @author Jack Chen */
public class DateUtilsTest {
    private static final Logger logger = LoggerFactory.getLogger(DateUtilsTest.class);

    private static Date newDate(int year, int month, int dayOfMonth, int hour, int minute, int second, int milliSecond) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, dayOfMonth, hour, minute, second);
        calendar.set(Calendar.MILLISECOND, milliSecond);

        return calendar.getTime();
    }

    @DataProvider
    public Object[][] p4testBuildDate() {
        List<Object[]> objectsList = Lists.newArrayList();
        final int paramLength = 4;
        Object[] objects;

        //1 正常的数据 2014-02-28
        objects = new Object[paramLength];
        objects[0] = 2014;
        objects[1] = 1;//2月为1
        objects[2] = 28;
        objects[3] = newDate(2014, 1, 28, 0, 0, 0, 0);
        objectsList.add(objects);

        return objectsList.toArray(new Object[objectsList.size()][]);
    }

    @Test(dataProvider = "p4testBuildDate")
    public void testBuildDate(int year, int month, int dayOfMonth, Date expectDate) {
        Date date = DateUtils.buildDate(year, month, dayOfMonth);

        Assert.assertEquals(date, expectDate);
    }

    @DataProvider
    public Object[][] p4testBuildTime() {
        List<Object[]> objectsList = Lists.newArrayList();
        final int paramLength = 4;
        Object[] objects;

        //1 正常的数据 12:12:12
        objects = new Object[paramLength];
        objects[0] = 12;
        objects[1] = 12;
        objects[2] = 12;
        objects[3] = newDate(1970, 0, 1, 12, 12, 12, 0);
        objectsList.add(objects);

        return objectsList.toArray(new Object[objectsList.size()][]);
    }

    @Test(dataProvider = "p4testBuildTime")
    public void testBuildTime(int hour, int minute, int second, Date expectDate) {
        Date date = DateUtils.buildTime(hour, minute, second);

        Assert.assertEquals(date, expectDate);
    }

    @DataProvider
    public Object[][] p4testBuildDateTime() {
        List<Object[]> objectsList = Lists.newArrayList();
        final int paramLength = 7;
        Object[] objects;

        //1 正常的数据 2014-01-02 03:04:05
        objects = new Object[paramLength];
        objects[0] = 2014;
        objects[1] = 0;
        objects[2] = 2;
        objects[3] = 3;
        objects[4] = 4;
        objects[5] = 5;
        objects[6] = newDate(2014, 0, 2, 3, 4, 5, 0);

        return objectsList.toArray(new Object[objectsList.size()][]);
    }

    @Test(dataProvider = "p4testBuildDateTime")
    public void testBuildDateTime(int year, int month, int dayOfMonth, int hour, int minute, int second, Date expectDate) {
        Date date = DateUtils.buildDateTime(year, month, dayOfMonth, hour, minute, second);

        Assert.assertEquals(date, expectDate);
    }

    @DataProvider
    public Object[][] p4testGetWeekOfYear() {
        List<Object[]> objectsList = Lists.newArrayList();
        final int paramLength = 2;
        Object[] objects;

        //1 2015-01-01 第1周
        objects = new Object[paramLength];
        objects[0] = newDate(2015, 0, 1, 0, 0, 0, 1);
        objects[1] = 1;
        objectsList.add(objects);

        //2 2014-12-31 第53周
        objects = new Object[paramLength];
        objects[0] = newDate(2014, 11, 31, 0, 0, 0, 1);
        objects[1] = 53;
        objectsList.add(objects);

        //3 2014-12-27 第52周(星期天算最后一天)
        objects = new Object[paramLength];
        objects[0] = newDate(2014, 11, 27, 0, 0, 0, 1);
        objects[1] = 52;
        objectsList.add(objects);

        return objectsList.toArray(new Object[objectsList.size()][]);
    }

    /** 测试获取年周 */
    @Test(dataProvider = "p4testGetWeekOfYear")
    public void testGetWeekOfYear(Date date, int expectValue) {
        int week = DateUtils.getWeekOfYear(date);

        Assert.assertEquals(week, expectValue);
    }

    /**
     * 在一个时间的基础上增加多少时间测试
     */
    @Test
    public void testAddTime() {
        String dt = "20001010";
        Date date = DateUtils.parseDatePattern(dt, "yyyyMMdd");

        Date addDate = DateUtils.addTime(date, 1, Calendar.DAY_OF_MONTH);

        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DAY_OF_MONTH, 1);
        long timeInMillis = cal.getTimeInMillis();

        Assert.assertEquals(addDate.getTime(), timeInMillis);
    }

    /**
     * 将日期字符串按指定格式转换成日期类型。
     */
    @Test
    public void testParseDatePattern() {
        String source = "20121111";
        Date date = DateUtils.parseDatePattern(source, "yyyyMMdd");

        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        Date expect = null;
        try{
            expect = sdf.parse(source);
        } catch(ParseException e) {
            logger.error(e.getMessage(), e);
        }
        Assert.assertEquals(DateUtils.format(date, "yyyy-MM-dd"), DateUtils.format(expect, "yyyy-MM-dd"));
    }


    @DataProvider
    public Object[][] p4testWeekStart() {
        List<Object[]> objectsList = Lists.newArrayList();
        final int paramLength = 2;
        Object[] objects;

        //准备正常数据，获取第1天
        objects = new Object[paramLength];
        objects[0] = DateUtils.buildDate(2015, 6, 14);
        objects[1] = DateUtils.buildDate(2015, 6, 13);
        objectsList.add(objects);

        //准备星期天，应该获取为星期一
        objects = new Object[paramLength];
        objects[0] = DateUtils.buildDate(2015, 6, 19);
        objects[1] = DateUtils.buildDate(2015, 6, 13);
        objectsList.add(objects);

        return objectsList.toArray(new Object[objectsList.size()][]);
    }

    @DataProvider
    public Object[][] p4testWeekEnd() {
        List<Object[]> objectsList = Lists.newArrayList();
        final int paramLength = 2;
        Object[] objects;

        //准备正常数据，返回星期日
        objects = new Object[paramLength];
        objects[0] = DateUtils.buildDate(2015, 6, 14);
        objects[1] = DateUtils.buildDate(2015, 6, 19);
        objectsList.add(objects);

        //准备星期一，返回星期一
        objects = new Object[paramLength];
        objects[0] = DateUtils.buildDate(2015, 6, 13);
        objects[1] = DateUtils.buildDate(2015, 6, 19);
        objectsList.add(objects);

        return objectsList.toArray(new Object[objectsList.size()][]);
    }

    @DataProvider
    public Object[][] p4testMonthStart() {
        List<Object[]> objectsList = Lists.newArrayList();
        final int paramLength = 2;
        Object[] objects;

        //准备正常数据，返回当月第1天

        //2月
        objects = new Object[paramLength];
        objects[0] = DateUtils.buildDate(2015, 1, 28);
        objects[1] = DateUtils.buildDate(2015, 1, 1);
        objectsList.add(objects);

        //3月
        objects = new Object[paramLength];
        objects[0] = DateUtils.buildDate(2015, 6, 15);
        objects[1] = DateUtils.buildDate(2015, 6, 1);
        objectsList.add(objects);

        return objectsList.toArray(new Object[objectsList.size()][]);
    }

    /** 测试获取当月第一天 */
    @Test(dataProvider = "p4testMonthStart")
    public void testMonthStart(Date source, Date expectValue) throws Exception {
        Date value = DateUtils.monthStart(source);

        Assert.assertTrue(DateUtils.isSameDay(value, expectValue));
    }

    @DataProvider
    public Object[][] p4testMonthEnd() {
        List<Object[]> objectsList = Lists.newArrayList();
        final int paramLength = 2;
        Object[] objects;

        //2月，非闰年
        objects = new Object[paramLength];
        objects[0] = DateUtils.buildDate(2015, 1, 15);
        objects[1] = DateUtils.buildDate(2015, 1, 28);
        objectsList.add(objects);

        //2月，闰年
        objects = new Object[paramLength];
        objects[0] = DateUtils.buildDate(2012, 1, 1);
        objects[1] = DateUtils.buildDate(2012, 1, 29);
        objectsList.add(objects);

        //正常月 7月
        objects = new Object[paramLength];
        objects[0] = DateUtils.buildDate(2015, 6, 15);
        objects[1] = DateUtils.buildDate(2015, 6, 31);
        objectsList.add(objects);

        //正常月 8月
        objects = new Object[paramLength];
        objects[0] = DateUtils.buildDate(2015, 7, 1);
        objects[1] = DateUtils.buildDate(2015, 7, 31);
        objectsList.add(objects);

        return objectsList.toArray(new Object[objectsList.size()][]);
    }

    /** 测试获取当月最后一天 */
    @Test(dataProvider = "p4testMonthEnd")
    public void testMonthEnd(Date source, Date expectValue) throws Exception {
        Date value = DateUtils.monthEnd(source);

        Assert.assertTrue(DateUtils.isSameDay(value, expectValue));
    }

    @DataProvider
    public Object[][] p4testQuarterStart() {
        List<Object[]> objectsList = Lists.newArrayList();
        final int paramLength = 2;
        Object[] objects;

        //正常数据，返回季度第1天
        objects = new Object[paramLength];
        objects[0] = DateUtils.buildDate(2015, 6, 15);
        objects[1] = DateUtils.buildDate(2015, 6, 1);
        objectsList.add(objects);

        //当季度第1天，仍返回当天
        objects = new Object[paramLength];
        objects[0] = DateUtils.buildDate(2015, 6, 1);
        objects[1] = DateUtils.buildDate(2015, 6, 1);
        objectsList.add(objects);

        return objectsList.toArray(new Object[objectsList.size()][]);
    }

    /** 测试获取当季第一天 */
    @Test(dataProvider = "p4testQuarterStart")
    public void testQuarterStart(Date source, Date expectValue) throws Exception {
        Date value = DateUtils.quarterStart(source);

        Assert.assertTrue(DateUtils.isSameDay(value, expectValue), "数据不一致:" + value + "," + expectValue);
    }

    @DataProvider
    public Object[][] p4testQuarterEnd() {
        List<Object[]> objectsList = Lists.newArrayList();
        final int paramLength = 2;
        Object[] objects;

        //正常数据，返回季度最后一天
        objects = new Object[paramLength];
        objects[0] = DateUtils.buildDate(2015, 6, 15);
        objects[1] = DateUtils.buildDate(2015, 8, 30);
        objectsList.add(objects);

        //季度最后一天，返回当天
        objects = new Object[paramLength];
        objects[0] = DateUtils.buildDate(2015, 11, 31);
        objects[1] = DateUtils.buildDate(2015, 11, 31);
        objectsList.add(objects);

        return objectsList.toArray(new Object[objectsList.size()][]);
    }

    /** 测试获取当报最后一天 */
    @Test(dataProvider = "p4testQuarterEnd")
    public void testQuarterEnd(Date source, Date expectValue) throws Exception {
        Date value = DateUtils.quarterEnd(source);

        Assert.assertTrue(DateUtils.isSameDay(value, expectValue), "数据不一致:" + value + "," + expectValue);
    }

    @DataProvider
    public Object[][] p4testHalfYearStart() {
        List<Object[]> objectsList = Lists.newArrayList();
        final int paramLength = 2;
        Object[] objects;

        //正常数据，返回半年的第1天 下半年
        objects = new Object[paramLength];
        objects[0] = DateUtils.buildDate(2015, 6, 15);
        objects[1] = DateUtils.buildDate(2015, 6, 1);
        objectsList.add(objects);

        //上半年
        objects = new Object[paramLength];
        objects[0] = DateUtils.buildDate(2015, 5, 30);
        objects[1] = DateUtils.buildDate(2015, 0, 1);
        objectsList.add(objects);

        return objectsList.toArray(new Object[objectsList.size()][]);
    }

    /** 测试获取当半年第一天 */
    @Test(dataProvider = "p4testHalfYearStart")
    public void testHalfYearStart(Date source, Date expectValue) throws Exception {
        Date value = DateUtils.halfYearStart(source);

        Assert.assertTrue(DateUtils.isSameDay(value, expectValue), "数据不一致:" + value + "," + expectValue);
    }

    @DataProvider
    public Object[][] p4testHalfYearEnd() {
        List<Object[]> objectsList = Lists.newArrayList();
        final int paramLength = 2;
        Object[] objects;

        //1 上半年
        objects = new Object[paramLength];
        objects[0] = DateUtils.buildDate(2015, 3, 3);
        objects[1] = DateUtils.buildDate(2015, 5, 30);
        objectsList.add(objects);

        //2 下半年
        objects = new Object[paramLength];
        objects[0] = DateUtils.buildDate(2015, 7, 15);
        objects[1] = DateUtils.buildDate(2015, 11, 31);
        objectsList.add(objects);

        return objectsList.toArray(new Object[objectsList.size()][]);
    }

    /** 测试获取当半年最后一天 */
    @Test(dataProvider = "p4testHalfYearEnd")
    public void testHalfYearEnd(Date source, Date expectValue) throws Exception {
        Date value = DateUtils.halfYearEnd(source);

        Assert.assertTrue(DateUtils.isSameDay(value, expectValue));
    }

    @DataProvider
    public Object[][] p4testYearStart() {
        List<Object[]> objectsList = Lists.newArrayList();
        final int paramLength = 2;
        Object[] objects;

        //正常数据
        objects = new Object[paramLength];
        objects[0] = DateUtils.buildDate(2015, 6, 15);
        objects[1] = DateUtils.buildDate(2015, 0, 1);
        objectsList.add(objects);

        return objectsList.toArray(new Object[objectsList.size()][]);
    }

    /** 测试获取当年第一天 */
    @Test(dataProvider = "p4testYearStart")
    public void testYearStart(Date source, Date expectValue) throws Exception {
        Date value = DateUtils.yearStart(source);

        Assert.assertTrue(DateUtils.isSameDay(value, expectValue));
    }

    @DataProvider
    public Object[][] p4testYearEnd() {
        List<Object[]> objectsList = Lists.newArrayList();
        final int paramLength = 2;
        Object[] objects;

        //正常数据
        objects = new Object[paramLength];
        objects[0] = DateUtils.buildDate(2015, 5, 15);
        objects[1] = DateUtils.buildDate(2015, 11, 31);
        objectsList.add(objects);

        return objectsList.toArray(new Object[objectsList.size()][]);
    }

    /** 测试获取当年最后一天 */
    @Test(dataProvider = "p4testYearEnd")
    public void testYearEnd(Date source, Date expectValue) throws Exception {
        Date value = DateUtils.yearEnd(source);

        Assert.assertTrue(DateUtils.isSameDay(value, expectValue));
    }
}

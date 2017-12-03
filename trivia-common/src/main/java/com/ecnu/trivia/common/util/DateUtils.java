package com.ecnu.trivia.common.util;


import com.ecnu.trivia.common.exception.DateParseException;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;
import java.util.regex.Pattern;

/**
 * 时间工具类，用于封装各个时间对象信息
 *
 * @author Jack Chen
 */
public class DateUtils {

    private static final Logger logger = LoggerFactory.getLogger(DateUtils.class);

    private static final Pattern datePattern = Pattern.compile("(?:19|20)[0-9]{2}-(?:0?[1-9]|1[012])-(?:0?[1-9]|[12][0-9]|3[01])");
    private static final Pattern datePattern2 = Pattern.compile("(?:19|20)[0-9]{2}(?:0?[1-9]|1[012])(?:0?[1-9]|[12][0-9]|3[01])");
    private static final Pattern timePattern = Pattern.compile("(?:[01]?[0-9]|2[0-3]):[0-5]?[0-9]:[0-5]?[0-9]");
    private static final Pattern dateTimePattern = Pattern.compile(datePattern.pattern() + " " + timePattern.pattern());
    private static final ThreadLocal<DateFormat> dateFormatThreadLocal = new ThreadLocal<DateFormat>() {
        @Override
        protected DateFormat initialValue() {
            return new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
        }
    };

    private static final ThreadLocal<DateFormat> dateFormat2ThreadLocal = new ThreadLocal<DateFormat>() {
        @Override
        protected DateFormat initialValue() {
            return new SimpleDateFormat("yyyyMMdd", Locale.CHINA);
        }
    };

    private static final ThreadLocal<DateFormat> timeFormatThreadLocal = new ThreadLocal<DateFormat>() {
        @Override
        protected DateFormat initialValue() {
            return new SimpleDateFormat("HH:mm:ss");
        }
    };

    public static final ThreadLocal<DateFormat> dateTimeFormatThreadLocal = new ThreadLocal<DateFormat>() {
        @Override
        protected DateFormat initialValue() {
            return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        }
    };
    private static final ThreadLocal<DateFormat> dateTimeFormatForHM = new ThreadLocal<DateFormat>() {
        @Override
        protected DateFormat initialValue() {
            return new SimpleDateFormat("yyyy-MM-dd HH:mm");
        }
    };
    private static final ThreadLocal<Calendar> calendarThreadLocal = new ThreadLocal<Calendar>() {
        @Override
        protected Calendar initialValue() {
            Calendar calendar = Calendar.getInstance(Locale.CHINA);
            //以星期一为第一天
            calendar.setFirstDayOfWeek(Calendar.MONDAY);
            return calendar;
        }
    };

    /** 用于表示日期的常量对象 */
    public static class DateX implements Serializable {
        private final int year;
        private final int month;
        private final int dayOfMonth;

        public DateX(int year, int month, int dayOfMonth) {
            this.year = year;
            this.month = month;
            this.dayOfMonth = dayOfMonth;
        }

        public int getYear() {
            return year;
        }

        public int getMonth() {
            return month;
        }

        public int getDayOfMonth() {
            return dayOfMonth;
        }

        @Override
        public boolean equals(Object o) {
            if(this == o) {
                return true;
            }
            if(o == null || getClass() != o.getClass()) {
                return false;
            }

            DateX dateX = (DateX) o;

            if(dayOfMonth != dateX.dayOfMonth) {
                return false;
            }
            if(month != dateX.month) {
                return false;
            }
            if(year != dateX.year) {
                return false;
            }

            return true;
        }

        @Override
        public int hashCode() {
            int result = year;
            result = 31 * result + month;
            result = 31 * result + dayOfMonth;
            return result;
        }

        /** 转换为日期 */
        public Date toDate() {
            return buildDate(year, month, dayOfMonth);
        }
    }

    /** 用于表示日期的常量对象 */
    public static class TimeX implements Serializable {
        private final int hour;
        private final int minute;
        private final int second;

        public TimeX(int hour, int minute, int second) {
            this.hour = hour;
            this.minute = minute;
            this.second = second;
        }

        public int getHour() {
            return hour;
        }

        public int getMinute() {
            return minute;
        }

        public int getSecond() {
            return second;
        }

        @Override
        public boolean equals(Object o) {
            if(this == o) {
                return true;
            }
            if(o == null || getClass() != o.getClass()) {
                return false;
            }

            TimeX timeX = (TimeX) o;

            if(hour != timeX.hour) {
                return false;
            }
            if(minute != timeX.minute) {
                return false;
            }
            if(second != timeX.second) {
                return false;
            }

            return true;
        }

        @Override
        public int hashCode() {
            int result = hour;
            result = 31 * result + minute;
            result = 31 * result + second;
            return result;
        }
    }

    /** 用于表示日期时间的常量对象 */
    public static class DateTimeX implements Serializable {
        private final int year;
        private final int month;
        private final int dayOfMonth;
        private final int hour;
        private final int minute;
        private final int second;

        public DateTimeX(int year, int month, int dayOfMonth, int hour, int minute, int second) {
            this.year = year;
            this.month = month;
            this.dayOfMonth = dayOfMonth;
            this.hour = hour;
            this.minute = minute;
            this.second = second;
        }

        public int getYear() {
            return year;
        }

        public int getMonth() {
            return month;
        }

        public int getDayOfMonth() {
            return dayOfMonth;
        }

        public int getHour() {
            return hour;
        }

        public int getMinute() {
            return minute;
        }

        public int getSecond() {
            return second;
        }

        @Override
        public boolean equals(Object o) {
            if(this == o) {
                return true;
            }
            if(o == null || getClass() != o.getClass()) {
                return false;
            }

            DateTimeX dateTimeX = (DateTimeX) o;

            if(dayOfMonth != dateTimeX.dayOfMonth) {
                return false;
            }
            if(hour != dateTimeX.hour) {
                return false;
            }
            if(minute != dateTimeX.minute) {
                return false;
            }
            if(month != dateTimeX.month) {
                return false;
            }
            if(second != dateTimeX.second) {
                return false;
            }
            if(year != dateTimeX.year) {
                return false;
            }

            return true;
        }

        @Override
        public int hashCode() {
            int result = year;
            result = 31 * result + month;
            result = 31 * result + dayOfMonth;
            result = 31 * result + hour;
            result = 31 * result + minute;
            result = 31 * result + second;
            return result;
        }
    }

    /** 转换日期至年月日时分 */
    public static String formatDateHM(Date date) {
        return dateTimeFormatForHM.get().format(date);
    }

    /** 时间转年月日 */
    public static String formatDate(Date date) {
        return dateFormatThreadLocal.get().format(date);
    }

    /** 时间转时分秒 */
    public static String formatTime(Date date) {
        return timeFormatThreadLocal.get().format(date);
    }

    /** 时间转年月日 时分秒 */
    public static String formatDateTime(Date date) {
        if(Objects.isNull(date)) {
            return null;
        }
        return dateTimeFormatThreadLocal.get().format(date);
    }

    /** 时间转指定格式的字符串 */
    public static String format(Date date, String pattern) {
        return DateFormatUtils.format(date, pattern, Locale.CHINA);
    }

    public static Date parseDateByStr(String dateStr) {
        if(Objects.isNull(dateStr)) {
            return null;
        }

        Date d = null;
        try{
            if(datePattern.matcher(dateStr).matches()) {
                d = dateFormatThreadLocal.get().parse(dateStr);
            } else if(datePattern2.matcher(dateStr).matches()) {
                d = dateFormat2ThreadLocal.get().parse(dateStr);
            } else if(dateTimePattern.matcher(dateStr).matches()) {
                d = dateTimeFormatThreadLocal.get().parse(dateStr);
            }
        } catch(ParseException e) {
            d = null;
        }
        if(d == null) {
            //类型转换失败,返回null
            logger.error(new DateParseException("String:" + dateStr + " 不能进行转换为Date").getMessage());
        }
        return d;
    }

    /** 年月日转时间,转换失败时抛出转换异常 */
    public static Date parseDate(String dateStr) throws DateParseException {
        Date d = parseDate(dateStr, null);
        if(d == null) {
            throw new DateParseException(dateStr + " 不能进行转换为Date");
        }
        return d;
    }

    /** 校验字符串能否被转换成Date */
    public static boolean validateDateStr(String dateStr) throws DateParseException {
        if(Objects.isNull(dateStr)) {
            return true;
        }
        if(datePattern.matcher(dateStr).matches()) {
            return true;
        } else if(datePattern2.matcher(dateStr).matches()) {
            return true;
        }
        return false;
    }

    /** 校验字符串能够被转换成dateTime */
    public static boolean validateDateTimeStr(String dateStr) throws DateParseException {
        if(Objects.isNull(dateStr)) {
            return true;
        }
        return dateTimePattern.matcher(dateStr).matches();
    }

    /** 校验long是否能被转换到Date */
    public static boolean validateDateLong(Long dateLong) throws DateParseException {
        if(Objects.isNull(dateLong)) {
            return true;
        }
        return datePattern2.matcher(dateLong.toString()).matches();
    }

    /** 校验DateTime是否能被转换到Date,不能有时分秒 */
    public static boolean validateDateDateTime(Date dateTime) throws DateParseException {
        if(Objects.isNull(dateTime)) {
            return true;
        }
        String dateTimeStr = DateUtils.formatDateTime(dateTime);
        return dateTimeStr.endsWith("00:00:00");
    }

    /** 年月日转时间,转换失败时返回默认时间 */
    public static Date parseDate(String dateStr, Date defaultDate) {
        if(!datePattern.matcher(dateStr).matches()) {
            return defaultDate;
        }
        try{
            return dateFormatThreadLocal.get().parse(dateStr);
        } catch(ParseException e) {
            return defaultDate;
        }
    }

    /** 时分秒转时间,转换失败时抛出转换异常 */
    public static Date parseTime(String dateStr) throws DateParseException {
        Date d = parseTime(dateStr, null);
        if(d == null) {
            throw new DateParseException(dateStr + " 不能转换为Time");
        }
        return d;
    }

    /** 时分秒转时间,转换失败时返回默认时间 */
    public static Date parseTime(String dateStr, Date defaultDate) {
        if(!timePattern.matcher(dateStr).matches()) {
            return defaultDate;
        }
        try{
            return timeFormatThreadLocal.get().parse(dateStr);
        } catch(ParseException e) {
            return defaultDate;
        }
    }

    /** 年月日 时分秒转时间,转换失败时抛出转换异常 */
    public static Date parseDateTime(String dateStr) throws DateParseException {
        Date d = parseDateTime(dateStr, null);
        if(d == null) {
            throw new DateParseException(dateStr + " 不能转换为DateTime");
        }
        return d;
    }

    /** 年月日 时分秒转时间,转换失败时返回默认值 */
    public static Date parseDateTime(String dateStr, Date defaultDate) {
        if(!dateTimePattern.matcher(dateStr).matches()) {
            return defaultDate;
        }
        try{
            return dateTimeFormatThreadLocal.get().parse(dateStr);
        } catch(ParseException e) {
            return defaultDate;
        }
    }

    private static Calendar getCalendar(Date date) {
        Calendar calendar = calendarThreadLocal.get();
        calendar.setTime(date);
        return calendar;
    }

    /** 时间对换 开始 结束 */
    public static void swap(Date startDate, Date endDate) {
        if(startDate == null || endDate == null) {
            return;
        }
        if(startDate.after(endDate)) {
            long startDateLong = startDate.getTime();
            startDate.setTime(endDate.getTime());
            endDate.setTime(startDateLong);
        }
    }

    /** 获取指定时间的年份 */
    public static int getFullYear(Date date) {
        Calendar calendar = getCalendar(date);
        return calendar.get(Calendar.YEAR);
    }

    /** 获取指定时间的月份 */
    public static int getMonth(Date date) {
        Calendar calendar = getCalendar(date);
        return calendar.get(Calendar.MONTH);
    }

    /** 获取指定时间的天(按月计) */
    public static int getDayOfMonth(Date date) {
        Calendar calendar = getCalendar(date);
        return calendar.get(Calendar.DAY_OF_MONTH);
    }

    /** 获取指定时间的星期几(按星期计) */
    public static int getDayOfWeek(Date date) {
        Calendar calendar = getCalendar(date);
        return calendar.get(Calendar.DAY_OF_WEEK);
    }

    /** 获取小时 */
    public static int getHour(Date date) {
        Calendar calendar = getCalendar(date);
        return calendar.get(Calendar.HOUR_OF_DAY);
    }

    /** 获取分钟 */
    public static int getMinute(Date date) {
        Calendar calendar = getCalendar(date);
        return calendar.get(Calendar.MINUTE);
    }

    /** 获取秒 */
    public static int getSecond(Date date) {
        Calendar calendar = getCalendar(date);
        return calendar.get(Calendar.SECOND);
    }

    /** 获取年周 */
    public static int getWeekOfYear(Date date) {
        Calendar calendar = getCalendar(date);
        int week = calendar.get(Calendar.WEEK_OF_YEAR);

        if(week == 1) {
            //如果为第1周，则尝试计算星期一，星期日所在年份，如果星期一和星期日年份相同，则表示都在新年。
            //如果与星期一相同，则表示是旧年，需要取星期一往前一天所在周+1
            int year = calendar.get(Calendar.YEAR);

            Calendar mondayCalendar = (Calendar) calendar.clone();
            mondayCalendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
            DateUtils.DateX monday = DateUtils.getDateX(mondayCalendar.getTime());

            Calendar sundayCalendar = (Calendar) calendar.clone();
            sundayCalendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
            DateUtils.DateX sunday = DateUtils.getDateX(sundayCalendar.getTime());

            if(monday.year == sunday.year) {
                //nothing to do
            } else if(year == monday.year) {
                Calendar temp = getCalendar(DateUtils.buildDate(monday.getYear(), monday.getMonth(), monday.getDayOfMonth() - 1));
                week = temp.get(Calendar.WEEK_OF_YEAR) + 1;
            }
        }
        return week;
    }

    /** 设置指定的秒 */
    public static Date setSecond(Date date, int second) {
        Calendar calendar = getCalendar(date);
        calendar.set(Calendar.SECOND, second);
        return calendar.getTime();
    }

    /** 设置分 */
    public static Date setMinute(Date date, int minute) {
        Calendar calendar = getCalendar(date);
        calendar.set(Calendar.MINUTE, minute);
        return calendar.getTime();
    }

    /** 设置小时 */
    public static Date setHour(Date date, int hour) {
        Calendar calendar = getCalendar(date);
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        return calendar.getTime();
    }

    /** 设置天(按月计) */
    public static Date setDayOfMonth(Date date, int dayOfMonth) {
        Calendar calendar = getCalendar(date);
        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        return calendar.getTime();
    }

    /** 设置天(按周计) */
    public static Date setDayOfWeek(Date date, int dayOfWeek) {
        Calendar calendar = getCalendar(date);
        calendar.set(Calendar.DAY_OF_WEEK, dayOfWeek);
        return calendar.getTime();
    }

    /** 设置月 */
    public static Date setMonth(Date date, int month) {
        Calendar calendar = getCalendar(date);
        calendar.set(Calendar.MONTH, month);
        return calendar.getTime();
    }

    /** 设置年 */
    public static Date setFullYear(Date date, int fullYear) {
        Calendar calendar = getCalendar(date);
        calendar.set(Calendar.YEAR, fullYear);
        return calendar.getTime();
    }

    /** 设置周 */
    public static Date setWeekOfYear(Date date, int weekOfYear) {
        Calendar calendar = getCalendar(date);
        calendar.set(Calendar.WEEK_OF_YEAR, weekOfYear);
        return calendar.getTime();
    }

    /** 获取日期 */
    public static DateX getDateX(Date date) {
        Calendar calendar = getCalendar(date);
        return new DateX(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
    }

    /** 获取时间 */
    public static TimeX getTimeX(Date date) {
        Calendar calendar = getCalendar(date);
        return new TimeX(calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), calendar.get(Calendar.SECOND));
    }

    /** 获取日期时间 */
    public static DateTimeX getDateTimeX(Date date) {
        Calendar calendar = getCalendar(date);
        return new DateTimeX(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE), calendar.get(Calendar.SECOND));
    }

    /** 判断两个时间是否是同一天 */
    public static boolean isSameDay(Date aDate, Date bDate) {
        DateX aDateX = getDateX(aDate);
        DateX bDateX = getDateX(bDate);

        return aDateX.equals(bDateX);
    }

    /** 判断指定的时间是否为今天 */
    public static boolean isToday(Date date) {
        return isSameDay(date, new Date());
    }

    /** 根据年,月,日创建时间 */
    public static Date buildDate(int year, int month, int dayOfMonth) {
        return buildDateTime(year, month, dayOfMonth, 0, 0, 0);
    }

    /** 根据小时,分,秒创建时间 */
    public static Date buildTime(int hour, int minute, int second) {
        return buildDateTime(1900, 0, 1, hour, minute, second);
    }

    /** 根据年,月,日,小时,分,秒创建时间 */
    public static Date buildDateTime(int year, int month, int dayOfMonth, int hour, int minute, int second) {
        Calendar calendar = getCalendar(new Date());
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, second);

        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

    /**
     * 在一个时间的基础上增加多少时间
     *
     * @param date   当前时间
     * @param amount 增加时间数
     * @param field  增加的时间域
     * @return 时间
     */
    public static Date addTime(Date date, int amount, int field) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(field, amount);
        return calendar.getTime();
    }

    /**
     * 将日期字段串按指定格式转换为日期类型。
     *
     * @param source  要转换的日期字符串。
     * @param pattern 日期格式。
     * @return 转换后的日期
     */
    public static Date parseDatePattern(String source, String pattern) {
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        Date date;
        try{
            date = sdf.parse(source);
        } catch(ParseException e) {
            throw new DateParseException(source + " 不能进行转换为Date");
        }
        return date;
    }

    public static Date truncatToday() {
        return org.apache.commons.lang3.time.DateUtils.truncate(new Date(), Calendar.DAY_OF_MONTH);
    }

    public static Date truncatYesterday() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, -1);

        return org.apache.commons.lang3.time.DateUtils.truncate(calendar.getTime(), Calendar.DAY_OF_MONTH);
    }

    /**
     * 获取传入时间的当天开始时间：
     * 示例：
     * 输入：2015-11-11 11:11:11
     * 输出：2015-11-11 00:00:00
     *
     * @param date 要转换的日期
     * @return 当天的开始日期。
     */
    public static Date dayStart(Date date) {
        Calendar calendar = getCalendar(date);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

    /**
     * 获取传入时间的当天最后时间点。
     * 示例：
     * 输入：2015-11-11 11:11:11
     * 输出：2015-11-11 23:59:59
     *
     * @param date 要转换的日期
     * @return 当天最后时间
     */
    public static Date dayEnd(Date date) {
        Calendar calendar = getCalendar(date);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

    /**
     * 获取传入时间的昨天开始时间点。
     * 示例：
     * 输入：2015-11-11 11:11:11
     * 输出：2015-11-10 00:00:00
     *
     * @param date 要转换的日期
     * @return 当天最后时间
     */
    public static Date yesterdayStart(Date date){
        Calendar calendar = getCalendar(date);
        calendar.add(Calendar.DATE,-1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }


    /**
     * 获取传入时间的昨天最后时间点。
     * 示例：
     * 输入：2015-11-11 11:11:11
     * 输出：2015-11-10 23:59:59
     *
     * @param date 要转换的日期
     * @return 当天最后时间
     */
    public static Date yesterdayEnd(Date date){
        Calendar calendar = getCalendar(date);
        calendar.add(Calendar.DATE,-1);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

    /** 转换为星期一 */
    public static Date weekStart(Date date) {
        Calendar calendar = getCalendar(date);
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        return calendar.getTime();
    }

    /** 转换为星期天 */
    public static Date weekEnd(Date date) {
        Calendar calendar = getCalendar(date);
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
        return calendar.getTime();
    }

    /** 转换为每月的1号 */
    public static Date monthStart(Date date) {
        Calendar calendar = getCalendar(date);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        return calendar.getTime();
    }

    /** 转换为当月的最后一天 */
    public static Date monthEnd(Date date) {
        Calendar calendar = getCalendar(date);
        int lastDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        calendar.set(Calendar.DAY_OF_MONTH, lastDay);
        return calendar.getTime();
    }

    /** 转换为当季度的第一天 */
    public static Date quarterStart(Date date) {
        Calendar calendar = getCalendar(date);
        int month = calendar.get(Calendar.MONTH);
        int firstMonth = month / 3 * 3 + 1;
        calendar.set(Calendar.MONTH, firstMonth - 1);
        calendar.set(Calendar.DAY_OF_MONTH, 1);

        return calendar.getTime();
    }

    /** 转换为当季度的最后一天 */
    public static Date quarterEnd(Date date) {
        Calendar calendar = getCalendar(date);
        int month = calendar.get(Calendar.MONTH);
        int lastMonth = (month / 3 + 1) * 3;
        calendar.set(Calendar.MONTH, lastMonth - 1);
        // 2 11 即3 12 月 31天 5 8 即 6 9月 30天
        calendar.set(Calendar.DAY_OF_MONTH, lastMonth == 3 || lastMonth == 12 ? 31 : 30);

        return calendar.getTime();
    }

    /** 转换为当半年的第一天 */
    public static Date halfYearStart(Date date) {
        Calendar calendar = getCalendar(date);
        int month = calendar.get(Calendar.MONTH);
        int half = month / 6;
        int newMonth = half == 0 ? 0 : 6;

        calendar.set(Calendar.MONTH, newMonth);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        return calendar.getTime();
    }

    /** 转换为当半年的最后一天 */
    public static Date halfYearEnd(Date date) {
        Calendar calendar = getCalendar(date);
        int month = calendar.get(Calendar.MONTH);
        int half = month / 6;
        int newMonth = half == 0 ? 5 : 11;
        int day = half == 0 ? 30 : 31;

        calendar.set(Calendar.MONTH, newMonth);
        calendar.set(Calendar.DAY_OF_MONTH, day);
        return calendar.getTime();
    }

    /** 转换为当年的第一天 */
    public static Date yearStart(Date date) {
        Calendar calendar = getCalendar(date);
        calendar.set(Calendar.MONTH, 0);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        return calendar.getTime();
    }

    /** 转换为当年的最后一天 */
    public static Date yearEnd(Date date) {
        Calendar calendar = getCalendar(date);
        calendar.set(Calendar.MONTH, 11);
        calendar.set(Calendar.DAY_OF_MONTH, 31);
        return calendar.getTime();
    }

    /**
     * 获取两个日期相差的天数绝对值
     * <p/>
     * 示例1，includeSelf = false：
     * 输入：date1:20160906 date2:20160903
     * 输出：2
     * <p/>
     * 示例2，includeSelf = true：
     * 输入：date1:20160906 date2:20160903
     * 输出：3
     *
     * @param date1       日期1
     * @param date2       日期2
     * @param includeSelf 是否包含当天
     * @return 两日期相差天数
     */
    public static String between(Date date1, Date date2, boolean includeSelf) {
        long between = (date1.getTime() - date2.getTime()) / (1000 * 3600 * 24);
        int i = Integer.parseInt(String.valueOf(between));
        //如果包含当天，则加1
        if(includeSelf) {
            i += 1;
        }

        return Integer.toString(Math.abs(i));
    }

    /** 获取两个日期相差的天数绝对值,包含当天 */
    public static String between(Date date1, Date date2) {
        return between(date1, date2, true);
    }
}

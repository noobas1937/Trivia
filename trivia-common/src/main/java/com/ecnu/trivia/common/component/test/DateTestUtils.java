/** Created by Jack Chen at 5/7/2015 */
package com.ecnu.trivia.common.component.test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

/**
 * 用于描述基于时间的测试工具类
 *
 * @author Jack Chen
 */
public class DateTestUtils {
    private static final Logger logger = LoggerFactory.getLogger(DateTestUtils.class);

    /** 判定是否为当前时间的有效间隔，当前2分钟钟均为相等 */
    private static final long currentMaxGap = 2 * 60 * 1000;
    /** 两个时间相等性的判定，相当与当前时间有效间隔一致 */
    private static final long validDateGap = currentMaxGap;

    /**
     * 判定是否是当前时间
     * 在测试场景中，判定一个时间是否是当前时间，仅是指接近当前。因此这里采用2分钟间隔，如果时间间隔在指定时间之内，则表示是当前时间
     */
    public static boolean isCurrentTime(Date date) {
        return Math.abs(System.currentTimeMillis() - date.getTime()) < currentMaxGap;
    }

    /**
     * 判断是否是同一时间
     * 在测试场景中，因为运行时间的关系,两个时间在计算时可能受到运行时间的影响，因此并不能严格一致性相等
     */
    public static boolean isSameTime(Date value, Date expectDate) {
        boolean same = Math.abs(value.getTime() - expectDate.getTime()) < validDateGap;

        if(!same) {
            logger.warn("时间值:{}与预期值:{}不相同", value, expectDate);
        }

        return same;
    }
}

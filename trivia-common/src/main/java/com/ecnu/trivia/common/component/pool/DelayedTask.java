/** Created by Jack Chen at 10/14/2014 */
package com.ecnu.trivia.common.component.pool;

import java.util.concurrent.TimeUnit;

/**
 * 描述在指定延迟之后并具有间隔延迟时间进行的任务
 *
 * @author Jack Chen
 */
public interface DelayedTask {
    /** 初始化延迟多少时间 */
    long getInitDelay();

    /** 延迟多少时间 */
    long getIntervalDelay();

    /** 时间单位 */
    TimeUnit getTimeUnit();
}

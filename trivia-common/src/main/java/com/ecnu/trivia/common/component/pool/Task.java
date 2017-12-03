/** Created by Jack Chen at 10/14/2014 */
package com.ecnu.trivia.common.component.pool;

/**
 * 描述待执行的任务信息
 *
 * @author Jack Chen
 */
public interface Task<T> extends Runnable, Comparable<Task> {
    /** 描述任务的惟一键 */
    String uniqueKey();

    void setTaskPool(ExecutorTaskPool taskPool);

    /** 此任务完成之后的结果 */
    T getResult();

    /** 是否满足运行的条件 */
    boolean conditionMeet();

    /** 任务完成时需要自己作什么操作 */
    void finish();
}

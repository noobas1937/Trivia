/** Created by Jack Chen at 12/10/2014 */
package com.ecnu.trivia.web.common.service;

import com.ecnu.trivia.common.ApplicationContextHolder;
import com.ecnu.trivia.common.component.pool.Task;
import com.ecnu.trivia.common.component.pool.service.AbstractAsyncTaskExecutor;

/**
 * web系统异步任务执行器
 *
 * @author Jack Chen
 */
public class WebAsyncTaskExecutor extends AbstractAsyncTaskExecutor {

    /** 执行任务 */
    public static void execute(Task task) {
        WebAsyncTaskExecutor taskExecutor = ApplicationContextHolder.getInstance(WebAsyncTaskExecutor.class);
        taskExecutor.executorTaskPool.addTask(task);
    }
}

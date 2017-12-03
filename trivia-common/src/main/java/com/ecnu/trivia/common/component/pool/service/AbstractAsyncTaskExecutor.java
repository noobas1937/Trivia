/** Created by Jack Chen at 12/10/2014 */
package com.ecnu.trivia.common.component.pool.service;

import com.ecnu.trivia.common.component.pool.ExecutorTaskPool;

/**
 * 为上层提供异步执行器框架
 *
 * @author Jack Chen
 */
public abstract class AbstractAsyncTaskExecutor {
    private int maxThread;
    protected ExecutorTaskPool executorTaskPool;

    public void setMaxThread(int maxThread) {
        this.maxThread = maxThread;
    }

    public void init() {
        executorTaskPool = new ExecutorTaskPool(maxThread);
        executorTaskPool.start();
    }

    public void close() {
        if(executorTaskPool != null) {
            executorTaskPool.close();
        }
    }
}

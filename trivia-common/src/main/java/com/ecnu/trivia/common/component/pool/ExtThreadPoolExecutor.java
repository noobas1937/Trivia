/** Created by Jack Chen at 10/15/2014 */
package com.ecnu.trivia.common.component.pool;

import java.util.concurrent.*;

/** @author Jack Chen */
class ExtThreadPoolExecutor extends ThreadPoolExecutor {
    public ExtThreadPoolExecutor(int maxThread, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue) {
        super(maxThread, maxThread, keepAliveTime, unit, workQueue, new NormalThreadFactory());
    }

    @Override
    protected <T> RunnableFuture<T> newTaskFor(Runnable runnable, T value) {
        if(!(runnable instanceof Task)) {
            throw new IllegalArgumentException("任务类型必须为Task类型");
        }
        return new ExtFutureTask<>((Task) runnable, 0, TimeUnit.MILLISECONDS);
    }

    @Override
    protected <T> RunnableFuture<T> newTaskFor(Callable<T> callable) {
        throw new RuntimeException("不支持此方法");
    }
}

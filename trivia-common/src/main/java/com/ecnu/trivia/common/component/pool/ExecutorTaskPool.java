/** Created by Jack Chen at 10/14/2014 */
package com.ecnu.trivia.common.component.pool;

import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 任务执行池，负责执行任务
 *
 * @author Jack Chen
 */
public class ExecutorTaskPool {
    private static final long timeout = 10;//线程过期时间 10分钟
    ExtBlockingQueue queue = new ExtBlockingQueue();
    private int maxThread;
    private ThreadPoolExecutor threadPoolExecutor;
    private boolean isClosing;

    public ExecutorTaskPool(int maxThread) {
        this.maxThread = maxThread;
        init();
    }

    @SuppressWarnings("unchecked")
    private void init() {
        this.threadPoolExecutor = new ExtThreadPoolExecutor(maxThread, timeout, TimeUnit.MINUTES, queue);
        threadPoolExecutor.allowCoreThreadTimeOut(true);//允许核心线程超时
    }

    /** 添加待执行的任务 */
    public void addTask(Task task) {
        if(isClosing) {
            return;
        }

        task.setTaskPool(this);

        if(task instanceof DelayedTask) {
            DelayedTask selfTask = (DelayedTask) task;
            addTask(task, selfTask.getInitDelay(), selfTask.getTimeUnit());
        } else {
            addTask(task, 0, TimeUnit.MILLISECONDS);
        }
    }

    /** 添加待执行的任务，到指定的延迟之后执行 */
    public void addTask(Task task, long time, TimeUnit timeUnit) {
        if(isClosing) {
            return;
        }

        ExtFutureTask futureTask = new ExtFutureTask(task, time, timeUnit);
        //判断是否已在执行队列表
        if(queue.size()>0 && queue.contains(futureTask)) {
            return;
        }

        _addInternalTask(futureTask);
    }

    private void _addInternalTask(ExtFutureTask futureTask) {
        queue.put(futureTask);
        threadPoolExecutor.prestartCoreThread();
    }

    public void start() {
        //nothing to do
    }

    public void close() {
        isClosing = true;
        threadPoolExecutor.shutdown();
    }
}

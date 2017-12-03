/** Created by Jack Chen at 10/14/2014 */
package com.ecnu.trivia.common.component.pool;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;

/** @author Jack Chen */
public abstract class AbstractTask<T> implements Task<T> {
    private static final Logger logger = LoggerFactory.getLogger(AbstractTask.class);
    protected ExecutorTaskPool executorTaskPool;
    protected T result;

    @Override
    public void setTaskPool(ExecutorTaskPool taskPool) {
        this.executorTaskPool = taskPool;
    }

    protected void nextRun() {
        if(this instanceof DelayedTask) {
            DelayedTask self = (DelayedTask) this;
            executorTaskPool.addTask(this, self.getIntervalDelay(), self.getTimeUnit());
        } else {
            executorTaskPool.addTask(this);
        }
    }

    public abstract T doRun() throws Exception;

    protected void innerRun() throws Exception {
        if(!conditionMeet()) {
            nextRun();
            return;
        }

        long start = System.currentTimeMillis();
        result = doRun();
        long end = System.currentTimeMillis();
        logThisRun(end - start);

        finish();
    }

    protected void logThisRun(long cost) {
        logger.debug("任务:{},当次执行花费:{}", uniqueKey(), cost);
    }

    @Override
    public final void run() {
        try{
            innerRun();
        } catch(Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    @Override
    public T getResult() {
        return result;
    }

    @Override
    public void finish() {
    }

    @Override
    public boolean conditionMeet() {
        return true;
    }

    @Override
    public boolean equals(Object o) {
        if(this == o) {
            return true;
        }
        if(o == null || getClass() != o.getClass()) {
            return false;
        }

        AbstractTask<?> that = (AbstractTask<?>) o;
        if(uniqueKey()!=null) {
            return uniqueKey().equals(that.uniqueKey());
        }
        logger.error("The current AbstractTask is null!");
        return false;
    }

    @Override
    public int hashCode() {
        return uniqueKey().hashCode();
    }

    @Override
    public int compareTo(@Nonnull Task o) {
        return uniqueKey().compareTo(o.uniqueKey());
    }
}

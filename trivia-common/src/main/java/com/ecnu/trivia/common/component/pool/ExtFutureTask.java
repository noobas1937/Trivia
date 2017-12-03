/** Created by Jack Chen at 10/15/2014 */
package com.ecnu.trivia.common.component.pool;

import com.google.common.primitives.Longs;

import javax.annotation.Nonnull;
import java.util.concurrent.Delayed;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

/**
 * 使用扩展的futureTask以保证多个任务之间可以进行比较
 *
 * @author Jack Chen
 */
class ExtFutureTask<V> extends FutureTask<V> implements Delayed {
    private final Task task;
    private long shouldRunTime;//毫秒，应该运行的时间点

    private static long now() {
        return System.currentTimeMillis();
    }

    public ExtFutureTask(Task task, long delay, TimeUnit timeUnit) {
        super(task, null);
        this.task = task;
        shouldRunTime = now() + timeUnit.toMillis(delay);
    }

    @Override
    public boolean equals(Object o) {
        if(this == o) {
            return true;
        }
        if(o == null || getClass() != o.getClass()) {
            return false;
        }

        ExtFutureTask that = (ExtFutureTask) o;

        return task.equals(that.task);
    }

    @Override
    public int hashCode() {
        return task.hashCode();
    }

    @Override
    public long getDelay(TimeUnit unit) {
        return unit.convert(shouldRunTime - now(), TimeUnit.MILLISECONDS);
    }

    @SuppressWarnings("unchecked")
    @Override
    public int compareTo(@Nonnull Delayed o) {
        ExtFutureTask that = (ExtFutureTask) o;
        return Longs.compare(shouldRunTime, that.shouldRunTime);
    }
}

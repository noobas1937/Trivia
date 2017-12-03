/** Created by Jack Chen at 10/14/2014 */
package com.ecnu.trivia.common.component.pool;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

/** @author Jack Chen */
public class NormalThreadFactory implements ThreadFactory {
    private static final Logger logger = LoggerFactory.getLogger(NormalThreadFactory.class);
    private ThreadFactory threadFactory = Executors.defaultThreadFactory();

    @Override
    public Thread newThread(@Nonnull Runnable r) {
        Thread thread = threadFactory.newThread(r);
        thread.setDaemon(false);
        thread.setUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread t, Throwable e) {
                logger.error("Thread UncaughtException:",e.getMessage(), e);
            }
        });
        return thread;
    }
}

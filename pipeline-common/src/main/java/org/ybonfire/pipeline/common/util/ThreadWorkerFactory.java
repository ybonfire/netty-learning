package org.ybonfire.pipeline.common.util;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 线程worker工厂
 *
 * @author yuanbo
 * @date 2022/09/08 17:39:19
 */
public class ThreadWorkerFactory implements ThreadFactory {
    private final AtomicInteger counter = new AtomicInteger(0);
    private final String threadNamePrefix;
    private final boolean isDaemon;

    public ThreadWorkerFactory(final String threadNamePrefix, final boolean isDaemon) {
        this.threadNamePrefix = threadNamePrefix;
        this.isDaemon = isDaemon;
    }

    @Override
    public Thread newThread(final Runnable r) {
        final Thread thread =
            new Thread(r, new StringBuffer(threadNamePrefix).append("_").append(counter.incrementAndGet()).toString());
        thread.setDaemon(isDaemon);

        return thread;
    }
}

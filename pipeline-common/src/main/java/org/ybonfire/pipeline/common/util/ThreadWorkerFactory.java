package org.ybonfire.pipeline.common.util;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 线程工厂
 *
 * @author Bo.Yuan5
 * @date 2022-07-04 10:03
 */
public class ThreadWorkerFactory implements ThreadFactory {
    private final AtomicInteger counter = new AtomicInteger(0);
    private final String threadNamePrefix;
    private final boolean isDaemon;

    public ThreadWorkerFactory(final String threadNamePrefix, final boolean isDaemon) {
        this.threadNamePrefix = threadNamePrefix;
        this.isDaemon = isDaemon;
    }

    /**
     * @description: 构造线程
     * @param:
     * @return:
     * @date: 2022/07/04 10:07:41
     */
    @Override
    public Thread newThread(final Runnable r) {
        final Thread thread =
            new Thread(r, new StringBuffer(threadNamePrefix).append("_").append(counter.incrementAndGet()).toString());
        thread.setDaemon(isDaemon);

        return thread;
    }
}

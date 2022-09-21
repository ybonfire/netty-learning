package org.ybonfire.pipeline.producer.util;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.ybonfire.pipeline.common.util.ThreadWorkerFactory;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * 线程池工具类
 *
 * @author Bo.Yuan5
 * @date 2022-05-23 18:01
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ThreadPoolUtil {
    // Message Produce Thread Pool
    private static final int MESSAGE_PRODUCE_THREAD_NUMS_MIN = Math.max(8, Runtime.getRuntime().availableProcessors());
    private static final int MESSAGE_PRODUCE_THREAD_NUMS_MAX = Math.max(8, Runtime.getRuntime().availableProcessors());
    private static final int MESSAGE_PRODUCE_TASK_QUEUE_CAPACITY = Integer.MAX_VALUE;
    private static final ThreadFactory MESSAGE_PRODUCE_THREAD_FACTORY = new ThreadWorkerFactory("produce_", true);
    private static final ExecutorService MESSAGE_PRODUCE_EXECUTOR_SERVICE =
        buildThreadPool(MESSAGE_PRODUCE_THREAD_NUMS_MIN, MESSAGE_PRODUCE_THREAD_NUMS_MAX,
            MESSAGE_PRODUCE_TASK_QUEUE_CAPACITY, MESSAGE_PRODUCE_THREAD_FACTORY);

    public static ExecutorService getMessageProduceExecutorService() {
        return MESSAGE_PRODUCE_EXECUTOR_SERVICE;
    }

    /**
     * @description: 构造非固定大小线程池
     * @param:
     * @return:
     * @date: 2022/5/23 18:16:23
     */
    public static ExecutorService buildThreadPool(final int minThreadNums, final int maxThreadNums,
        final int queueCapacity, final ThreadFactory factory) {
        return new ThreadPoolExecutor(minThreadNums, maxThreadNums, 60 * 1000L, TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<>(queueCapacity), factory);
    }
}

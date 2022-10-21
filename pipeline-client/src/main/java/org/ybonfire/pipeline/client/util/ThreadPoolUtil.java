package org.ybonfire.pipeline.client.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.ybonfire.pipeline.common.util.ThreadWorkerFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 线程池工具类
 *
 * @author yuanbo
 * @date 2022-10-20 22:42
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ThreadPoolUtil {
    // response Processor Thread Pool
    private static final int RESPONSE_PROCESSOR_THREAD_NUMS_MIN =
        Math.max(2, Runtime.getRuntime().availableProcessors());
    private static final int RESPONSE_PROCESSOR_THREAD_NUMS_MAX =
        Math.max(2, Runtime.getRuntime().availableProcessors());
    private static final int RESPONSE_TASK_QUEUE_CAPACITY = Integer.MAX_VALUE;
    private static final ThreadFactory RESPONSE_PROCESSOR_THREAD_FACTORY =
        new ThreadWorkerFactory("response_processor_", true);
    private static final ExecutorService RESPONSE_PROCESSOR_EXECUTOR_SERVICE =
        buildThreadPool(RESPONSE_PROCESSOR_THREAD_NUMS_MIN, RESPONSE_PROCESSOR_THREAD_NUMS_MAX,
            RESPONSE_TASK_QUEUE_CAPACITY, RESPONSE_PROCESSOR_THREAD_FACTORY);

    public static ExecutorService getResponseProcessorExecutorService() {
        return RESPONSE_PROCESSOR_EXECUTOR_SERVICE;
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

package org.ybonfire.pipeline.common.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 线程池工具类
 *
 * @author Bo.Yuan5
 * @date 2022-05-23 18:01
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ThreadPoolUtil {
    private static final int TEST_THREAD_NUMS_MIN = 4;
    private static final int TEST_THREAD_NUMS_MAX = 4;
    private static final int TEST_QUEUE_CAPACITY = Integer.MAX_VALUE;

    private static final int REQUEST_CALLBACK_THREAD_NUMS_MIN = 4;
    private static final int REQUEST_CALLBACK_THREAD_NUMS_MAX = 4;
    private static final int REQUEST_CALLBACK_QUEUE_CAPACITY = Integer.MAX_VALUE;

    private static final ExecutorService TEST_EXECUTOR_SERVICE =
        buildThreadPool(TEST_THREAD_NUMS_MIN, TEST_THREAD_NUMS_MAX, TEST_QUEUE_CAPACITY, "test_");

    private static final ExecutorService REQUEST_CALLBACK_EXECUTOR_SERVICE =
        buildThreadPool(REQUEST_CALLBACK_THREAD_NUMS_MIN, REQUEST_CALLBACK_THREAD_NUMS_MAX,
            REQUEST_CALLBACK_QUEUE_CAPACITY, "request_callback_");

    public static ExecutorService getTestExecutorService() {
        return TEST_EXECUTOR_SERVICE;
    }

    public static ExecutorService getRequestCallbackExecutorService() {
        return REQUEST_CALLBACK_EXECUTOR_SERVICE;
    }

    /**
     * @description: 构造非固定大小线程池
     * @param:
     * @return:
     * @date: 2022/5/23 18:16:23
     */
    public static ExecutorService buildThreadPool(final int minThreadNums, final int maxThreadNums,
        final int queueCapacity, final String threadNamePrefix) {
        return new ThreadPoolExecutor(minThreadNums, maxThreadNums, 60 * 1000L, TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<>(queueCapacity),
            runnable -> new Thread(runnable, threadNamePrefix + System.currentTimeMillis()));
    }

    /**
     * @description: 构造固定大小线程池
     * @param:
     * @return:rr
     * @date: 2022/5/23 18:16:23
     */
    public static ExecutorService buildFixedSizeThreadPool(final int coreSize, final int queueCapacity,
        final String threadNamePrefix) {
        return new ThreadPoolExecutor(coreSize, coreSize, 0L, TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<>(queueCapacity),
            runnable -> new Thread(runnable, threadNamePrefix + System.currentTimeMillis()));
    }
}

package org.ybonfire.pipeline.common.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
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
    // Request Callback Thread Pool
    private static final int REQUEST_CALLBACK_THREAD_NUMS_MIN = 4;
    private static final int REQUEST_CALLBACK_THREAD_NUMS_MAX = 4;
    private static final int REQUEST_CALLBACK_QUEUE_CAPACITY = Integer.MAX_VALUE;
    private static final ThreadFactory REQUEST_CALLBACK_THREAD_FACTORY =
        new ThreadWorkerFactory("request_callback_", true);
    private static final ExecutorService REQUEST_CALLBACK_EXECUTOR_SERVICE =
        buildThreadPool(REQUEST_CALLBACK_THREAD_NUMS_MIN, REQUEST_CALLBACK_THREAD_NUMS_MAX,
            REQUEST_CALLBACK_QUEUE_CAPACITY, REQUEST_CALLBACK_THREAD_FACTORY);
    // Message Produce Thread Pool
    private static final int MESSAGE_PRODUCE_THREAD_NUMS_MIN = Math.max(8, Runtime.getRuntime().availableProcessors());
    private static final int MESSAGE_PRODUCE_THREAD_NUMS_MAX = Math.max(8, Runtime.getRuntime().availableProcessors());
    private static final int MESSAGE_PRODUCE_QUEUE_CAPACITY = Integer.MAX_VALUE;
    private static final ThreadFactory MESSAGE_PRODUCE_THREAD_FACTORY = new ThreadWorkerFactory("produce_", true);
    private static final ExecutorService MESSAGE_PRODUCE_EXECUTOR_SERVICE =
        buildThreadPool(MESSAGE_PRODUCE_THREAD_NUMS_MIN, MESSAGE_PRODUCE_THREAD_NUMS_MAX,
            MESSAGE_PRODUCE_QUEUE_CAPACITY, MESSAGE_PRODUCE_THREAD_FACTORY);
    // NameServer Handler Thread Pool
    private static final int NAMESERVER_HANDLER_THREAD_NUMS_MIN =
        Math.max(4, Runtime.getRuntime().availableProcessors());
    private static final int NAMESERVER_HANDLER_THREAD_NUMS_MAX =
        Math.max(4, Runtime.getRuntime().availableProcessors());
    private static final int NAMESERVER_HANDLER_QUEUE_CAPACITY = Integer.MAX_VALUE;
    private static final ThreadFactory NAMESERVER_HANDLER_THREAD_FACTORY =
        new ThreadWorkerFactory("namesrv_handler_", true);
    private static final ExecutorService NAMESERVER_HANDLER_EXECUTOR_SERVICE =
        buildThreadPool(NAMESERVER_HANDLER_THREAD_NUMS_MIN, NAMESERVER_HANDLER_THREAD_NUMS_MAX,
            NAMESERVER_HANDLER_QUEUE_CAPACITY, NAMESERVER_HANDLER_THREAD_FACTORY);
    // Response Handler Thread Pool
    private static final int RESPONSE_HANDLER_THREAD_NUMS_MIN = Math.max(4, Runtime.getRuntime().availableProcessors());
    private static final int RESPONSE_HANDLER_THREAD_NUMS_MAX = Math.max(4, Runtime.getRuntime().availableProcessors());
    private static final int RESPONSE_HANDLER_QUEUE_CAPACITY = Integer.MAX_VALUE;
    private static final ThreadFactory RESPONSE_HANDLER_THREAD_FACTORY =
        new ThreadWorkerFactory("response_handler_", true);
    private static final ExecutorService RESPONSE_HANDLER_EXECUTOR_SERVICE =
        buildThreadPool(RESPONSE_HANDLER_THREAD_NUMS_MIN, RESPONSE_HANDLER_THREAD_NUMS_MAX,
            RESPONSE_HANDLER_QUEUE_CAPACITY, RESPONSE_HANDLER_THREAD_FACTORY);

    public static ExecutorService getRequestCallbackExecutorService() {
        return REQUEST_CALLBACK_EXECUTOR_SERVICE;
    }

    public static ExecutorService getMessageProduceExecutorService() {
        return MESSAGE_PRODUCE_EXECUTOR_SERVICE;
    }

    public static ExecutorService getNameserverHandlerExecutorService() {
        return NAMESERVER_HANDLER_EXECUTOR_SERVICE;
    }

    public static ExecutorService getResponseHandlerExecutorService() {
        return RESPONSE_HANDLER_EXECUTOR_SERVICE;
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

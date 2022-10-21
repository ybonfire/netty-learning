package org.ybonfire.pipeline.broker.util;

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
 * @author Bo.Yuan5
 * @date 2022-05-23 18:01
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ThreadPoolUtil {
    // Send Message Processor Thread Pool
    private static final int SEND_MESSAGE_PROCESSOR_THREAD_NUMS_MIN =
        Math.max(8, Runtime.getRuntime().availableProcessors());
    private static final int SEND_MESSAGE_PROCESSOR_THREAD_NUMS_MAX =
        Math.max(8, Runtime.getRuntime().availableProcessors());
    private static final int SEND_MESSAGE_TASK_QUEUE_CAPACITY = Integer.MAX_VALUE;
    private static final ThreadFactory SEND_MESSAGE_PROCESSOR_THREAD_FACTORY =
        new ThreadWorkerFactory("send_message_processor_", true);
    private static final ExecutorService SEND_MESSAGE_PROCESSOR_EXECUTOR_SERVICE =
        buildThreadPool(SEND_MESSAGE_PROCESSOR_THREAD_NUMS_MIN, SEND_MESSAGE_PROCESSOR_THREAD_NUMS_MAX,
            SEND_MESSAGE_TASK_QUEUE_CAPACITY, SEND_MESSAGE_PROCESSOR_THREAD_FACTORY);

    // Pull Message Processor Thread Pool
    private static final int PULL_MESSAGE_PROCESSOR_THREAD_NUMS_MIN =
        Math.max(8, Runtime.getRuntime().availableProcessors());
    private static final int PULL_MESSAGE_PROCESSOR_THREAD_NUMS_MAX =
        Math.max(8, Runtime.getRuntime().availableProcessors());
    private static final int PULL_MESSAGE_TASK_QUEUE_CAPACITY = Integer.MAX_VALUE;
    private static final ThreadFactory CONSUME_MESSAGE_PROCESSOR_THREAD_FACTORY =
        new ThreadWorkerFactory("pull_message_processor_", true);
    private static final ExecutorService PULL_MESSAGE_PROCESSOR_EXECUTOR_SERVICE =
        buildThreadPool(PULL_MESSAGE_PROCESSOR_THREAD_NUMS_MIN, PULL_MESSAGE_PROCESSOR_THREAD_NUMS_MAX,
            PULL_MESSAGE_TASK_QUEUE_CAPACITY, CONSUME_MESSAGE_PROCESSOR_THREAD_FACTORY);

    // Broker Heartbeat Task Thread Pool
    private static final int BROKER_HEARTBEAT_TASK_THREAD_NUMS_MIN =
        Math.max(4, Runtime.getRuntime().availableProcessors());
    private static final int BROKER_HEARTBEAT_TASK_THREAD_NUMS_MAX =
        Math.max(4, Runtime.getRuntime().availableProcessors());
    private static final int BROKER_HEARTBEAT_TASK_QUEUE_CAPACITY = Integer.MAX_VALUE;
    private static final ThreadFactory BROKER_HEARTBEAT_TASK_THREAD_FACTORY =
        new ThreadWorkerFactory("broker_heartbeat_task_", true);
    private static final ExecutorService BROKER_HEARTBEAT_TASK_EXECUTOR_SERVICE =
        buildThreadPool(BROKER_HEARTBEAT_TASK_THREAD_NUMS_MIN, BROKER_HEARTBEAT_TASK_THREAD_NUMS_MAX,
            BROKER_HEARTBEAT_TASK_QUEUE_CAPACITY, BROKER_HEARTBEAT_TASK_THREAD_FACTORY);

    // Broker Admin Thread Pool
    private static final int BROKER_ADMIN_TASK_THREAD_NUMS_MIN =
        Math.max(4, Runtime.getRuntime().availableProcessors());
    private static final int BROKER_ADMIN_TASK_THREAD_NUMS_MAX =
        Math.max(4, Runtime.getRuntime().availableProcessors());
    private static final int BROKER_ADMIN_TASK_QUEUE_CAPACITY = Integer.MAX_VALUE;
    private static final ThreadFactory BROKER_ADMIN_TASK_THREAD_FACTORY =
        new ThreadWorkerFactory("broker_admin_task_", true);
    private static final ExecutorService BROKER_ADMIN_EXECUTOR_SERVICE =
        buildThreadPool(BROKER_ADMIN_TASK_THREAD_NUMS_MIN, BROKER_ADMIN_TASK_THREAD_NUMS_MAX,
            BROKER_ADMIN_TASK_QUEUE_CAPACITY, BROKER_ADMIN_TASK_THREAD_FACTORY);

    public static ExecutorService getSendMessageProcessorExecutorService() {
        return SEND_MESSAGE_PROCESSOR_EXECUTOR_SERVICE;
    }

    public static ExecutorService getPullMessageProcessorExecutorService() {
        return PULL_MESSAGE_PROCESSOR_EXECUTOR_SERVICE;
    }

    public static ExecutorService getBrokerHeartbeatTaskExecutorService() {
        return BROKER_HEARTBEAT_TASK_EXECUTOR_SERVICE;
    }

    public static ExecutorService getBrokerAdminExecutorService() {
        return BROKER_ADMIN_EXECUTOR_SERVICE;
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

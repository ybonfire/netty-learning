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
    // Produce Message Processor Thread Pool
    private static final int PRODUCE_MESSAGE_PROCESSOR_THREAD_NUMS_MIN =
        Math.max(8, Runtime.getRuntime().availableProcessors());
    private static final int PRODUCE_MESSAGE_PROCESSOR_THREAD_NUMS_MAX =
        Math.max(8, Runtime.getRuntime().availableProcessors());
    private static final int PRODUCE_MESSAGE_TASK_QUEUE_CAPACITY = Integer.MAX_VALUE;
    private static final ThreadFactory PRODUCE_MESSAGE_PROCESSOR_THREAD_FACTORY =
        new ThreadWorkerFactory("produce_message_processor_", true);
    private static final ExecutorService PRODUCE_MESSAGE_PROCESSOR_EXECUTOR_SERVICE =
        buildThreadPool(PRODUCE_MESSAGE_PROCESSOR_THREAD_NUMS_MIN, PRODUCE_MESSAGE_PROCESSOR_THREAD_NUMS_MAX,
            PRODUCE_MESSAGE_TASK_QUEUE_CAPACITY, PRODUCE_MESSAGE_PROCESSOR_THREAD_FACTORY);

    // Consume Message Processor Thread Pool
    private static final int CONSUME_MESSAGE_PROCESSOR_THREAD_NUMS_MIN =
        Math.max(8, Runtime.getRuntime().availableProcessors());
    private static final int CONSUME_MESSAGE_PROCESSOR_THREAD_NUMS_MAX =
        Math.max(8, Runtime.getRuntime().availableProcessors());
    private static final int CONSUME_MESSAGE_TASK_QUEUE_CAPACITY = Integer.MAX_VALUE;
    private static final ThreadFactory CONSUME_MESSAGE_PROCESSOR_THREAD_FACTORY =
        new ThreadWorkerFactory("consume_message_processor_", true);
    private static final ExecutorService CONSUME_MESSAGE_PROCESSOR_EXECUTOR_SERVICE =
        buildThreadPool(CONSUME_MESSAGE_PROCESSOR_THREAD_NUMS_MIN, CONSUME_MESSAGE_PROCESSOR_THREAD_NUMS_MAX,
            CONSUME_MESSAGE_TASK_QUEUE_CAPACITY, CONSUME_MESSAGE_PROCESSOR_THREAD_FACTORY);

    // Register Broker Task Thread Pool
    private static final int REGISTER_BROKER_TASK_THREAD_NUMS_MIN =
        Math.max(4, Runtime.getRuntime().availableProcessors());
    private static final int REGISTER_BROKER_TASK_THREAD_NUMS_MAX =
        Math.max(4, Runtime.getRuntime().availableProcessors());
    private static final int REGISTER_BROKER_TASK_QUEUE_CAPACITY = Integer.MAX_VALUE;
    private static final ThreadFactory REGISTER_BROKER_TASK_THREAD_FACTORY =
        new ThreadWorkerFactory("register_broker_task_", true);
    private static final ExecutorService REGISTER_BROKER_TASK_EXECUTOR_SERVICE =
        buildThreadPool(REGISTER_BROKER_TASK_THREAD_NUMS_MIN, REGISTER_BROKER_TASK_THREAD_NUMS_MAX,
            REGISTER_BROKER_TASK_QUEUE_CAPACITY, REGISTER_BROKER_TASK_THREAD_FACTORY);

    // MessageLog Listener Thread Pool
    private static final int MESSAGE_LOG_LISTENER_TASK_THREAD_NUMS_MIN =
        Math.max(4, Runtime.getRuntime().availableProcessors());
    private static final int MESSAGE_LOG_LISTENER_TASK_THREAD_NUMS_MAX =
        Math.max(4, Runtime.getRuntime().availableProcessors());
    private static final int MESSAGE_LOG_LISTENER_TASK_QUEUE_CAPACITY = Integer.MAX_VALUE;
    private static final ThreadFactory MESSAGE_LOG_LISTENER_TASK_THREAD_FACTORY =
        new ThreadWorkerFactory("message_log_listener_task_", true);
    private static final ExecutorService MESSAGE_LOG_LISTENER_TASK_EXECUTOR_SERVICE =
        buildThreadPool(MESSAGE_LOG_LISTENER_TASK_THREAD_NUMS_MIN, MESSAGE_LOG_LISTENER_TASK_THREAD_NUMS_MAX,
            MESSAGE_LOG_LISTENER_TASK_QUEUE_CAPACITY, MESSAGE_LOG_LISTENER_TASK_THREAD_FACTORY);

    public static ExecutorService getProduceMessageProcessorExecutorService() {
        return PRODUCE_MESSAGE_PROCESSOR_EXECUTOR_SERVICE;
    }

    public static ExecutorService getConsumeMessageProcessorExecutorService() {
        return CONSUME_MESSAGE_PROCESSOR_EXECUTOR_SERVICE;
    }

    public static ExecutorService getRegisterBrokerTaskExecutorService() {
        return REGISTER_BROKER_TASK_EXECUTOR_SERVICE;
    }

    public static ExecutorService getMessageLogListenerTaskExecutorService() {
        return MESSAGE_LOG_LISTENER_TASK_EXECUTOR_SERVICE;
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

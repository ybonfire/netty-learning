package org.ybonfire.pipeline.nameserver.util;

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
    // NameServer Processor Thread Pool
    private static final int NAMESERVER_PROCESSOR_THREAD_NUMS_MIN =
        Math.max(4, Runtime.getRuntime().availableProcessors());
    private static final int NAMESERVER_PROCESSOR_THREAD_NUMS_MAX =
        Math.max(4, Runtime.getRuntime().availableProcessors());
    private static final int NAMESERVER_TASK_QUEUE_CAPACITY = Integer.MAX_VALUE;
    private static final ThreadFactory NAMESERVER_PROCESSOR_THREAD_FACTORY =
        new ThreadWorkerFactory("namesrv_processor_", true);
    private static final ExecutorService NAMESERVER_PROCESSOR_EXECUTOR_SERVICE =
        buildThreadPool(NAMESERVER_PROCESSOR_THREAD_NUMS_MIN, NAMESERVER_PROCESSOR_THREAD_NUMS_MAX,
            NAMESERVER_TASK_QUEUE_CAPACITY, NAMESERVER_PROCESSOR_THREAD_FACTORY);
    // RouteUploadRequestPublish Thread Pool
    private static final int ROUTE_UPLOAD_REQUEST_PUBLISH_THREAD_NUMS_MIN =
        Math.max(4, Runtime.getRuntime().availableProcessors());
    private static final int ROUTE_UPLOAD_REQUEST_PUBLISH_THREAD_NUMS_MAX =
        Math.max(4, Runtime.getRuntime().availableProcessors());
    private static final int ROUTE_UPLOAD_REQUEST_PUBLISH_TASK_QUEUE_CAPACITY = Integer.MAX_VALUE;
    private static final ThreadFactory ROUTE_UPLOAD_REQUEST_PUBLISH_THREAD_FACTORY =
        new ThreadWorkerFactory("route_upload_request_publish_", true);
    private static final ExecutorService ROUTE_UPLOAD_REQUEST_PUBLISH_EXECUTOR_SERVICE =
        buildThreadPool(ROUTE_UPLOAD_REQUEST_PUBLISH_THREAD_NUMS_MIN, ROUTE_UPLOAD_REQUEST_PUBLISH_THREAD_NUMS_MAX,
            ROUTE_UPLOAD_REQUEST_PUBLISH_TASK_QUEUE_CAPACITY, ROUTE_UPLOAD_REQUEST_PUBLISH_THREAD_FACTORY);

    public static ExecutorService getNameserverProcessorExecutorService() {
        return NAMESERVER_PROCESSOR_EXECUTOR_SERVICE;
    }

    public static ExecutorService getRouteUploadRequestPublishExecutorService() {
        return ROUTE_UPLOAD_REQUEST_PUBLISH_EXECUTOR_SERVICE;
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

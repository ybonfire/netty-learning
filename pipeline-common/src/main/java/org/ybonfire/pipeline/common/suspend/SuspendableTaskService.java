package org.ybonfire.pipeline.common.suspend;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.ybonfire.pipeline.common.suspend.callback.ISuspendableTaskActivateCallback;
import org.ybonfire.pipeline.common.suspend.model.SuspendableTask;
import org.ybonfire.pipeline.common.thread.service.AbstractThreadService;

/**
 * 可挂起任务服务
 *
 * @author Bo.Yuan5
 * @date 2022-08-19 15:28
 */
public class SuspendableTaskService {
    private final Map<String, SuspendableTask<?>> suspendingTasks = new ConcurrentHashMap<>();
    private final Scheduler scheduler = new Scheduler(50L);

    public void start() {
        scheduler.start();
    }

    public void shutdown() {
        scheduler.stop();
    }

    /**
     * @description: 注册挂起任务
     * @param:
     * @return:
     * @date: 2022/08/19 15:37:55
     */
    public void register(final SuspendableTask<?> task) {
        suspendingTasks.putIfAbsent(task.getTaskId(), task);
    }

    /**
     * @description: 注销挂起任务
     * @param:
     * @return:
     * @date: 2022/08/19 16:02:52
     */
    public void deregister(final String taskId) {
        suspendingTasks.remove(taskId);
    }

    /**
     * @description: 激活全部过期任务
     * @param:
     * @return:
     * @date: 2022/08/19 18:24:49
     */
    public void activate() {
        final Iterator<Map.Entry<String, SuspendableTask<?>>> iterator = suspendingTasks.entrySet().iterator();
        while (iterator.hasNext()) {
            final SuspendableTask<?> task = iterator.next().getValue();
            // 移除挂起任务
            iterator.remove();
            // 执行激活回调
            final ISuspendableTaskActivateCallback callback = task.getCallback();
            if (callback != null) {
                callback.onActivate(task);
            }
        }
    }

    /**
     * @description:
     * @author: Bo.Yuan5
     * @date: 2022/8/19
     */
    private class Scheduler extends AbstractThreadService {
        private static final String THREAD_SERVICE_NAME = "SuspendTaskServiceScheduler";

        private Scheduler(final long intervalMillis) {
            super(intervalMillis);
        }

        @Override
        protected String getName() {
            return THREAD_SERVICE_NAME;
        }

        @Override
        protected void execute() {
            // 扫描全部挂起任务
            final Iterator<Map.Entry<String, SuspendableTask<?>>> iterator = suspendingTasks.entrySet().iterator();
            while (iterator.hasNext()) {
                final SuspendableTask<?> task = iterator.next().getValue();
                // 判断任务是否到期
                if (isExpired(task)) {
                    // 移除挂起任务
                    iterator.remove();
                    // 执行过期回调
                    final ISuspendableTaskActivateCallback callback = task.getCallback();
                    if (callback != null) {
                        callback.onTimeout(task);
                    }
                }
            }
        }

        /**
         * @description: 判断挂起任务是否到期
         * @param:
         * @return:
         * @date: 2022/08/19 16:21:46
         */
        private boolean isExpired(final SuspendableTask<?> task) {
            return System.currentTimeMillis() > task.getExpireTimestamp();
        }
    }
}

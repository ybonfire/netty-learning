package org.ybonfire.pipeline.common.thread.task;

import lombok.extern.slf4j.Slf4j;

import java.util.UUID;

/**
 * 异步线程任务模板
 *
 * @author Bo.Yuan5
 * @date 2022-05-18 17:11
 */
@Slf4j
public abstract class AbstractThreadTask implements Runnable {
    private final String taskId;
    private final IThreadTaskExecuteFailedCallback threadTaskExecuteFailedCallback;

    protected AbstractThreadTask() {
        this.taskId = UUID.randomUUID().toString();
        this.threadTaskExecuteFailedCallback = (threadTask, e) -> log.error("ThreadTask Execute failed.", e);
    }

    protected AbstractThreadTask(final String taskId,
        final IThreadTaskExecuteFailedCallback threadTaskExecuteFailedCallback) {
        this.taskId = taskId;
        this.threadTaskExecuteFailedCallback = threadTaskExecuteFailedCallback;
    }

    /**
     * @description: 异步任务
     * @param:
     * @return:
     * @date: 2022/5/18 17:13:35
     */
    @Override
    public final void run() {
        try {
            execute();
        } catch (Throwable e) {
            if (threadTaskExecuteFailedCallback != null) {
                threadTaskExecuteFailedCallback.onException(this, e);
            } else {
                log.error("ThreadTask Execute failed.", e);
            }
        }
    }

    public String getTaskId() {
        return taskId;
    }

    /**
     * @description: 执行异步任务
     * @param:
     * @return:
     * @date: 2022/5/18 17:13:45
     */
    protected abstract void execute();
}

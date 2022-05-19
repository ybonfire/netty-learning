package org.ybonfire.netty.common.thread;

/**
 * 线程任务执行失败回调接口
 *
 * @author Bo.Yuan5
 * @date 2022-05-18 17:03
 */
@FunctionalInterface
public interface IThreadTaskExecuteFailedCallback {

    /**
     * @description: 线程任务失败回调流程
     * @param:
     * @return:
     * @date: 2022/5/18 17:12:45
     */
    void onException(final AbstractThreadTask threadTask, final Throwable e);
}

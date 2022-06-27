package org.ybonfire.pipeline.common.thread.service;

/**
 * 线程任务执行失败回调接口
 *
 * @author yuanbo@megvii.com
 * @date 2021-03-22 17:03
 */
@FunctionalInterface
public interface IThreadServiceExecuteFailedCallback {
    /**
     * @description: 线程任务失败回调流程
     * @param:
     * @return:
     * @date: 2021/3/22
     */
    void onException(final AbstractThreadService threadService, final Throwable e);
}

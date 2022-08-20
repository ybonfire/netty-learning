package org.ybonfire.pipeline.common.suspend.callback;

import org.ybonfire.pipeline.common.suspend.model.SuspendableTask;

/**
 * 可挂起任务激活回调接口
 *
 * @author Bo.Yuan5
 * @date 2022-08-19 15:54
 */
public interface ISuspendableTaskActivateCallback {

    /**
     * @description: 超时回调流程
     * @param:
     * @return:
     * @date: 2022/08/19 18:28:12
     */
    void onTimeout(final SuspendableTask<?> task);

    /**
     * @description: 激活回调流程
     * @param:
     * @return:
     * @date: 2022/08/19 18:28:19
     */
    void onActivate(final SuspendableTask<?> task);
}

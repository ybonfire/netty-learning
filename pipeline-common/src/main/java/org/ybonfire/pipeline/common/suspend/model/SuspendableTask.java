package org.ybonfire.pipeline.common.suspend.model;

import lombok.Builder;
import lombok.Data;
import org.ybonfire.pipeline.common.suspend.callback.ISuspendableTaskActivateCallback;

/**
 * 可挂起任务
 *
 * @author Bo.Yuan5
 * @date 2022-08-19 15:55
 */
@Builder
@Data
public class SuspendableTask<T> {
    /**
     * 挂起任务Id
     */
    private final String taskId;
    /**
     * 相关数据
     */
    private final T data;
    /**
     * 到期时间
     */
    private final long expireTimestamp;
    /**
     * 激活回调
     */
    private final ISuspendableTaskActivateCallback callback;
}

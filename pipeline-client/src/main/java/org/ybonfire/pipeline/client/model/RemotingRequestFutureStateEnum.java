package org.ybonfire.pipeline.client.model;

/**
 * RemotingRequestFuture状态枚举
 *
 * @author Bo.Yuan5
 * @date 2022-08-05 10:50
 */
public enum RemotingRequestFutureStateEnum {
    /**
     * 待发送
     */
    LAUNCH(0, "待发送"),
    /**
     * 在途中
     */
    INFLIGHT(1, "在途中"),
    /**
     * 已响应
     */
    RESPOND(2, "已回应"),
    /**
     * 失败
     */
    FAILED(-1, "已失败");

    private final int code;
    private final String description;

    RemotingRequestFutureStateEnum(final int code, final String description) {
        this.code = code;
        this.description = description;
    }
}

package org.ybonfire.pipeline.client.model;

/**
 * RemotingRequestFuture状态枚举
 *
 * @author Bo.Yuan5
 * @date 2022-08-05 10:50
 */
public enum RemotingRequestFutureStateEnum {
    /**
     * 在途中
     */
    FLIGHT(0, "在途中"),
    /**
     * 已响应
     */
    RESPOND(1, "已回应");

    private int code;
    private String description;

    RemotingRequestFutureStateEnum(int code, String description) {
        this.code = code;
        this.description = description;
    }
}
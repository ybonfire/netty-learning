package org.ybonfire.pipeline.common.model;

/**
 * Netty Channel Event类型枚举
 *
 * @author Bo.Yuan5
 * @date 2022-05-24 18:18
 */
public enum NettyChannelEventTypeEnum {
    /**
     * 建立连接
     */
    OPEN(1, "建立连接"),
    /**
     * 关闭连接
     */
    CLOSE(2, "关闭连接");

    private final int code;
    private final String description;

    NettyChannelEventTypeEnum(final int code, final String description) {
        this.code = code;
        this.description = description;
    }
}

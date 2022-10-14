package org.ybonfire.pipeline.broker.model.store;

/**
 * 消息刷盘策略枚举
 *
 * @author yuanbo
 * @date 2022-10-06 16:29
 */
public enum MessageFlushPolicyEnum {
    /**
     * 异步刷盘
     */
    ASYNC(0, "异步"),
    /**
     * 同步刷盘
     */
    SYNC(1, "同步");

    private int code;
    private String description;

    MessageFlushPolicyEnum(final int code, final String description) {
        this.code = code;
        this.description = description;
    }

    public static MessageFlushPolicyEnum of(final int code) {
        for (final MessageFlushPolicyEnum type : MessageFlushPolicyEnum.values()) {
            if (type.code == code) {
                return type;
            }
        }

        return null;
    }
}

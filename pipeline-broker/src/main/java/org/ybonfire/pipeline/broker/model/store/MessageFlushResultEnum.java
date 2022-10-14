package org.ybonfire.pipeline.broker.model.store;

/**
 * 消息输盘结果枚举
 *
 * @author yuanbo
 * @date 2022-10-06 17:22
 */
public enum MessageFlushResultEnum {
    /**
     * 刷盘成功
     */
    SUCCESS(1, "刷盘成功"),
    /**
     * 刷盘失败
     */
    FAILED(-1, "输盘失败");

    private int code;
    private String description;

    MessageFlushResultEnum(final int code, final String description) {
        this.code = code;
        this.description = description;
    }
}

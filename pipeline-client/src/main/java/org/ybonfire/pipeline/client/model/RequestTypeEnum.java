package org.ybonfire.pipeline.client.model;

/**
 * 请求类型枚举
 *
 * @author Bo.Yuan5
 * @date 2022-05-19 14:07
 */
public enum RequestTypeEnum {
    /**
     * 同步
     */
    SYNC(2, "同步请求"),
    /**
     * 异步
     */
    ASYNC(1, "异步请求"),
    /**
     * 单向
     */
    ONEWAY(0, "单向请求");

    private int code;
    private String description;

    RequestTypeEnum(final int code, final String description) {
        this.code = code;
        this.description = description;
    }
}

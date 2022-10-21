package org.ybonfire.pipeline.broker.model.message;

/**
 * 消息查询结果状态
 *
 * @author yuanbo
 * @date 2022-10-21 10:38
 */
public enum SelectMessageResultTypeEnum {
    /**
     * 查询到数据
     */
    FOUND(1, "FOUND"),
    /**
     * 未查询到数据
     */
    NOT_FOUND(0, "NOT_FOUND"),
    /**
     * 路由异常
     */
    BAD_ROUTE(-1, "BAD_ROUTE"),;

    private final int code;
    private final String description;

    SelectMessageResultTypeEnum(final int code, final String description) {
        this.code = code;
        this.description = description;
    }

    public int getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }
}

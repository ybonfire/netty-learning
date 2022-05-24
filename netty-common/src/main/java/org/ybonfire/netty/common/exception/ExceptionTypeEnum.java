package org.ybonfire.netty.common.exception;

import org.omg.CORBA.UNKNOWN;

/**
 * 异常码常量类
 *
 * @author Bo.Yuan5
 * @date 2022-05-19 10:19
 */
public enum ExceptionTypeEnum {
    /**
     * 连接超时
     */
    CONNECT_TIMEOUT(1, "连接超时"),
    /**
     * 连接失败
     */
    CONNECT_FAILED(2, "连接失败"),
    /**
     * 请求超时
     */
    REQUEST_TIMEOUT(3, "请求超时"),
    /**
     * 未知异常
     */
    UNKNOWN(-1, "未知异常");

    private int code;
    private String description;

    ExceptionTypeEnum(int code, String description) {
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

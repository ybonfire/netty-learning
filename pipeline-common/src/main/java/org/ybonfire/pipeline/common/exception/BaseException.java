package org.ybonfire.pipeline.common.exception;

/**
 * 基础异常
 *
 * @author Bo.Yuan5
 * @date 2022-05-19 10:16
 */
public class BaseException extends RuntimeException {
    private final ExceptionTypeEnum type;

    public BaseException(final ExceptionTypeEnum type) {
        this(type, null);
    }

    public BaseException(final ExceptionTypeEnum type, final Throwable cause) {
        super(type.getDescription(), cause);
        this.type = type;
    }

    public ExceptionTypeEnum getType() {
        return type;
    }
}

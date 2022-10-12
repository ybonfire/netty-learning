package org.ybonfire.pipeline.client.exception;

/**
 * 不支持的请求类型异常
 *
 * @author yuanbo
 * @date 2022-10-12 14:40
 */
public final class UnSupportedRequestTypeException extends ClientException {

    public UnSupportedRequestTypeException() {}

    public UnSupportedRequestTypeException(final String message) {
        super(message);
    }

    public UnSupportedRequestTypeException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public UnSupportedRequestTypeException(final Throwable cause) {
        super(cause);
    }
}

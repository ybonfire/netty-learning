package org.ybonfire.pipeline.client.exception;

/**
 * 连接超时异常
 *
 * @author yuanbo
 * @date 2022-09-09 16:26
 */
public final class ConnectTimeoutException extends ClientException {

    public ConnectTimeoutException() {}

    public ConnectTimeoutException(final String message) {
        super(message);
    }

    public ConnectTimeoutException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public ConnectTimeoutException(final Throwable cause) {
        super(cause);
    }
}

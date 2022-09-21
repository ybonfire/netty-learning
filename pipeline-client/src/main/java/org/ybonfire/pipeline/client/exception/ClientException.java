package org.ybonfire.pipeline.client.exception;

/**
 * 客户端异常
 *
 * @author yuanbo
 * @date 2022-09-09 16:21
 */
public abstract class ClientException extends RuntimeException {

    protected ClientException() {}

    protected ClientException(final String message) {
        super(message);
    }

    protected ClientException(final String message, final Throwable cause) {
        super(message, cause);
    }

    protected ClientException(final Throwable cause) {
        super(cause);
    }
}

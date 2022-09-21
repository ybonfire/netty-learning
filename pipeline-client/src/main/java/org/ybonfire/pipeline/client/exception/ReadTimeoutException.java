package org.ybonfire.pipeline.client.exception;

/**
 * 等待响应超时异常
 *
 * @author yuanbo
 * @date 2022-09-09 16:38
 */
public class ReadTimeoutException extends ClientException {

    public ReadTimeoutException() {}

    public ReadTimeoutException(final String message) {
        super(message);
    }

    public ReadTimeoutException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public ReadTimeoutException(final Throwable cause) {
        super(cause);
    }
}

package org.ybonfire.pipeline.producer.exception;

import org.ybonfire.pipeline.client.exception.ClientException;

/**
 * 非法消息异常
 *
 * @author yuanbo
 * @date 2022-09-20 18:56
 */
public class IllegalMessageException extends ClientException {

    public IllegalMessageException() {}

    public IllegalMessageException(final String message) {
        super(message);
    }

    public IllegalMessageException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public IllegalMessageException(final Throwable cause) {
        super(cause);
    }
}

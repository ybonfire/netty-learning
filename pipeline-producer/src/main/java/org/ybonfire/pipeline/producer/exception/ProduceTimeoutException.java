package org.ybonfire.pipeline.producer.exception;

import org.ybonfire.pipeline.client.exception.ClientException;

/**
 * 生产超时异常
 *
 * @author yuanbo
 * @date 2022-09-09 18:06
 */
public class ProduceTimeoutException extends ClientException {

    public ProduceTimeoutException() {}

    public ProduceTimeoutException(final String message) {
        super(message);
    }

    public ProduceTimeoutException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public ProduceTimeoutException(final Throwable cause) {
        super(cause);
    }
}

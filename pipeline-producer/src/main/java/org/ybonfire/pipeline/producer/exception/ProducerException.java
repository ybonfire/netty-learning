package org.ybonfire.pipeline.producer.exception;

/**
 * 生产者业务异常
 *
 * @author yuanbo
 * @date 2022-09-09 18:11
 */
public class ProducerException extends RuntimeException {

    public ProducerException() {}

    public ProducerException(final String message) {
        super(message);
    }

    public ProducerException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public ProducerException(final Throwable cause) {
        super(cause);
    }
}

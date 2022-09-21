package org.ybonfire.pipeline.client.exception;

/**
 * 调用中断异常
 *
 * @author yuanbo
 * @date 2022-09-09 17:19
 */
public class InvokeInterruptedException extends ClientException {

    public InvokeInterruptedException() {}

    public InvokeInterruptedException(final String message) {
        super(message);
    }

    public InvokeInterruptedException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public InvokeInterruptedException(final Throwable cause) {
        super(cause);
    }
}

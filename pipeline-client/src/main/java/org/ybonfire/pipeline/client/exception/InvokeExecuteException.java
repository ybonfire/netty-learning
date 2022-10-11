package org.ybonfire.pipeline.client.exception;

/**
 * 调用执行异常
 *
 * @author yuanbo
 * @date 2022-09-09 17:14
 */
public final class InvokeExecuteException extends ClientException {

    public InvokeExecuteException() {}

    public InvokeExecuteException(final String message) {
        super(message);
    }

    public InvokeExecuteException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public InvokeExecuteException(final Throwable cause) {
        super(cause);
    }
}

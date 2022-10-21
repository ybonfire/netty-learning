package org.ybonfire.pipeline.client.exception;

/**
 * 远程调用执行异常
 *
 * @author yuanbo
 * @date 2022-09-09 17:14
 */
public final class RemotingInvokeExecuteException extends ClientException {

    public RemotingInvokeExecuteException() {}

    public RemotingInvokeExecuteException(final String message) {
        super(message);
    }

    public RemotingInvokeExecuteException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public RemotingInvokeExecuteException(final Throwable cause) {
        super(cause);
    }
}

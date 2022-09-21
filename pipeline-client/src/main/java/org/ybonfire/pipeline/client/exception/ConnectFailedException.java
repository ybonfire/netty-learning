package org.ybonfire.pipeline.client.exception;

/**
 * 连接失败异常
 *
 * @author yuanbo
 * @date 2022-09-09 16:29
 */
public class ConnectFailedException extends ClientException {

    public ConnectFailedException() {}

    public ConnectFailedException(final String message) {
        super(message);
    }

    public ConnectFailedException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public ConnectFailedException(final Throwable cause) {
        super(cause);
    }
}

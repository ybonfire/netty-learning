package org.ybonfire.netty.common.exception;

/**
 * 连接超时异常
 *
 * @author Bo.Yuan5
 * @date 2022-05-19 10:16
 */
public class ConnectTimeoutException extends BaseException {

    public ConnectTimeoutException() {
        super(ExceptionTypeEnum.CONNECT_TIMEOUT);
    }

    public ConnectTimeoutException(final Throwable cause) {
        super(ExceptionTypeEnum.CONNECT_TIMEOUT, cause);
    }
}

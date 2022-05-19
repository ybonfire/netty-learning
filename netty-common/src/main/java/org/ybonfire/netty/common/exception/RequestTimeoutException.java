package org.ybonfire.netty.common.exception;

/**
 * 调用超时异常
 *
 * @author Bo.Yuan5
 * @date 2022-05-19 13:38
 */
public class RequestTimeoutException extends BaseException {
    public RequestTimeoutException() {
        super(ExceptionTypeEnum.REQUEST_TIMEOUT);
    }

    public RequestTimeoutException(final Throwable cause) {
        super(ExceptionTypeEnum.REQUEST_TIMEOUT, cause);
    }
}

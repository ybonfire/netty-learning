package org.ybonfire.pipeline.server.exception;

import org.ybonfire.pipeline.common.constant.ResponseEnum;

/**
 * 未知内部异常
 *
 * @author yuanbo
 * @date 2022-09-09 14:38
 */
public class UnknownException extends ServerException {
    private static final ResponseEnum RESPONSE_TYPE = ResponseEnum.UNKNOWN_ERROR;

    public UnknownException() {}

    public UnknownException(final String message) {
        super(message);
    }

    public UnknownException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public UnknownException(final Throwable cause) {
        super(cause);
    }

    @Override
    public ResponseEnum getResponseType() {
        return RESPONSE_TYPE;
    }
}

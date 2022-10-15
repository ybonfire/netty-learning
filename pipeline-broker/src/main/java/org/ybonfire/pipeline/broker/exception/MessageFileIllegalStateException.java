package org.ybonfire.pipeline.broker.exception;

import org.ybonfire.pipeline.common.constant.ResponseEnum;
import org.ybonfire.pipeline.server.exception.ServerException;

/**
 * MessageLog非法状态异常
 *
 * @author yuanbo
 * @date 2022-10-15 10:46
 */
public final class MessageFileIllegalStateException extends ServerException {
    private static final ResponseEnum RESPONSE_TYPE = ResponseEnum.INTERNAL_SERVER_ERROR;

    public MessageFileIllegalStateException() {}

    public MessageFileIllegalStateException(final String message) {
        super(message);
    }

    public MessageFileIllegalStateException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public MessageFileIllegalStateException(final Throwable cause) {
        super(cause);
    }

    @Override
    public ResponseEnum getResponseType() {
        return RESPONSE_TYPE;
    }
}

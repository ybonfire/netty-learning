package org.ybonfire.pipeline.broker.exception;

import org.ybonfire.pipeline.common.constant.ResponseEnum;
import org.ybonfire.pipeline.server.exception.ServerException;

/**
 * 消息文件没有足够空间异常
 *
 * @author yuanbo
 * @date 2022-09-21 14:29
 */
public final class MessageFileNotEnoughSpaceException extends ServerException {
    private static final ResponseEnum RESPONSE_TYPE = ResponseEnum.MESSAGE_FILE_NOT_ENOUGH_SPACE;

    public MessageFileNotEnoughSpaceException() {}

    public MessageFileNotEnoughSpaceException(final String message) {
        super(message);
    }

    public MessageFileNotEnoughSpaceException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public MessageFileNotEnoughSpaceException(final Throwable cause) {
        super(cause);
    }

    @Override
    public ResponseEnum getResponseType() {
        return RESPONSE_TYPE;
    }
}

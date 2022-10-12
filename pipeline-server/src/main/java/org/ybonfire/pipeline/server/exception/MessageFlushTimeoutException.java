package org.ybonfire.pipeline.server.exception;

import org.ybonfire.pipeline.common.constant.ResponseEnum;

/**
 * 消息刷盘超时异常
 *
 * @author yuanbo
 * @date 2022-10-06 17:36
 */
public class MessageFlushTimeoutException extends ServerException {
    private static final ResponseEnum RESPONSE_TYPE = ResponseEnum.MESSAGE_FLUSH_DISK_TIMEOUT;

    public MessageFlushTimeoutException() {}

    public MessageFlushTimeoutException(final String message) {
        super(message);
    }

    public MessageFlushTimeoutException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public MessageFlushTimeoutException(final Throwable cause) {
        super(cause);
    }

    @Override
    public ResponseEnum getResponseType() {
        return RESPONSE_TYPE;
    }
}

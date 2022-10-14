package org.ybonfire.pipeline.broker.exception;

import org.ybonfire.pipeline.common.constant.ResponseEnum;
import org.ybonfire.pipeline.server.exception.ServerException;

/**
 * 未知Topic异常
 *
 * @author yuanbo
 * @date 2022-10-14 16:48
 */
public final class TopicNotFoundException extends ServerException {
    private static final ResponseEnum RESPONSE_TYPE = ResponseEnum.TOPIC_NOT_FOUND;

    public TopicNotFoundException() {}

    public TopicNotFoundException(final String message) {
        super(message);
    }

    public TopicNotFoundException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public TopicNotFoundException(final Throwable cause) {
        super(cause);
    }

    @Override
    public ResponseEnum getResponseType() {
        return RESPONSE_TYPE;
    }
}

package org.ybonfire.pipeline.broker.exception;

import org.ybonfire.pipeline.common.constant.ResponseEnum;
import org.ybonfire.pipeline.server.exception.ServerException;

/**
 * Topic重复创建异常
 *
 * @author yuanbo
 * @date 2022-10-14 16:41
 */
public final class TopicAlreadyCreatedException extends ServerException {
    private static final ResponseEnum RESPONSE_TYPE = ResponseEnum.TOPIC_ALREADY_CREATED;

    public TopicAlreadyCreatedException() {}

    public TopicAlreadyCreatedException(final String message) {
        super(message);
    }

    public TopicAlreadyCreatedException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public TopicAlreadyCreatedException(final Throwable cause) {
        super(cause);
    }

    @Override
    public ResponseEnum getResponseType() {
        return RESPONSE_TYPE;
    }
}

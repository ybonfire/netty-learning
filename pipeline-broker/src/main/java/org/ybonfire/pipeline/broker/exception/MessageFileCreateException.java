package org.ybonfire.pipeline.broker.exception;

import org.ybonfire.pipeline.common.constant.ResponseEnum;
import org.ybonfire.pipeline.server.exception.ServerException;

/**
 * 消息文件创建异常
 *
 * @author yuanbo
 * @date 2022-09-21 14:25
 */
public class MessageFileCreateException extends ServerException {
    private final ResponseEnum responseType = ResponseEnum.MESSAGE_FILE_CREATE_FAILED;

    public MessageFileCreateException() {}

    public MessageFileCreateException(final String message) {
        super(message);
    }

    public MessageFileCreateException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public MessageFileCreateException(final Throwable cause) {
        super(cause);
    }

    @Override
    public ResponseEnum getResponseType() {
        return responseType;
    }
}

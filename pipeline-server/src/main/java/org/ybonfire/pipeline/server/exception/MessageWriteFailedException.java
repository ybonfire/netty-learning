package org.ybonfire.pipeline.server.exception;

import org.ybonfire.pipeline.common.constant.ResponseEnum;

/**
 * 消息写入失败异常
 *
 * @author yuanbo
 * @date 2022-10-06 18:31
 */
public class MessageWriteFailedException extends ServerException {
    private final ResponseEnum responseType = ResponseEnum.Message_WRITE_FAILED;

    public MessageWriteFailedException() {}

    public MessageWriteFailedException(final String message) {
        super(message);
    }

    public MessageWriteFailedException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public MessageWriteFailedException(final Throwable cause) {
        super(cause);
    }

    @Override
    public ResponseEnum getResponseType() {
        return responseType;
    }
}

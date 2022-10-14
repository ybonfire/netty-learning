package org.ybonfire.pipeline.broker.exception;

import org.ybonfire.pipeline.common.constant.ResponseEnum;
import org.ybonfire.pipeline.server.exception.ServerException;

/**
 * 索引文件创建异常
 *
 * @author yuanbo
 * @date 2022-10-11 16:19
 */
public final class IndexFileCreateException extends ServerException {
    private static final ResponseEnum RESPONSE_TYPE = ResponseEnum.INTERNAL_SERVER_ERROR;

    public IndexFileCreateException() {}

    public IndexFileCreateException(final String message) {
        super(message);
    }

    public IndexFileCreateException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public IndexFileCreateException(final Throwable cause) {
        super(cause);
    }

    @Override
    public ResponseEnum getResponseType() {
        return RESPONSE_TYPE;
    }
}

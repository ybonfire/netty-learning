package org.ybonfire.pipeline.broker.exception;

import org.ybonfire.pipeline.common.constant.ResponseEnum;
import org.ybonfire.pipeline.server.exception.ServerException;

/**
 * 文件数据读取异常
 *
 * @author yuanbo
 * @date 2022-10-19 17:19
 */
public final class FileReadException extends ServerException {
    private static final ResponseEnum RESPONSE_TYPE = ResponseEnum.INTERNAL_SERVER_ERROR;

    public FileReadException() {}

    public FileReadException(final String message) {
        super(message);
    }

    public FileReadException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public FileReadException(final Throwable cause) {
        super(cause);
    }

    @Override
    public ResponseEnum getResponseType() {
        return RESPONSE_TYPE;
    }

}

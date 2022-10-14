package org.ybonfire.pipeline.broker.exception;

import org.ybonfire.pipeline.common.constant.ResponseEnum;
import org.ybonfire.pipeline.server.exception.ServerException;

/**
 * 文件数据加载异常
 *
 * @author yuanbo
 * @date 2022-10-13 11:07
 */
public final class FileLoadException extends ServerException {
    private static final ResponseEnum RESPONSE_TYPE = ResponseEnum.INTERNAL_SERVER_ERROR;

    public FileLoadException() {}

    public FileLoadException(final String message) {
        super(message);
    }

    public FileLoadException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public FileLoadException(final Throwable cause) {
        super(cause);
    }

    @Override
    public ResponseEnum getResponseType() {
        return RESPONSE_TYPE;
    }
}

package org.ybonfire.pipeline.server.exception;

import org.ybonfire.pipeline.common.constant.ResponseEnum;

/**
 * 服务端异常
 *
 * @author yuanbo
 * @date 2022-09-09 14:21
 */
public abstract class ServerException extends RuntimeException {
    protected ServerException() {}

    protected ServerException(final String message) {
        super(message);
    }

    protected ServerException(final String message, final Throwable cause) {
        super(message, cause);
    }

    protected ServerException(final Throwable cause) {
        super(cause);
    }

    public abstract ResponseEnum getResponseType();
}

package org.ybonfire.pipeline.server.exception;

import org.ybonfire.pipeline.common.constant.ResponseEnum;

/**
 * 服务端启动异常
 *
 * @author yuanbo
 * @date 2022-10-12 17:42
 */
public class StartupException extends ServerException {

    public StartupException() {}

    public StartupException(final String message) {
        super(message);
    }

    public StartupException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public StartupException(final Throwable cause) {
        super(cause);
    }

    @Override
    public ResponseEnum getResponseType() {
        return null;
    }
}

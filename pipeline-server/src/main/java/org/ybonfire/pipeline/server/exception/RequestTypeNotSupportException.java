package org.ybonfire.pipeline.server.exception;

import org.ybonfire.pipeline.common.constant.ResponseEnum;

/**
 * 不支持的请求类型异常
 *
 * @author yuanbo
 * @date 2022-09-09 14:25
 */
public class RequestTypeNotSupportException extends ServerException {
    private final ResponseEnum responseType = ResponseEnum.REQUEST_TYPE_NOT_SUPPORTED;

    public RequestTypeNotSupportException() {}

    public RequestTypeNotSupportException(final String message) {
        super(message);
    }

    public RequestTypeNotSupportException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public RequestTypeNotSupportException(final Throwable cause) {
        super(cause);
    }

    @Override
    public ResponseEnum getResponseType() {
        return responseType;
    }
}

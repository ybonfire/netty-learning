package org.ybonfire.pipeline.server.exception.handler;

import org.apache.commons.lang3.ObjectUtils;
import org.ybonfire.pipeline.common.constant.ResponseEnum;
import org.ybonfire.pipeline.common.protocol.IRemotingRequest;
import org.ybonfire.pipeline.common.protocol.RemotingResponse;
import org.ybonfire.pipeline.common.protocol.response.DefaultResponse;
import org.ybonfire.pipeline.server.exception.ServerException;

/**
 * 服务端异常处理器
 *
 * @author yuanbo
 * @date 2022-09-13 17:06
 */
public class ServerExceptionHandler {
    private static final ServerExceptionHandler INSTANCE = new ServerExceptionHandler();

    private ServerExceptionHandler() {}

    /**
     * 处理
     *
     * @param request 远程调用请求
     * @param ex 异常
     * @return {@link RemotingResponse}
     */
    public RemotingResponse<DefaultResponse> handle(final IRemotingRequest request, final ServerException ex) {
        final String id = request.getId();
        final int code = request.getCode();
        final ResponseEnum responseType = ObjectUtils.defaultIfNull(ex.getResponseType(), ResponseEnum.UNKNOWN_ERROR);
        final int status = responseType.getCode();
        final DefaultResponse response = DefaultResponse.create(responseType.name());
        return RemotingResponse.create(id, code, status, response);
    }

    /**
     * 处理异常
     *
     * @param request 远程调用请求
     * @param ex 异常
     * @return {@link RemotingResponse}
     */
    public RemotingResponse<DefaultResponse> handle(final IRemotingRequest request, final Exception ex) {
        final String id = request.getId();
        final int code = request.getCode();
        final ResponseEnum responseType = ResponseEnum.UNKNOWN_ERROR;
        final int status = responseType.getCode();
        final DefaultResponse response = DefaultResponse.create(responseType.name());
        return RemotingResponse.create(id, code, status, response);
    }

    /**
     * 获取ServerExceptionHandler实例
     *
     * @return {@link ServerExceptionHandler}
     */
    public static ServerExceptionHandler getInstance() {
        return INSTANCE;
    }
}

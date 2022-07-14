package org.ybonfire.pipeline.common.command;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.ybonfire.pipeline.common.constant.RemotingCommandTypeEnum;
import org.ybonfire.pipeline.common.protocol.IRemotingCommandBody;

import java.io.Serializable;

/**
 * 远程调用请求、响应数据体
 *
 * @author Bo.Yuan5
 * @date 2022-05-18 10:16
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class RemotingCommand implements Serializable {
    private Integer commandType;
    private Integer code;
    private String commandId;
    private IRemotingCommandBody body;

    public boolean isSuccess() {
        return code != null && code >= 0;
    }

    /**
     * @description: 构造请求体
     * @param:
     * @return:
     * @date: 2022/05/23 17:58:37
     */
    public static RemotingCommand createRequestCommand(int code, final String requestId,
        final IRemotingCommandBody body) {
        return new RemotingCommand(RemotingCommandTypeEnum.REMOTING_COMMAND_REQUEST.getCode(), code, requestId, body);
    }

    /**
     * @description: 构造响应体
     * @param:
     * @return:
     * @date: 2022/05/23 17:58:39
     */
    public static RemotingCommand createResponseCommand(int code, final String responseId,
        final IRemotingCommandBody body) {
        return new RemotingCommand(RemotingCommandTypeEnum.REMOTING_COMMAND_RESPONSE.getCode(), code, responseId, body);
    }
}

package org.ybonfire.netty.common.command;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.ybonfire.netty.common.protocol.RemotingCommandConstant;

/**
 * 远程调用请求、响应数据体
 *
 * @author Bo.Yuan5
 * @date 2022-05-18 10:16
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class RemotingCommand {
    private Integer commandType;
    private Integer code;
    private String body;
    private String commandId;

    /**
     * @description: 构造请求体
     * @param:
     * @return:
     * @date: 2022/05/23 17:58:37
     */
    public static RemotingCommand createRequestCommand(int code, final String body, final String requestId) {
        return new RemotingCommand(RemotingCommandConstant.REMOTING_COMMAND_REQUEST, code, body, requestId);
    }

    /**
     * @description: 构造响应体
     * @param:
     * @return:
     * @date: 2022/05/23 17:58:39
     */
    public static RemotingCommand createResponseCommand(int code, final String body, final String responseId) {
        return new RemotingCommand(RemotingCommandConstant.REMOTING_COMMAND_RESPONSE, code, body, responseId);
    }
}

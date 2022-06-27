package org.ybonfire.pipeline.common.command;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.ybonfire.pipeline.common.protocol.RemotingCommandTypeConstant;

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
    private Object body;

    /**
     * @description: 构造请求体
     * @param:
     * @return:
     * @date: 2022/05/23 17:58:37
     */
    public static RemotingCommand createRequestCommand(int code, final String requestId, final Object body) {
        return new RemotingCommand(RemotingCommandTypeConstant.REMOTING_COMMAND_REQUEST, code, requestId, body);
    }

    /**
     * @description: 构造响应体
     * @param:
     * @return:
     * @date: 2022/05/23 17:58:39
     */
    public static RemotingCommand createResponseCommand(int code, final String responseId, final Object body) {
        return new RemotingCommand(RemotingCommandTypeConstant.REMOTING_COMMAND_RESPONSE, code, responseId, body);
    }
}

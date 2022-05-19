package org.ybonfire.netty.common.command;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 远程调用请求、响应数据体
 *
 * @author Bo.Yuan5
 * @date 2022-05-18 10:16
 */
@AllArgsConstructor
@Data
public class RemotingCommand<T> {
    private int code;
    private T body;
    private String requestId;

    public static <T> RemotingCommand<T> createResponseCommand(int code, final T body, final String requestId) {
        return new RemotingCommand(code, body, requestId);
    }
}

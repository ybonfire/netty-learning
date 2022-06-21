package org.ybonfire.netty.common.protocol;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * 远程命令类型枚举
 *
 * @author Bo.Yuan5
 * @date 2022-05-23 17:48
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class RemotingCommandTypeConstant {
    /**
     * 请求
     */
    public static final int REMOTING_COMMAND_REQUEST = 1;
    /**
     * 响应
     */
    public static final int REMOTING_COMMAND_RESPONSE = 2;
}

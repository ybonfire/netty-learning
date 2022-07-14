package org.ybonfire.pipeline.common.constant;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * 远程命令类型枚举
 *
 * @author Bo.Yuan5
 * @date 2022-05-23 17:48
 */
public enum RemotingCommandTypeEnum {
    /**
     * 请求
     */
    REMOTING_COMMAND_REQUEST(1),
    /**
     * 响应
     */
    REMOTING_COMMAND_RESPONSE(2);

    private int code;

    RemotingCommandTypeEnum(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public static RemotingCommandTypeEnum of(final int code) {
        for (final RemotingCommandTypeEnum type : RemotingCommandTypeEnum.values()) {
            if (type.code == code) {
                return type;
            }
        }

        return null;
    }
}

package org.ybonfire.netty.common.codec.serializer;

import org.ybonfire.netty.common.command.RemotingCommand;

import java.nio.ByteBuffer;

/**
 * 序列化器接口
 *
 * @author Bo.Yuan5
 * @date 2022-05-25 12:28
 */
public interface ISerializer {
    /**
     * @description: 序列化
     * @param:
     * @return:
     * @date: 2022/05/25 12:32:22
     */
    ByteBuffer encode(final RemotingCommand src);

    /**
     * @description: 反序列化
     * @param:
     * @return:
     * @date: 2022/05/25 12:32:27
     */
    RemotingCommand decode(final ByteBuffer src);
}

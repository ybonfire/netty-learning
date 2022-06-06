package org.ybonfire.netty.common.codec;

import java.nio.ByteBuffer;

import org.ybonfire.netty.common.codec.serializer.ISerializer;
import org.ybonfire.netty.common.codec.serializer.impl.DefaultSerializerImpl;
import org.ybonfire.netty.common.command.RemotingCommand;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * 序列化器
 *
 * @author Bo.Yuan5
 * @date 2022-05-18 12:35
 */
public class Encoder extends MessageToByteEncoder<RemotingCommand> {
    private final ISerializer serializer;

    public Encoder() {
        this.serializer = new DefaultSerializerImpl();
    }

    public Encoder(final ISerializer serializer) {
        this.serializer = serializer;
    }

    /**
     * @description: 序列化
     * @param:
     * @return:
     * @date: 2022/05/18 12:44:42
     */
    @Override
    protected void encode(final ChannelHandlerContext ctx, final RemotingCommand msg, final ByteBuf out)
        throws Exception {
        final ByteBuffer result = serializer.encode(msg);
        if (result != null) {
            out.writeBytes(result);
        }
    }
}

package org.ybonfire.netty.common.codec;

import org.ybonfire.netty.common.command.RemotingCommand;
import org.ybonfire.netty.common.util.CodecUtil;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * 序列化器
 *
 * @author Bo.Yuan5
 * @date 2022-05-18 12:35
 */
@ChannelHandler.Sharable
public class Encoder extends MessageToByteEncoder<RemotingCommand> {

    /**
     * @description: 序列化
     * @param:
     * @return:
     * @date: 2022/05/18 12:44:42
     */
    @Override
    protected void encode(final ChannelHandlerContext ctx, final RemotingCommand msg, final ByteBuf out)
        throws Exception {
        final byte[] result = CodecUtil.toBytes(msg);
        if (result != null) {
            out.writeBytes(result);
        }
    }
}

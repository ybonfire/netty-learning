package org.ybonfire.netty.common.codec;

import java.nio.ByteBuffer;
import java.util.List;

import org.ybonfire.netty.common.command.RemotingCommand;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import org.ybonfire.netty.common.util.CodecUtil;

/**
 * 反序列化器
 *
 * @author Bo.Yuan5
 * @date 2022-05-18 12:35
 */
public class Decoder extends ByteToMessageDecoder {

    /**
     * @description: 反序列化
     * @param:
     * @return:
     * @date: 2022/05/18 12:48:34
     */
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        final byte[] result;
        if (in.hasArray()) {
            result = in.array();
        } else {
            result = new byte[in.readableBytes()];
            in.getBytes(in.readerIndex(), result);
        }

        in.skipBytes(in.readableBytes());
        out.add(CodecUtil.fromBytes(result, RemotingCommand.class));
    }
}

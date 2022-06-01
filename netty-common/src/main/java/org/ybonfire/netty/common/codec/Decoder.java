package org.ybonfire.netty.common.codec;

import java.util.List;

import org.ybonfire.netty.common.codec.serializer.ISerializer;
import org.ybonfire.netty.common.codec.serializer.impl.DefaultSerializerImpl;
import org.ybonfire.netty.common.command.RemotingCommand;
import org.ybonfire.netty.common.logger.IInternalLogger;
import org.ybonfire.netty.common.logger.impl.SimpleInternalLogger;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

/**
 * 反序列化器
 *
 * @author Bo.Yuan5
 * @date 2022-05-18 12:35
 */
public class Decoder extends ByteToMessageDecoder {
    private static final int INT_BYTE_LENGTH = 4;
    private static final IInternalLogger logger = new SimpleInternalLogger();
    private final ISerializer serializer;

    public Decoder() {
        this.serializer = new DefaultSerializerImpl();
    }

    public Decoder(final ISerializer serializer) {
        this.serializer = serializer;
    }

    /**
     * @description: 反序列化
     * @param:
     * @return:
     * @date: 2022/05/18 12:48:34
     */
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        if (in.readableBytes() < INT_BYTE_LENGTH) {
            logger.warn("数据异常, 丢弃");
            return;
        }

        final int dataLength = in.readInt();
        if (in.readableBytes() < dataLength) {
            in.resetReaderIndex();
            logger.warn("数据异常, 丢弃");
            return;
        }

        final RemotingCommand result = serializer.decode(in.nioBuffer());
        if (result != null) {
            out.add(result);
        }
    }
}

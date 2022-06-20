package org.ybonfire.netty.common.codec;

import java.util.List;

import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
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
public class Decoder extends LengthFieldBasedFrameDecoder {
    private static final int INT_BYTE_LENGTH = 4;
    private static final IInternalLogger logger = new SimpleInternalLogger();
    private final ISerializer serializer;

    public Decoder() {
        super(65536, 0, INT_BYTE_LENGTH, 0, 0);
        this.serializer = new DefaultSerializerImpl();
    }

    public Decoder(final ISerializer serializer) {
        super(65536, 0, INT_BYTE_LENGTH, 0, 0);
        this.serializer = serializer;
    }

    /**
     * @description: 反序列化
     * @param:
     * @return:
     * @date: 2022/05/18 12:48:34
     */
    @Override
    protected Object decode(ChannelHandlerContext ctx, ByteBuf in) throws Exception {
        ByteBuf frame = null;
        try {
            frame = (ByteBuf)super.decode(ctx, in);
            if (frame == null) {
                return null;
            }

            if (frame.readableBytes() < INT_BYTE_LENGTH) {
                logger.warn("数据异常, 丢弃");
                return null;
            }

            final int dataLength = frame.readInt();
            if (frame.readableBytes() < dataLength) {
                in.resetReaderIndex();
                logger.warn("数据异常, 丢弃");
                return null;
            }

            final RemotingCommand result = serializer.decode(frame.nioBuffer());
            return result;
        } finally {
            if (frame != null) {
                frame.release();
            }
        }
    }
}

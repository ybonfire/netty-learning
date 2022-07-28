package org.ybonfire.pipeline.common.codec.request;

import org.ybonfire.pipeline.common.codec.request.serializer.IRequestSerializer;
import org.ybonfire.pipeline.common.codec.request.serializer.impl.DefaultRequestSerializerImpl;
import org.ybonfire.pipeline.common.logger.IInternalLogger;
import org.ybonfire.pipeline.common.logger.impl.SimpleInternalLogger;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

/**
 * 反序列化器
 *
 * @author Bo.Yuan5
 * @date 2022-05-18 12:35
 */
public class RequestDecoder extends LengthFieldBasedFrameDecoder {
    private static final int INT_BYTE_LENGTH = 4;
    private static final IInternalLogger logger = new SimpleInternalLogger();
    private final IRequestSerializer serializer;

    public RequestDecoder() {
        super(65536, 0, INT_BYTE_LENGTH, 0, 0);
        this.serializer = new DefaultRequestSerializerImpl();
    }

    public RequestDecoder(final IRequestSerializer serializer) {
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

            return serializer.decode(frame.nioBuffer());
        } finally {
            if (frame != null) {
                frame.release();
            }
        }
    }
}

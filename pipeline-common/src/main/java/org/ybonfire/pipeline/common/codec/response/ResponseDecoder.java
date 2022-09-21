package org.ybonfire.pipeline.common.codec.response;

import org.ybonfire.pipeline.common.codec.response.serializer.IResponseSerializer;
import org.ybonfire.pipeline.common.codec.response.serializer.impl.DefaultResponseSerializerImpl;
import org.ybonfire.pipeline.common.logger.IInternalLogger;
import org.ybonfire.pipeline.common.logger.impl.SimpleInternalLogger;
import org.ybonfire.pipeline.common.util.RemotingUtil;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

/**
 * 响应反序列化器
 *
 * @author Bo.Yuan5
 * @date 2022-07-29 14:35
 */
public class ResponseDecoder extends LengthFieldBasedFrameDecoder {
    private static final int INT_BYTE_LENGTH = 4;
    private static final IInternalLogger LOGGER = new SimpleInternalLogger();
    private final IResponseSerializer serializer;

    public ResponseDecoder() {
        super(65536, 0, INT_BYTE_LENGTH, 0, 0);
        this.serializer = new DefaultResponseSerializerImpl();
    }

    public ResponseDecoder(final IResponseSerializer serializer) {
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
    protected Object decode(final ChannelHandlerContext ctx, final ByteBuf in) throws Exception {
        ByteBuf frame = null;
        try {
            frame = (ByteBuf)super.decode(ctx, in);
            if (frame == null) {
                return null;
            }

            if (frame.readableBytes() < INT_BYTE_LENGTH) {
                LOGGER.warn("数据异常, 丢弃");
                return null;
            }

            final int totalLength = frame.readInt();
            if (frame.readableBytes() < totalLength) {
                in.resetReaderIndex();
                LOGGER.warn("数据异常, 丢弃");
                return null;
            }

            return serializer.decode(frame.nioBuffer());
        } catch (Exception ex) {
            RemotingUtil.closeChannel(ctx.channel());
            return null;
        } finally {
            if (frame != null) {
                frame.release();
            }
        }
    }
}

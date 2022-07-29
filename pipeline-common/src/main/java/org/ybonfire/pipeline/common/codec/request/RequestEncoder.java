package org.ybonfire.pipeline.common.codec.request;

import java.nio.ByteBuffer;

import org.ybonfire.pipeline.common.codec.request.serializer.IRequestSerializer;
import org.ybonfire.pipeline.common.codec.request.serializer.impl.DefaultRequestSerializerImpl;
import org.ybonfire.pipeline.common.protocol.RemotingRequest;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * 请求序列化器
 *
 * @author Bo.Yuan5
 * @date 2022-05-18 12:35
 */
public class RequestEncoder extends MessageToByteEncoder<RemotingRequest> {
    private final IRequestSerializer serializer;

    public RequestEncoder() {
        this.serializer = new DefaultRequestSerializerImpl();
    }

    public RequestEncoder(final IRequestSerializer serializer) {
        this.serializer = serializer;
    }

    /**
     * @description: 序列化
     * @param:
     * @return:
     * @date: 2022/05/18 12:44:42
     */
    @Override
    protected void encode(final ChannelHandlerContext ctx, final RemotingRequest msg, final ByteBuf out)
        throws Exception {
        final ByteBuffer result = serializer.encode(msg);
        if (result != null) {
            out.writeBytes(result);
        }
    }
}

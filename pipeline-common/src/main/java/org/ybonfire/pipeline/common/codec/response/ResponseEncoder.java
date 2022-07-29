package org.ybonfire.pipeline.common.codec.response;

import java.nio.ByteBuffer;

import org.ybonfire.pipeline.common.codec.response.serializer.IResponseSerializer;
import org.ybonfire.pipeline.common.codec.response.serializer.impl.DefaultResponseSerializerImpl;
import org.ybonfire.pipeline.common.protocol.RemotingResponse;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * 响应序列化器
 *
 * @author Bo.Yuan5
 * @date 2022-07-29 14:35
 */
public class ResponseEncoder extends MessageToByteEncoder<RemotingResponse> {
    private final IResponseSerializer serializer;

    public ResponseEncoder() {
        this.serializer = new DefaultResponseSerializerImpl();
    }

    public ResponseEncoder(final IResponseSerializer serializer) {
        this.serializer = serializer;
    }

    /**
     * @description: 序列化
     * @param:
     * @return:
     * @date: 2022/05/18 12:44:42
     */
    @Override
    protected void encode(final ChannelHandlerContext ctx, final RemotingResponse msg, ByteBuf out) throws Exception {
        final ByteBuffer result = serializer.encode(msg);
        if (result != null) {
            out.writeBytes(result);
        }
    }
}

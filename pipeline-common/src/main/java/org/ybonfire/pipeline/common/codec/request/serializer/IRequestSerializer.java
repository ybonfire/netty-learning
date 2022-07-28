package org.ybonfire.pipeline.common.codec.request.serializer;

import java.nio.ByteBuffer;

import org.ybonfire.pipeline.common.protocol.RemotingRequest;
import org.ybonfire.pipeline.common.protocol.RemotingResponse;

/**
 * 远程调用请求序列化器接口
 *
 * @author Bo.Yuan5
 * @date 2022-05-25 12:28
 */
public interface IRequestSerializer {
    /**
     * @description: 序列化
     * @param:
     * @return:
     * @date: 2022/05/25 12:32:22
     */
    ByteBuffer encode(final RemotingRequest src);

    /**
     * @description: 反序列化
     * @param:
     * @return:
     * @date: 2022/05/25 12:32:27
     */
    RemotingRequest decode(final ByteBuffer src);
}

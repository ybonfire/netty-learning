package org.ybonfire.pipeline.common.codec.request.serializer.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.ybonfire.pipeline.common.codec.request.serializer.IRequestSerializer;
import org.ybonfire.pipeline.common.constant.CommonConstant;
import org.ybonfire.pipeline.common.constant.RequestEnum;
import org.ybonfire.pipeline.common.logger.IInternalLogger;
import org.ybonfire.pipeline.common.logger.impl.SimpleInternalLogger;
import org.ybonfire.pipeline.common.protocol.IRemotingRequestBody;
import org.ybonfire.pipeline.common.protocol.RemotingRequest;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Objects;
import java.util.Optional;

/**
 * 默认序列化器实现
 *
 * @author Bo.Yuan5
 * @date 2022-06-01 16:42
 */
public class DefaultRequestSerializerImpl implements IRequestSerializer {
    private static final IInternalLogger LOGGER = new SimpleInternalLogger();
    private static final ObjectMapper MAPPER = new ObjectMapper().disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);

    /**
     * @description: 序列化
     * @param:
     * @return:
     * @date: 2022/06/01 16:43:43
     */
    @Override
    public ByteBuffer encode(final RemotingRequest src) throws JsonProcessingException {
        if (src == null) {
            return null;
        }

        // id
        final byte[] idBytes = src.getId().getBytes(CommonConstant.CHARSET_UTF8);
        final int idByteLength = idBytes.length;
        // code
        final Integer code = src.getCode();
        // data
        final byte[] bodyBytes = Objects.isNull(src.getBody()) ? new byte[0] : MAPPER.writeValueAsBytes(src.getBody());
        final int bodyBytesLength = bodyBytes.length;

        final int totalLength = CommonConstant.INT_BYTE_LENGTH/*code*/ + CommonConstant.INT_BYTE_LENGTH/*idByteLength*/
            + CommonConstant.INT_BYTE_LENGTH/*bodyBytesLength*/
            + idByteLength + bodyBytesLength;

        final ByteBuffer result = ByteBuffer.allocate(CommonConstant.INT_BYTE_LENGTH + totalLength);
        result.putInt(totalLength); // totalLength
        result.putInt(code); // code
        result.putInt(idByteLength); // id
        result.put(idBytes);
        result.putInt(bodyBytesLength); // body
        result.put(bodyBytes);

        result.flip();
        return result;
    }

    /**
     * @description: 反序列化
     * @param:
     * @return:
     * @date: 2022/06/01 16:43:49
     */
    @Override
    public RemotingRequest decode(final ByteBuffer src) throws IOException {
        if (src == null) {
            return null;
        }

        // code
        final int code = src.getInt();
        final RequestEnum request = RequestEnum.code(code);
        if (request == null) {
            LOGGER.error("反序列化失败. 异常的RemotingRequestCode: [" + code + "]");
            throw new IllegalArgumentException();
        }

        // id
        final int idLength = src.getInt();
        final byte[] idBytes = new byte[idLength];
        src.get(idBytes);
        final String id = new String(idBytes, CommonConstant.CHARSET_UTF8);

        // body
        IRemotingRequestBody data = null;
        final int bodyBytesLength = src.getInt();
        if (bodyBytesLength != 0) {
            final byte[] bodyBytes = new byte[bodyBytesLength];
            src.get(bodyBytes);

            final Optional<Class<? extends IRemotingRequestBody>> classOptional = request.getRequestClazz();
            if (classOptional.isPresent()) {
                data = MAPPER.readValue(bodyBytes, classOptional.get());
            }
        }

        return RemotingRequest.create(id, code, data);
    }
}

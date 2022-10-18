package org.ybonfire.pipeline.common.codec.response.serializer.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.ybonfire.pipeline.common.codec.response.serializer.IResponseSerializer;
import org.ybonfire.pipeline.common.constant.CommonConstant;
import org.ybonfire.pipeline.common.constant.RequestEnum;
import org.ybonfire.pipeline.common.constant.ResponseEnum;
import org.ybonfire.pipeline.common.logger.IInternalLogger;
import org.ybonfire.pipeline.common.logger.impl.SimpleInternalLogger;
import org.ybonfire.pipeline.common.protocol.IRemotingResponseBody;
import org.ybonfire.pipeline.common.protocol.RemotingResponse;

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
public class DefaultResponseSerializerImpl implements IResponseSerializer {
    private static final IInternalLogger LOGGER = new SimpleInternalLogger();
    private static final ObjectMapper MAPPER = new ObjectMapper().disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);

    /**
     * @description: 序列化
     * @param:
     * @return:
     * @date: 2022/06/01 16:43:43
     */
    @Override
    public ByteBuffer encode(final RemotingResponse src) throws JsonProcessingException {
        if (src == null) {
            return null;
        }

        // code
        final Integer code = src.getCode();

        // id
        final byte[] idBytes = src.getId().getBytes(CommonConstant.CHARSET_UTF8);
        final int idByteLength = idBytes.length;

        // status
        final Integer status = src.getStatus();

        // data
        final byte[] bodyBytes = Objects.isNull(src.getBody()) ? new byte[0] : MAPPER.writeValueAsBytes(src.getBody());
        final int bodyBytesLength = bodyBytes.length;

        // total
        final int totalLength = CommonConstant.INT_BYTE_LENGTH/*code*/ + CommonConstant.INT_BYTE_LENGTH/*idByteLength*/
            + idByteLength + CommonConstant.INT_BYTE_LENGTH/*status*/
            + CommonConstant.INT_BYTE_LENGTH/*bodyBytesLength*/
            + bodyBytesLength;

        final ByteBuffer result = ByteBuffer.allocate(CommonConstant.INT_BYTE_LENGTH + totalLength);
        result.putInt(totalLength); // totalLength
        result.putInt(code); // code
        result.putInt(idByteLength); // id
        result.put(idBytes);
        result.putInt(status); // status
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
    public RemotingResponse decode(final ByteBuffer src) throws IOException {
        if (src == null) {
            return null;
        }

        // code
        final int code = src.getInt();
        final RequestEnum request = RequestEnum.code(code);
        if (request == null) {
            LOGGER.error("反序列化失败. 异常的RemotingResponseCode: [" + code + "]");
            throw new IllegalArgumentException();
        }

        // id
        final int idLength = src.getInt();
        final byte[] idBytes = new byte[idLength];
        src.get(idBytes);
        final String id = new String(idBytes, CommonConstant.CHARSET_UTF8);

        // status
        final int statusCode = src.getInt();

        // body
        IRemotingResponseBody data = null;
        final int bodyBytesLength = src.getInt();
        if (bodyBytesLength != 0) {
            final byte[] bodyBytes = new byte[bodyBytesLength];
            src.get(bodyBytes);

            final ResponseEnum response = ResponseEnum.of(statusCode);
            if (response == null) {
                LOGGER.error("反序列化失败. 异常的RemotingResponseStatus: [" + response + "]");
                throw new IllegalArgumentException();
            }

            final Optional<Class<? extends IRemotingResponseBody>> classOptional = (response == ResponseEnum.SUCCESS)
                ? request.getResponseClazz() : Optional.ofNullable(response.getClazz());

            if (classOptional.isPresent()) {
                data = MAPPER.readValue(bodyBytes, classOptional.get());
            }
        }

        return RemotingResponse.create(id, code, statusCode, data);
    }
}

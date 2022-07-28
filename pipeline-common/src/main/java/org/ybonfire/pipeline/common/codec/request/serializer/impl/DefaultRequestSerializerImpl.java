package org.ybonfire.pipeline.common.codec.request.serializer.impl;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.Optional;

import org.ybonfire.pipeline.common.codec.request.serializer.IRequestSerializer;
import org.ybonfire.pipeline.common.constant.RequestEnum;
import org.ybonfire.pipeline.common.exception.ExceptionTypeEnum;
import org.ybonfire.pipeline.common.logger.IInternalLogger;
import org.ybonfire.pipeline.common.logger.impl.SimpleInternalLogger;
import org.ybonfire.pipeline.common.protocol.IRemotingRequestBody;
import org.ybonfire.pipeline.common.protocol.RemotingRequest;
import org.ybonfire.pipeline.common.util.ExceptionUtil;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * 默认序列化器实现
 *
 * @author Bo.Yuan5
 * @date 2022-06-01 16:42
 */
public class DefaultRequestSerializerImpl implements IRequestSerializer {
    private static final int INT_BYTE_LENGTH = 4;
    private static final IInternalLogger LOGGER = new SimpleInternalLogger();
    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final Charset CHARSET_UTF8 = StandardCharsets.UTF_8;

    /**
     * @description: 序列化
     * @param:
     * @return:
     * @date: 2022/06/01 16:43:43
     */
    @Override
    public ByteBuffer encode(final RemotingRequest src) {
        if (src == null) {
            return null;
        }

        try {
            // id
            final byte[] idBytes = src.getId().getBytes(CHARSET_UTF8);
            final int idByteLength = idBytes.length;
            // code
            final Integer code = src.getCode();
            // data
            final byte[] bodyBytes = Objects.isNull(src.getBody()) ? null : MAPPER.writeValueAsBytes(src.getBody());
            final int bodyBytesLength = Objects.isNull(bodyBytes) ? 0 : bodyBytes.length;

            final int totalLength =
                INT_BYTE_LENGTH/*code*/ + INT_BYTE_LENGTH/*idByteLength*/ + INT_BYTE_LENGTH/*bodyBytesLength*/
                    + idByteLength + bodyBytesLength;

            final ByteBuffer result = ByteBuffer.allocate(INT_BYTE_LENGTH + totalLength);
            result.putInt(totalLength); // totalLength
            result.putInt(code); // code
            result.putInt(idByteLength); // id
            result.put(idBytes);
            result.putInt(bodyBytesLength); // body
            result.put(bodyBytes);

            result.flip();
            return result;
        } catch (JsonProcessingException e) {
            LOGGER.error("序列化失败", e);
            throw ExceptionUtil.exception(ExceptionTypeEnum.SERIALIZE_FAILED);
        }
    }

    /**
     * @description: 反序列化
     * @param:
     * @return:
     * @date: 2022/06/01 16:43:49
     */
    @Override
    public RemotingRequest decode(final ByteBuffer src) {
        if (src == null) {
            return null;
        }

        try {
            // id
            final int idLength = src.getInt();
            final byte[] idBytes = new byte[idLength];
            src.get(idBytes);
            final String id = new String(idBytes, CHARSET_UTF8);

            // code
            final int code = src.getInt();
            final RequestEnum request = RequestEnum.code(code);
            if (request == null) {
                LOGGER.error("反序列化失败. 异常的RemotingRequestCode: [" + code + "]");
                throw ExceptionUtil.exception(ExceptionTypeEnum.ILLEGAL_ARGUMENT);
            }

            // body
            IRemotingRequestBody data = null;
            final int bodyBytesLength = src.getInt();
            if (bodyBytesLength != 0) {
                final byte[] bodyBytes = new byte[bodyBytesLength];
                src.get(bodyBytes);

                final Optional<Class<? extends IRemotingRequestBody>> classOptional = request.getClazz();
                if (classOptional.isPresent()) {
                    data = MAPPER.readValue(bodyBytes, classOptional.get());
                }
            }

            return RemotingRequest.create(id, code, data);
        } catch (IOException ex) {
            LOGGER.error("反序列化失败.", ex);
            throw ExceptionUtil.exception(ExceptionTypeEnum.SERIALIZE_FAILED);
        }
    }
}

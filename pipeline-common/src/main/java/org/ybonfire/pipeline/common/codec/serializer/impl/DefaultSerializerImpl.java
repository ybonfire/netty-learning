package org.ybonfire.pipeline.common.codec.serializer.impl;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

import org.ybonfire.pipeline.common.codec.serializer.ISerializer;
import org.ybonfire.pipeline.common.command.RemotingCommand;
import org.ybonfire.pipeline.common.constant.RemotingCommandTypeEnum;
import org.ybonfire.pipeline.common.constant.RequestEnum;
import org.ybonfire.pipeline.common.constant.ResponseEnum;
import org.ybonfire.pipeline.common.exception.ExceptionTypeEnum;
import org.ybonfire.pipeline.common.logger.IInternalLogger;
import org.ybonfire.pipeline.common.logger.impl.SimpleInternalLogger;
import org.ybonfire.pipeline.common.protocol.IRemotingCommandBody;
import org.ybonfire.pipeline.common.protocol.IRemotingRequestBody;
import org.ybonfire.pipeline.common.protocol.IRemotingResponseBody;
import org.ybonfire.pipeline.common.util.ExceptionUtil;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * 默认序列化器实现
 *
 * @author Bo.Yuan5
 * @date 2022-06-01 16:42
 */
public class DefaultSerializerImpl implements ISerializer {
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
    public ByteBuffer encode(final RemotingCommand src) {
        if (src == null) {
            return null;
        }

        try {
            final int commandType = src.getCommandType(); // commandType
            final int code = src.getCode(); // commandCode
            final byte[] commandIdBytes = src.getCommandId().getBytes(CHARSET_UTF8); // commandId
            final int commandIdByteLength = commandIdBytes.length; // commandId Length
            final byte[] commandBodyBytes = MAPPER.writeValueAsBytes(src.getBody()); // command body
            final int commandBodyBytesLength = commandBodyBytes.length; // command body Length
            final int totalLength =
                INT_BYTE_LENGTH/*commandType*/ + INT_BYTE_LENGTH/*commandCode*/ + INT_BYTE_LENGTH/*commandId Length*/
                    + commandIdByteLength + +INT_BYTE_LENGTH/*commandBodyBytesLength*/ + commandBodyBytesLength;

            final ByteBuffer result = ByteBuffer.allocate(INT_BYTE_LENGTH + totalLength);
            result.putInt(totalLength); // totalLength
            result.putInt(commandType); // commandType
            result.putInt(code); // commandCode
            result.putInt(commandIdByteLength); // commandId Length
            result.put(commandIdBytes); // commandId
            result.putInt(commandBodyBytesLength); // commandBody Length
            result.put(commandBodyBytes); // commandBody

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
    public RemotingCommand decode(final ByteBuffer src) {
        if (src == null) {
            return null;
        }

        // commandType
        final int commandType = src.getInt();
        try {
            if (commandType == RemotingCommandTypeEnum.REMOTING_COMMAND_REQUEST.getCode()) {
                return doRemotingRequestDecode(src);
            } else if (commandType == RemotingCommandTypeEnum.REMOTING_COMMAND_RESPONSE.getCode()) {
                return doRemotingResponseDecode(src);
            } else {
                LOGGER.error("反序列化失败. 异常的RemotingCommand类型: [" + commandType + "]");
                throw ExceptionUtil.exception(ExceptionTypeEnum.ILLEGAL_ARGUMENT);
            }
        } catch (IOException ex) {
            throw ExceptionUtil.exception(ExceptionTypeEnum.SERIALIZE_FAILED, ex);
        }
    }

    /**
     * @description: 远程调用请求反序列化
     * @param:
     * @return:
     * @date: 2022/07/13 15:56:53
     */
    private RemotingCommand doRemotingRequestDecode(final ByteBuffer src) throws IOException {
        if (src == null) {
            return null;
        }

        // code
        final int code = src.getInt();
        // commandId
        final int commandIdLength = src.getInt();
        final byte[] commandIdBytes = new byte[commandIdLength];
        src.get(commandIdBytes);
        final String commandId = new String(commandIdBytes, CHARSET_UTF8);
        // body
        IRemotingCommandBody body = null;
        final int commandBodyLength = src.getInt();
        if (commandBodyLength != 0) {
            final byte[] commandBodyBytes = new byte[commandBodyLength];
            src.get(commandBodyBytes);

            final RequestEnum request = RequestEnum.code(code);
            if (request == null) {
                LOGGER.error("反序列化失败. 异常的RemotingRequestCode: [" + code + "]");
                throw ExceptionUtil.exception(ExceptionTypeEnum.ILLEGAL_ARGUMENT);
            }

            final Optional<Class<? extends IRemotingRequestBody>> classOptional = request.getClazz();
            if (classOptional.isPresent()) {
                body = MAPPER.readValue(commandBodyBytes, classOptional.get());
            }
        }

        return RemotingCommand.createRequestCommand(code, commandId, body);
    }

    /**
     * @description: 远程调用响应反序列化
     * @param:
     * @return:
     * @date: 2022/07/13 15:57:01
     */
    private RemotingCommand doRemotingResponseDecode(final ByteBuffer src) throws IOException {
        if (src == null) {
            return null;
        }

        // code
        final int code = src.getInt();
        // commandId
        final int commandIdLength = src.getInt();
        final byte[] commandIdBytes = new byte[commandIdLength];
        src.get(commandIdBytes);
        final String commandId = new String(commandIdBytes, CHARSET_UTF8);
        // body
        IRemotingCommandBody body = null;
        final int commandBodyLength = src.getInt();
        if (commandBodyLength != 0) {
            final byte[] commandBodyBytes = new byte[commandBodyLength];
            src.get(commandBodyBytes);

            final ResponseEnum response = ResponseEnum.code(code);
            if (response == null) {
                LOGGER.error("反序列化失败. 异常的RemotingResponseCode: [" + code + "]");
                throw ExceptionUtil.exception(ExceptionTypeEnum.ILLEGAL_ARGUMENT);
            }

            final Optional<Class<? extends IRemotingResponseBody>> classOptional = response.getClazz();
            if (classOptional.isPresent()) {
                body = MAPPER.readValue(commandBodyBytes, classOptional.get());
            }
        }

        return RemotingCommand.createRequestCommand(code, commandId, body);
    }
}

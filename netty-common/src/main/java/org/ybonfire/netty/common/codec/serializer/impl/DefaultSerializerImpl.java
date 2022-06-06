package org.ybonfire.netty.common.codec.serializer.impl;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;

import org.ybonfire.netty.common.codec.serializer.ISerializer;
import org.ybonfire.netty.common.command.RemotingCommand;
import org.ybonfire.netty.common.exception.ExceptionTypeEnum;
import org.ybonfire.netty.common.logger.IInternalLogger;
import org.ybonfire.netty.common.logger.impl.SimpleInternalLogger;
import org.ybonfire.netty.common.protocol.RemotingCommandTypeConstant;
import org.ybonfire.netty.common.protocol.RequestCommandEnum;
import org.ybonfire.netty.common.util.ExceptionUtil;

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
    private static final Charset CHARSET_UTF8 = Charset.forName("UTF-8");

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
            final byte[] commandBodyBytes = MAPPER.writeValueAsBytes(src.getBody()); // commandbody
            final int commandBodyBytesLength = commandBodyBytes.length; // commandbody Length
            final int totalLength = INT_BYTE_LENGTH + INT_BYTE_LENGTH + INT_BYTE_LENGTH + commandIdByteLength
                + INT_BYTE_LENGTH + commandBodyBytesLength; // totalLength

            final ByteBuffer result = ByteBuffer.allocate(INT_BYTE_LENGTH + totalLength);
            result.putInt(totalLength); // totalLength
            result.putInt(commandType); // commandType
            result.putInt(code); // commandCode
            result.putInt(commandIdByteLength); // commandIdByteLength
            result.put(commandIdBytes); // commandId
            result.putInt(commandBodyBytesLength); // commandBodyBytesLength
            result.put(commandBodyBytes); // commandBody

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

        try {
            // commandType
            final int commandType = src.getInt();
            // code
            final int code = src.getInt();
            // commandId
            final int commandIdLength = src.getInt();
            final byte[] commandIdBytes = new byte[commandIdLength];
            src.get(commandIdBytes);
            final String commandId = new String(commandIdBytes, CHARSET_UTF8);
            // body
            final int commandBodyLength = src.getInt();
            final byte[] commandBodyBytes = new byte[commandBodyLength];
            src.get(commandBodyBytes);
            final RequestCommandEnum commandEnum = RequestCommandEnum.of(code);
            final Object body =
                commandEnum == null ? null : MAPPER.readValue(commandBodyBytes, commandEnum.getRequestClazz());

            if (commandType == RemotingCommandTypeConstant.REMOTING_COMMAND_REQUEST) {
                return RemotingCommand.createRequestCommand(code, commandId, body);
            } else if (commandType == RemotingCommandTypeConstant.REMOTING_COMMAND_RESPONSE) {
                return RemotingCommand.createResponseCommand(code, commandId, body);
            } else {
                throw ExceptionUtil.exception(ExceptionTypeEnum.ILLEGAL_ARGUMENT);
            }
        } catch (IOException e) {
            throw ExceptionUtil.exception(ExceptionTypeEnum.SERIALIZE_FAILED);
        }
    }
}

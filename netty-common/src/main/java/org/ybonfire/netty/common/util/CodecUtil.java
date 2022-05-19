package org.ybonfire.netty.common.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

/**
 * JSON工具类
 *
 * @author Bo.Yuan5
 * @date 2022-05-18 16:52
 */
@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class CodecUtil {
    private static final ObjectMapper MAPPER = new ObjectMapper();

    public static byte[] toBytes(final Object object) {
        try {
            return MAPPER.writeValueAsBytes(object);
        } catch (JsonProcessingException e) {
            log.error("序列化异常", e);
            return null;
        }
    }

    public static <T> T fromBytes(final byte[] bytes, final Class<T> clazz) {
        try {
            return MAPPER.readValue(bytes, clazz);
        } catch (IOException e) {
            log.error("反序列化异常", e);
            return null;
        }
    }
}

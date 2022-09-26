package org.ybonfire.pipeline.common.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * JSON序列化/反序列化工具类
 *
 * @author yuanbo
 * @date 2022-09-22 10:12
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class JsonUtil {
    private static final ObjectMapper MAPPER = new ObjectMapper();

    /**
     * 编码
     *
     * @param obj obj
     * @return {@link String}
     * @throws JsonProcessingException json处理异常
     */
    public static String encode(final Object obj) throws JsonProcessingException {
        return MAPPER.writeValueAsString(obj);
    }

    /**
     * 解码
     *
     * @param json json
     * @param clazz clazz
     * @return {@link T}
     * @throws JsonProcessingException json处理异常
     */
    public static <T> T decode(final String json, final Class<T> clazz) throws JsonProcessingException {
        return MAPPER.readValue(json, clazz);
    }
}

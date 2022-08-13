package org.ybonfire.pipeline.producer.converter.provider;


import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.ybonfire.pipeline.producer.converter.NodeConverter;

/**
 * NodeConverterProvider
 *
 * @author Bo.Yuan5
 * @date 2022-07-29 10:01
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class NodeConverterProvider {
    private static final NodeConverter INSTANCE = new NodeConverter();

    /**
     * @description: 获取NodeConverter实例
     * @param:
     * @return:
     * @date: 2022/08/12 21:56:10
     */
    public static NodeConverter getInstance() {
        return INSTANCE;
    }
}

package org.ybonfire.pipeline.nameserver.converter.provider;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.ybonfire.pipeline.nameserver.converter.NodeConverter;

/**
 * NodeConverterProvider
 *
 * @author Bo.Yuan5
 * @date 2022-07-29 10:01
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class NodeConverterProvider {
    private static final NodeConverter INSTANCE = new NodeConverter();

    public static NodeConverter getInstance() {
        return INSTANCE;
    }
}

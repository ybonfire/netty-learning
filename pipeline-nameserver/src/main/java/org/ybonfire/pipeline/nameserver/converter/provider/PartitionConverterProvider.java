package org.ybonfire.pipeline.nameserver.converter.provider;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.ybonfire.pipeline.nameserver.converter.PartitionConverter;

/**
 * PartitionConverterProvider
 *
 * @author Bo.Yuan5
 * @date 2022-07-29 10:00
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class PartitionConverterProvider {
    private static final PartitionConverter INSTANCE = new PartitionConverter(NodeConverterProvider.getInstance());

    public static PartitionConverter getInstance() {
        return INSTANCE;
    }
}

package org.ybonfire.pipeline.producer.converter.provider;

import org.ybonfire.pipeline.producer.converter.PartitionConverter;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

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

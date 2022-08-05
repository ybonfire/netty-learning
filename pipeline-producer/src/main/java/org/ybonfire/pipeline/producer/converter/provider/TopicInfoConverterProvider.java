package org.ybonfire.pipeline.producer.converter.provider;

import org.ybonfire.pipeline.producer.converter.TopicInfoConverter;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * TopicInfoConverterProvider
 *
 * @author Bo.Yuan5
 * @date 2022-07-29 10:00
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class TopicInfoConverterProvider {
    private static final TopicInfoConverter INSTANCE = new TopicInfoConverter(PartitionConverterProvider.getInstance());

    public static TopicInfoConverter getInstance() {
        return INSTANCE;
    }
}

package org.ybonfire.pipeline.broker.converter;

import org.ybonfire.pipeline.broker.model.topic.TopicConfig;
import org.ybonfire.pipeline.common.converter.IConverter;
import org.ybonfire.pipeline.common.model.TopicConfigRemotingEntity;

/**
 * TopicConfig类型转换器
 *
 * @author yuanbo
 * @date 2022-09-23 11:25
 */
public class TopicConfigConverter implements IConverter<TopicConfig, TopicConfigRemotingEntity> {
    private static final TopicConfigConverter INSTANCE = new TopicConfigConverter();

    public static TopicConfigConverter getInstance() {
        return INSTANCE;
    }

    private TopicConfigConverter() {}

    /**
     * @description: TopicConfig -> TopicConfigRemotingEntity
     * @param:
     * @return:
     * @date: 2022/09/23 11:25:48
     */
    @Override
    public TopicConfigRemotingEntity convert(final TopicConfig src) {
        if (src == null) {
            return null;
        }

        return null;
    }
}

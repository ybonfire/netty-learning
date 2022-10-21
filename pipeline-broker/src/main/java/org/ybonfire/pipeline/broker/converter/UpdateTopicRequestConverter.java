package org.ybonfire.pipeline.broker.converter;

import org.ybonfire.pipeline.broker.model.topic.PartitionConfig;
import org.ybonfire.pipeline.broker.model.topic.TopicConfig;
import org.ybonfire.pipeline.common.converter.IConverter;
import org.ybonfire.pipeline.common.protocol.request.broker.UpdateTopicRequest;

import java.util.ArrayList;

/**
 * UpdateTopicRequestConverter
 *
 * @author yuanbo
 * @date 2022-10-14 17:56
 */
public final class UpdateTopicRequestConverter implements IConverter<UpdateTopicRequest, TopicConfig> {
    private static final UpdateTopicRequestConverter INSTANCE = new UpdateTopicRequestConverter();

    public static UpdateTopicRequestConverter getInstance() {
        return INSTANCE;
    }

    private UpdateTopicRequestConverter() {}

    /**
     * @description: UpdateTopicRequest -> TopicConfig
     * @param:
     * @return:
     * @date: 2022/09/23 10:25:03
     */
    @Override
    public TopicConfig convert(final UpdateTopicRequest request) {
        if (request == null) {
            return null;
        }

        final TopicConfig topicConfig = new TopicConfig();
        topicConfig.setTopic(request.getTopic());
        topicConfig.setPartitions(new ArrayList<>(request.getPartitionNums()));
        for (int i = 0; i < request.getPartitionNums(); ++i) {
            topicConfig.getPartitions().add(new PartitionConfig(i));
        }

        return topicConfig;
    }
}

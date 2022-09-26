package org.ybonfire.pipeline.broker.converter;

import java.util.ArrayList;

import org.ybonfire.pipeline.broker.model.PartitionConfig;
import org.ybonfire.pipeline.broker.model.TopicConfig;
import org.ybonfire.pipeline.common.converter.IConverter;
import org.ybonfire.pipeline.common.protocol.request.CreateTopicRequest;

/**
 * CreateTopicRequest转换器
 *
 * @author yuanbo
 * @date 2022-09-23 10:20
 */
public final class CreateTopicRequestConverter implements IConverter<CreateTopicRequest, TopicConfig> {
    private static final CreateTopicRequestConverter INSTANCE = new CreateTopicRequestConverter();

    public static CreateTopicRequestConverter getInstance() {
        return INSTANCE;
    }

    private CreateTopicRequestConverter() {}

    /**
     * @description: CreateTopicRequest -> TopicConfig
     * @param:
     * @return:
     * @date: 2022/09/23 10:25:03
     */
    @Override
    public TopicConfig convert(final CreateTopicRequest request) {
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

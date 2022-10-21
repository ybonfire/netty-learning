package org.ybonfire.pipeline.broker.converter;

import org.ybonfire.pipeline.broker.model.message.SelectMessageResult;
import org.ybonfire.pipeline.common.converter.IConverter;
import org.ybonfire.pipeline.common.model.Message;
import org.ybonfire.pipeline.common.protocol.response.broker.PullMessageResponse;

import java.util.List;

/**
 * SelectMessageResultConverter
 *
 * @author yuanbo
 * @date 2022-10-20 23:47
 */
public final class SelectMessageResultConverter implements IConverter<SelectMessageResult, PullMessageResponse> {
    private static final SelectMessageResultConverter INSTANCE = new SelectMessageResultConverter();

    public static SelectMessageResultConverter getInstance() {
        return INSTANCE;
    }

    private SelectMessageResultConverter() {}

    @Override
    public PullMessageResponse convert(final SelectMessageResult src) {
        if (src == null) {
            return null;
        }

        final String topic = src.getTopic();
        final int partitionId = src.getPartitionId();
        final int startOffset = src.getStartLogicOffset();
        final List<Message> messages = src.getMessages();
        final Integer selectStateCode = src.getType().getCode();

        return new PullMessageResponse(topic, partitionId, startOffset, messages, selectStateCode);
    }
}

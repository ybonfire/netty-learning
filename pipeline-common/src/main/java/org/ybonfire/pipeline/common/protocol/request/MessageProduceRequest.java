package org.ybonfire.pipeline.common.protocol.request;

import lombok.Builder;
import lombok.Data;
import org.ybonfire.pipeline.common.model.Message;
import org.ybonfire.pipeline.common.protocol.IRemotingRequestBody;

/**
 * 消息投递请求体
 *
 * @author Bo.Yuan5
 * @date 2022-07-14 13:34
 */
@Builder
@Data
public class MessageProduceRequest implements IRemotingRequestBody {
    private final String topic;
    private final Integer partitionId;
    private String address;
    private final Message message;
}

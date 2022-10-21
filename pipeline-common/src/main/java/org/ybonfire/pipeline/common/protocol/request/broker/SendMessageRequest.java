package org.ybonfire.pipeline.common.protocol.request.broker;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.ybonfire.pipeline.common.model.Message;
import org.ybonfire.pipeline.common.protocol.IRemotingRequestBody;

/**
 * 消息投递请求体
 *
 * @author Bo.Yuan5
 * @date 2022-07-14 13:34
 */
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class SendMessageRequest implements IRemotingRequestBody {
    /**
     * topic名称
     */
    private String topic;
    /**
     * partitionId
     */
    private Integer partitionId;
    /**
     * 消息
     */
    private Message message;
}

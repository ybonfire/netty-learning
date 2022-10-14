package org.ybonfire.pipeline.common.protocol.response.broker;

import org.ybonfire.pipeline.common.protocol.IRemotingResponseBody;

import lombok.Data;

/**
 * 消息投递请求体
 *
 * @author Bo.Yuan5
 * @date 2022-07-28 17:12
 */
@Data
public class MessageProduceResponse implements IRemotingResponseBody {
    private String topic;
    private Integer partitionId;
    private Long offset;
    private Boolean isSuccess;
    private String messageId;
}

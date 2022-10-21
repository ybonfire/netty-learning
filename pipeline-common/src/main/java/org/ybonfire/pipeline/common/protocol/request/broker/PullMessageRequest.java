package org.ybonfire.pipeline.common.protocol.request.broker;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.ybonfire.pipeline.common.protocol.IRemotingRequestBody;

/**
 * 消息拉取请求体
 *
 * @author yuanbo
 * @date 2022-10-20 14:05
 */
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class PullMessageRequest implements IRemotingRequestBody {
    /**
     * topic名称
     */
    private String topic;
    /**
     * partitionId
     */
    private Integer partitionId;
    /**
     * 开始拉取偏移量
     */
    private Integer pullStartOffset;
    /**
     * 最大拉取条数
     */
    private Integer messageNums;
    /**
     * 消费者组
     */
    private String group;
}

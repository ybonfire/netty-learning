package org.ybonfire.pipeline.common.protocol.response.broker;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.ybonfire.pipeline.common.model.Message;
import org.ybonfire.pipeline.common.protocol.IRemotingResponseBody;

import java.util.List;

/**
 * 消息拉取响应体
 *
 * @author yuanbo
 * @date 2022-10-20 23:42
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class PullMessageResponse implements IRemotingResponseBody {
    /**
     * topic名称
     */
    private String topic;
    /**
     * partitionId
     */
    private Integer partitionId;
    /**
     * 起始偏移量
     */
    private Integer startOffset;
    /**
     * 消息
     */
    private List<Message> messages;
    /**
     * 查询状态
     */
    private Integer selectStateCode;
}

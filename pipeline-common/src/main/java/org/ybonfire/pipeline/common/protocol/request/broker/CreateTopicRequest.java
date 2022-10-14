package org.ybonfire.pipeline.common.protocol.request.broker;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.ybonfire.pipeline.common.protocol.IRemotingRequestBody;

/**
 * 创建主题请求
 *
 * @author yuanbo
 * @date 2022-09-22 18:24
 */
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class CreateTopicRequest implements IRemotingRequestBody {
    private String topic;
    private Integer partitionNums;
}

package org.ybonfire.pipeline.common.protocol.request.broker;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.ybonfire.pipeline.common.protocol.IRemotingRequestBody;

/**
 * 更新主题请求
 *
 * @author yuanbo
 * @date 2022-10-14 17:35
 */
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class UpdateTopicRequest implements IRemotingRequestBody {
    private String topic;
    private Integer partitionNums;
}

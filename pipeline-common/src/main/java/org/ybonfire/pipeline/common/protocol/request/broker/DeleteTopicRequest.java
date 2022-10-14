package org.ybonfire.pipeline.common.protocol.request.broker;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.ybonfire.pipeline.common.protocol.IRemotingRequestBody;

/**
 * 删除主题请求
 *
 * @author yuanbo
 * @date 2022-10-14 17:50
 */
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class DeleteTopicRequest implements IRemotingRequestBody {
    private String topic;
}

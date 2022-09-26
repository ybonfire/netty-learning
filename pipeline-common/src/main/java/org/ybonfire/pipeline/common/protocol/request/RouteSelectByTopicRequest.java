package org.ybonfire.pipeline.common.protocol.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.ybonfire.pipeline.common.protocol.IRemotingRequestBody;

/**
 * 查询指定路由请求体
 *
 * @author Bo.Yuan5
 * @date 2022-07-13 15:30
 */
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class RouteSelectByTopicRequest implements IRemotingRequestBody {
    private String topic;
}

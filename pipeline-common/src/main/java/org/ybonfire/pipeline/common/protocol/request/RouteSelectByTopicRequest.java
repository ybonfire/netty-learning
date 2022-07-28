package org.ybonfire.pipeline.common.protocol.request;

import lombok.Builder;
import lombok.Data;
import org.ybonfire.pipeline.common.protocol.IRemotingRequestBody;

/**
 * 查询指定路由请求体
 *
 * @author Bo.Yuan5
 * @date 2022-07-13 15:30
 */
@Builder
@Data
public class RouteSelectByTopicRequest implements IRemotingRequestBody {
    private final String topic;
}

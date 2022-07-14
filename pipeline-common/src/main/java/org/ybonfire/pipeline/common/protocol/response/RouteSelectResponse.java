package org.ybonfire.pipeline.common.protocol.response;

import java.util.HashMap;
import java.util.Map;

import lombok.Builder;
import lombok.Data;
import org.ybonfire.pipeline.common.protocol.IRemotingResponseBody;
import org.ybonfire.pipeline.common.protocol.TopicInfoRemotingEntity;

/**
 * 路由查询接口响应体
 *
 * @author Bo.Yuan5
 * @date 2022-07-13 10:58
 */
@Builder
@Data
public class RouteSelectResponse implements IRemotingResponseBody {
    private final Map<String, TopicInfoRemotingEntity> result;
}

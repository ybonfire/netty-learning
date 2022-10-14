package org.ybonfire.pipeline.common.protocol.response.nameserver;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.ybonfire.pipeline.common.model.TopicConfigRemotingEntity;
import org.ybonfire.pipeline.common.protocol.IRemotingResponseBody;

import java.util.Map;

/**
 * 路由查询接口响应体
 *
 * @author Bo.Yuan5
 * @date 2022-07-13 10:58
 */
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class RouteSelectResponse implements IRemotingResponseBody {
    private Map<String, TopicConfigRemotingEntity> result;
}

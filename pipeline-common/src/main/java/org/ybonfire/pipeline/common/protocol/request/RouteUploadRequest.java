package org.ybonfire.pipeline.common.protocol.request;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.ybonfire.pipeline.common.model.TopicConfigRemotingEntity;
import org.ybonfire.pipeline.common.protocol.IRemotingRequestBody;

import lombok.Builder;
import lombok.Data;

/**
 * 路由上报请求体
 *
 * @author Bo.Yuan5
 * @date 2022-07-01 18:00
 */
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public final class RouteUploadRequest implements IRemotingRequestBody {
    private String address;
    private List<TopicConfigRemotingEntity> topics;
}

package org.ybonfire.pipeline.common.protocol.request;

import lombok.Builder;
import lombok.Data;
import org.ybonfire.pipeline.common.protocol.IRemotingRequestBody;
import org.ybonfire.pipeline.common.model.TopicInfoRemotingEntity;

import java.util.List;

/**
 * 路由上报请求体
 *
 * @author Bo.Yuan5
 * @date 2022-07-01 18:00
 */
@Builder
@Data
public final class RouteUploadRequest implements IRemotingRequestBody {
    private String brokerId;
    private String address;
    private Integer role;
    private List<TopicInfoRemotingEntity> topics;
    private Long dataVersion;
}

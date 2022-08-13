package org.ybonfire.pipeline.common.protocol.request;

import lombok.Builder;
import lombok.Data;
import org.ybonfire.pipeline.common.protocol.IRemotingRequestBody;

/**
 * 加入集群请求
 *
 * @author Bo.Yuan5
 * @date 2022-08-05 18:07
 */
@Builder
@Data
public class JoinClusterRequest implements IRemotingRequestBody {
    private String nodeId;
    private String address;
}

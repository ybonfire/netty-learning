package org.ybonfire.pipeline.common.protocol.request.nameserver;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.ybonfire.pipeline.common.protocol.IRemotingRequestBody;

import java.util.List;

/**
 * 批量查询指定路由请求体
 *
 * @author yuanbo
 * @date 2022-10-14 16:37
 */
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class RouteSelectByTopicsRequest implements IRemotingRequestBody {
    private List<String> topics;
}

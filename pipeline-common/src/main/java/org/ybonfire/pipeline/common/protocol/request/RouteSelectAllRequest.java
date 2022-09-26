package org.ybonfire.pipeline.common.protocol.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.ybonfire.pipeline.common.protocol.IRemotingRequestBody;

/**
 * 查询全部路由请求体
 *
 * @author Bo.Yuan5
 * @date 2022-07-28 11:10
 */
@Builder
@NoArgsConstructor
@Data
public class RouteSelectAllRequest implements IRemotingRequestBody {}

package org.ybonfire.pipeline.common.protocol.request;

import org.ybonfire.pipeline.common.protocol.IRemotingRequestBody;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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

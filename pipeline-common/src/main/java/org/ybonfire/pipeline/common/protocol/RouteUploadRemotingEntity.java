package org.ybonfire.pipeline.common.protocol;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 路由上报请求体
 *
 * @author Bo.Yuan5
 * @date 2022-07-01 18:00
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class RouteUploadRemotingEntity {
    private String brokerId;
    private String address;
    private Integer role;
    private List<TopicInfoRemotingEntity> topics;
    private Long dataVersion;
}

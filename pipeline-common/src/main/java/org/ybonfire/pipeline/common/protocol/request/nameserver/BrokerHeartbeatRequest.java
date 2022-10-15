package org.ybonfire.pipeline.common.protocol.request.nameserver;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.ybonfire.pipeline.common.model.TopicConfigRemotingEntity;
import org.ybonfire.pipeline.common.protocol.IRemotingRequestBody;

import java.util.List;

/**
 * Broker心跳上报请求
 *
 * @author yuanbo
 * @date 2022-10-15 14:11
 */
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class BrokerHeartbeatRequest implements IRemotingRequestBody {
    /**
     * BrokerId
     */
    private String brokerId;
    /**
     * Broker角色
     */
    private Integer role;
    /**
     * Broker地址
     */
    private String address;
    /**
     * Topic信息
     */
    private List<TopicConfigRemotingEntity> topicConfigs;
    /**
     * 心跳时间戳
     */
    private Long timestamp;
}

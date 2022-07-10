package org.ybonfire.pipeline.common.protocol;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 服务结点响应体
 *
 * @author Bo.Yuan5
 * @date 2022-06-27 18:41
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class NodeRemotingEntity {
    /**
     * 结点地址 Ip:port
     */
    private String address;
    /**
     * 结点角色
     */
    private Integer role;
}

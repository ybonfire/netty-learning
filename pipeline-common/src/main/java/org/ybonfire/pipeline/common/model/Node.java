package org.ybonfire.pipeline.common.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 服务结点
 *
 * @author Bo.Yuan5
 * @date 2022-06-27 18:41
 */
@EqualsAndHashCode
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Node {
    /**
     * 结点地址 Ip:port
     */
    private String address;
    /**
     * 结点角色
     */
    private NodeRole role;
}

package org.ybonfire.pipeline.common.model;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 * 服务结点
 *
 * @author Bo.Yuan5
 * @date 2022-06-27 18:41
 */
@EqualsAndHashCode
@Builder
@Getter
public class Node {
    /**
     * 结点地址 Ip:port
     */
    private final String address;
    /**
     * 结点角色
     */
    private final NodeRole role;
}

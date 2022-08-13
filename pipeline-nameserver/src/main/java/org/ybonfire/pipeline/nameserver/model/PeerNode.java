package org.ybonfire.pipeline.nameserver.model;

import lombok.Builder;
import lombok.Data;

/**
 * 集群结点对象
 *
 * @author Bo.Yuan5
 * @date 2022-08-12 21:49
 */
@Builder
@Data
public class PeerNode {
    /**
     * 结点Id
     */
    private String id;
    /**
     * 结点地址
     */
    private String address;
}

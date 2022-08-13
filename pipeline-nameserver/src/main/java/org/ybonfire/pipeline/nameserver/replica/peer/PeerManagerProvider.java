package org.ybonfire.pipeline.nameserver.replica.peer;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * 集群结点对象管理器Provider
 *
 * @author Bo.Yuan5
 * @date 2022-08-12 21:54
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class PeerManagerProvider {
    private static final PeerManager INSTANCE = new PeerManager();

    /**
     * @description: 获取PeerManager实例
     * @param:
     * @return:
     * @date: 2022/08/12 21:55:57
     */
    public static PeerManager getInstance() {
        return INSTANCE;
    }
}

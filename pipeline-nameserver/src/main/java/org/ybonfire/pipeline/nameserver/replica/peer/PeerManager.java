package org.ybonfire.pipeline.nameserver.replica.peer;

import org.ybonfire.pipeline.nameserver.model.PeerNode;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * 集群结点对象管理器
 *
 * @author Bo.Yuan5
 * @date 2022-08-12 21:49
 */
public class PeerManager {
    private final Set<PeerNode> peers = new CopyOnWriteArraySet<>();

    PeerManager() {}

    /**
     * @description: 添加结点
     * @param:
     * @return:
     * @date: 2022/08/12 21:53:17
     */
    public void add(final PeerNode node) {
        peers.add(node);
    }

    /**
     * @description: 获取结点列表
     * @param:
     * @return:
     * @date: 2022/08/12 21:53:25
     */
    public Set<PeerNode> getPeers() {
        return this.peers;
    }
}

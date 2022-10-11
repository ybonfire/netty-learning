package org.ybonfire.pipeline.nameserver.replica.peer;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import org.ybonfire.pipeline.common.logger.IInternalLogger;
import org.ybonfire.pipeline.common.logger.impl.SimpleInternalLogger;
import org.ybonfire.pipeline.nameserver.model.PeerNode;

/**
 * 集群结点对象管理器
 *
 * @author Bo.Yuan5
 * @date 2022-08-12 21:49
 */
public class PeerManager {
    private static final IInternalLogger LOGGER = new SimpleInternalLogger();
    private static final PeerManager INSTANCE = new PeerManager();
    private final Set<PeerNode> peers = new CopyOnWriteArraySet<>();

    private PeerManager() {}

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

    /**
     * 获取PeerManager实例
     *
     * @return {@link PeerManager}
     */
    public static PeerManager getInstance() {
        return INSTANCE;
    }
}

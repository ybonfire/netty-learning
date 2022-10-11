package org.ybonfire.pipeline.client.thread;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.ybonfire.pipeline.client.listener.ClientChannelEventListener;
import org.ybonfire.pipeline.client.manager.NettyChannelManager;
import org.ybonfire.pipeline.common.listener.INettyChannelEventListener;
import org.ybonfire.pipeline.common.model.NettyChannelEvent;
import org.ybonfire.pipeline.common.thread.service.AbstractThreadService;

import io.netty.channel.Channel;

/**
 * Netty客户端连接相关事件处理器
 *
 * @author Bo.Yuan5
 * @date 2022-05-24 18:10
 */
public class ClientChannelEventHandleThreadService extends AbstractThreadService {
    private static final String NAME = "ClientChannelEventHandler";
    private final BlockingQueue<NettyChannelEvent> eventQueue = new LinkedBlockingQueue<>();
    private final INettyChannelEventListener listener;

    public ClientChannelEventHandleThreadService(final NettyChannelManager nettyChannelManager) {
        super(1000L);
        this.listener = new ClientChannelEventListener(nettyChannelManager);
    }

    @Override
    protected String getName() {
        return NAME;
    }

    /**
     * @description: 处理相关事件
     * @param:
     * @return:
     * @date: 2022/05/24 18:11:40
     */
    @Override
    protected void execute() {
        try {
            final NettyChannelEvent event = this.eventQueue.take();
            final String address = event.getAddress();
            final Channel channel = event.getChannel();
            switch (event.getType()) {
                case OPEN:
                    this.listener.onOpen(address, channel);
                    break;
                case CLOSE:
                    this.listener.onClose(address, channel);
                    break;
                default:
                    break;
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            // TODO
        }
    }

    /**
     * @description: 添加连接事件
     * @param:
     * @return:
     * @date: 2022/05/24 18:24:54
     */
    public void putEvent(final NettyChannelEvent event) {
        try {
            this.eventQueue.put(event);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            // TODO
        }
    }
}

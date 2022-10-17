package org.ybonfire.pipeline.client.listener;

import org.ybonfire.pipeline.client.connection.Connection;
import org.ybonfire.pipeline.client.connection.ConnectionManager;
import org.ybonfire.pipeline.common.listener.INettyChannelEventListener;
import org.ybonfire.pipeline.common.util.RemotingUtil;

import io.netty.channel.Channel;

/**
 * Netty客户端连接相关事件监听器
 *
 * @author Bo.Yuan5
 * @date 2022-05-24 17:30
 */
public class ClientChannelEventListener implements INettyChannelEventListener {

    /**
     * @description: 连接开启
     * @param:
     * @return:
     * @date: 2022/05/24 17:37:53
     */
    @Override
    public void onOpen(final Channel channel) {
        if (channel == null) {
            return;
        }

        // 将连接添加至ConnectionManager
        final Connection connection = Connection.wrap(channel);
        ConnectionManager.getInstance().add(connection);
    }

    /**
     * @description: 连接关闭
     * @param:
     * @return:
     * @date: 2022/05/24 17:37:58
     */
    @Override
    public void onClose(final Channel channel) {
        if (channel == null) {
            return;
        }

        // 移除并关闭指定地址的连接
        final String address = RemotingUtil.parseChannelAddress(channel);
        ConnectionManager.getInstance().remove(address);
    }
}

package org.ybonfire.netty.common.listener;

import io.netty.channel.Channel;

/**
 * Netty 连接相关事件监听器接口
 *
 * @author Bo.Yuan5
 * @date 2022-05-24 17:12
 */
public interface INettyChannelEventListener {

    /**
     * @description: 连接开启
     * @param:
     * @return:
     * @date: 2022/05/24 17:17:36
     */
    void onOpen(final String address, final Channel channel);

    /**
     * @description: 连接关闭
     * @param:
     * @return:
     * @date: 2022/05/24 17:18:14
     */
    void onClose(final String address, final Channel channel);
}

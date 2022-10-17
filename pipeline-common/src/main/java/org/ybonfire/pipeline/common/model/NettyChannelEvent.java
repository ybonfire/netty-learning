package org.ybonfire.pipeline.common.model;

import io.netty.channel.Channel;

/**
 * Netty连接事件
 *
 * @author Bo.Yuan5
 * @date 2022-05-24 18:17
 */
public class NettyChannelEvent {
    /**
     * 事件类型
     */
    private final NettyChannelEventTypeEnum type;
    /**
     * 连接
     */
    private final Channel channel;

    public NettyChannelEvent(final NettyChannelEventTypeEnum type, final Channel channel) {
        this.type = type;
        this.channel = channel;
    }

    public NettyChannelEventTypeEnum getType() {
        return type;
    }

    public Channel getChannel() {
        return channel;
    }
}

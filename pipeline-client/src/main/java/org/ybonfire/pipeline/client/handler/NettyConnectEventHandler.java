package org.ybonfire.pipeline.client.handler;

import io.netty.channel.Channel;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import org.ybonfire.pipeline.client.thread.ClientChannelEventHandleThreadService;
import org.ybonfire.pipeline.common.logger.IInternalLogger;
import org.ybonfire.pipeline.common.logger.impl.SimpleInternalLogger;
import org.ybonfire.pipeline.common.model.NettyChannelEvent;
import org.ybonfire.pipeline.common.model.NettyChannelEventTypeEnum;
import org.ybonfire.pipeline.common.util.RemotingUtil;

import java.net.SocketAddress;

/**
 * Netty连接事件处理器
 *
 * @author yuanbo
 * @date 2022-10-12 17:50
 */
public final class NettyConnectEventHandler extends ChannelDuplexHandler {
    private static final IInternalLogger LOGGER = new SimpleInternalLogger();
    private static final NettyConnectEventHandler INSTANCE = new NettyConnectEventHandler();
    private final ClientChannelEventHandleThreadService channelEventHandleThreadService =
        new ClientChannelEventHandleThreadService();

    private NettyConnectEventHandler() {}

    /**
     * @description: 建立连接
     * @param:
     * @return:
     * @date: 2022/05/24 23:01:03
     */
    @Override
    public void connect(final ChannelHandlerContext ctx, final SocketAddress remoteAddress,
        final SocketAddress localAddress, final ChannelPromise promise) throws Exception {
        // 建立连接
        super.connect(ctx, remoteAddress, localAddress, promise);

        final String local = RemotingUtil.getLocalAddress();
        final String remote = RemotingUtil.parseChannelAddress(ctx.channel());
        LOGGER.info("NETTY CLIENT PIPELINE: CONNECT " + local + "->" + remote);

        // 发布连接事件
        final NettyChannelEvent event = buildNettyChannelEvent(NettyChannelEventTypeEnum.OPEN, ctx.channel());
        channelEventHandleThreadService.putEvent(event);
    }

    /**
     * @description: 断开连接
     * @param:
     * @return:
     * @date: 2022/05/24 23:01:20
     */
    @Override
    public void disconnect(final ChannelHandlerContext ctx, final ChannelPromise promise) throws Exception {
        // 断开连接
        super.disconnect(ctx, promise);

        final String remote = RemotingUtil.parseChannelAddress(ctx.channel());
        LOGGER.info("NETTY CLIENT PIPELINE: DISCONNECT " + remote);

        // 发布关闭事件
        final NettyChannelEvent event = buildNettyChannelEvent(NettyChannelEventTypeEnum.CLOSE, ctx.channel());
        channelEventHandleThreadService.putEvent(event);
    }

    /**
     * @description: 关闭连接
     * @param:
     * @return:
     * @date: 2022/05/24 23:01:35
     */
    @Override
    public void close(final ChannelHandlerContext ctx, final ChannelPromise promise) throws Exception {
        // 断开连接
        super.disconnect(ctx, promise);

        final String remote = RemotingUtil.parseChannelAddress(ctx.channel());
        LOGGER.info("NETTY CLIENT PIPELINE: DISCONNECT " + remote);

        // 发布关闭事件
        final NettyChannelEvent event = buildNettyChannelEvent(NettyChannelEventTypeEnum.CLOSE, ctx.channel());
        channelEventHandleThreadService.putEvent(event);
    }

    /**
     * @description: 构造NettyChannelEvent
     * @param:
     * @return:
     * @date: 2022/05/24 23:06:17
     */
    private NettyChannelEvent buildNettyChannelEvent(final NettyChannelEventTypeEnum type, final Channel channel) {
        return new NettyChannelEvent(type, channel);
    }

    /**
     * 获取NettyConnectEventHandler实例
     *
     * @return {@link NettyConnectEventHandler}
     */
    public static NettyConnectEventHandler getInstance() {
        return INSTANCE;
    }
}

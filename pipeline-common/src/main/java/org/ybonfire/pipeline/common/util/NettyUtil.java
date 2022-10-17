package org.ybonfire.pipeline.common.util;

import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.epoll.EpollSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.ServerSocketChannel;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.concurrent.DefaultEventExecutorGroup;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.concurrent.ThreadFactory;

/**
 * Netty工具类
 *
 * @author yuanbo
 * @date 2022-10-17 17:52
 */
@NoArgsConstructor(access = AccessLevel.PACKAGE)
public final class NettyUtil {

    /**
     * @description: 创建DefaultEventExecutorGroup
     * @param:
     * @return:
     * @date: 2022/10/17 18:01:21
     */
    public static DefaultEventExecutorGroup newDefaultEventExecutorGroup(final int threadNums,
        final ThreadFactory threadFactory) {
        return new DefaultEventExecutorGroup(threadNums, threadFactory);
    }

    /**
     * @description: 创建EventLoopGroup
     * @param:
     * @return:
     * @date: 2022/10/17 17:56:13
     */
    public static EventLoopGroup newEventLoopGroup(final int threadNums, final ThreadFactory threadFactory) {
        return newEventLoopGroup(threadNums, threadFactory, false);
    }

    /**
     * @description: 创建EventLoopGroup
     * @param:
     * @return:
     * @date: 2022/10/17 17:56:13
     */
    public static EventLoopGroup newEventLoopGroup(final int threadNums, final ThreadFactory threadFactory,
        final boolean isEpollEnabled) {
        return isEpollEnabled ? new EpollEventLoopGroup(threadNums, threadFactory)
            : new NioEventLoopGroup(threadNums, threadFactory);
    }

    /**
     * @description: 获取ClientSocketChannel类型
     * @param:
     * @return:
     * @date: 2022/10/17 17:58:06
     */
    public static Class<? extends SocketChannel> getClientSocketChannelClass() {
        return getClientSocketChannelClass(false);
    }

    /**
     * @description: 获取ClientSocketChannel类型
     * @param:
     * @return:
     * @date: 2022/10/17 17:58:06
     */
    public static Class<? extends SocketChannel> getClientSocketChannelClass(final boolean isEpollEnabled) {
        return isEpollEnabled ? EpollSocketChannel.class : NioSocketChannel.class;
    }

    /**
     * @description: 获取ServerSocketChannel类型
     * @param:
     * @return:
     * @date: 2022/10/17 17:58:18
     */
    public static Class<? extends ServerSocketChannel> getServerSocketChannelClass() {
        return getServerSocketChannelClass(false);
    }

    /**
     * @description: 获取ServerSocketChannel类型
     * @param:
     * @return:
     * @date: 2022/10/17 17:58:18
     */
    public static Class<? extends ServerSocketChannel> getServerSocketChannelClass(final boolean isEpollEnabled) {
        return isEpollEnabled ? EpollServerSocketChannel.class : NioServerSocketChannel.class;
    }
}

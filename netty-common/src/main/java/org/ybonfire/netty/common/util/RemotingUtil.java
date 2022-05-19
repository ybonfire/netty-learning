package org.ybonfire.netty.common.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.net.InetSocketAddress;
import java.net.SocketAddress;

/**
 * 远程调用工具类
 *
 * @author Bo.Yuan5
 * @date 2022-05-19 10:40
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class RemotingUtil {
    /**
     * @description: 将ip:port结构的字符串转换为InetSocketAddress对象
     * @param:
     * @return:
     * @date: 2022/05/19 10:40:35
     */
    public static SocketAddress addressToSocketAddress(final String address) {
        int split = address.lastIndexOf(":");
        String host = address.substring(0, split);
        String port = address.substring(split + 1);
        return new InetSocketAddress(host, Integer.parseInt(port));
    }
}

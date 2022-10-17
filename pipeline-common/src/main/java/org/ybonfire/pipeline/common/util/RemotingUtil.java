package org.ybonfire.pipeline.common.util;

import io.netty.channel.Channel;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.ybonfire.pipeline.common.logger.IInternalLogger;
import org.ybonfire.pipeline.common.logger.impl.SimpleInternalLogger;

import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.Enumeration;

/**
 * 远程调用工具类
 *
 * @author Bo.Yuan5
 * @date 2022-05-19 10:40
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class RemotingUtil {
    private static final IInternalLogger LOGGER = new SimpleInternalLogger();

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

    /**
     * @description: 解析连接地址
     * @param:
     * @return:
     * @date: 2022/05/24 14:31:43
     */
    public static String parseChannelAddress(final Channel channel) {
        if (null == channel) {
            return "";
        }
        SocketAddress remote = channel.remoteAddress();
        final String addr = remote != null ? remote.toString() : "";

        if (addr.length() > 0) {
            int index = addr.lastIndexOf("/");
            if (index >= 0) {
                return addr.substring(index + 1);
            }

            return addr;
        }

        return "";
    }

    /**
     * 套接字地址解析地址
     *
     * @param socketAddress 套接字地址
     * @return {@link String}
     */
    public static String parseSocketAddress(SocketAddress socketAddress) {
        if (socketAddress != null) {
            final String addr = socketAddress.toString();

            if (addr.length() > 0) {
                return addr.substring(1);
            }
        }
        return "";
    }

    /**
     * 关闭通道
     *
     * @param channel 通道
     */
    public static void closeChannel(final Channel channel) {
        if (channel == null) {
            return;
        }

        final String addrRemote = parseChannelAddress(channel);
        channel.close().addListener(future -> LOGGER.info(
            "closeChannel: close the connection to remote address: " + addrRemote + "result: " + future.isSuccess()));
    }

    /**
     * 获取本地地址
     *
     * @return {@link String}
     */
    public static String getLocalAddress() {
        try {
            // Traversal Network interface to get the first non-loopback and non-private address
            Enumeration<NetworkInterface> enumeration = NetworkInterface.getNetworkInterfaces();
            ArrayList<String> ipv4Result = new ArrayList<>();
            ArrayList<String> ipv6Result = new ArrayList<>();
            while (enumeration.hasMoreElements()) {
                final NetworkInterface networkInterface = enumeration.nextElement();
                final Enumeration<InetAddress> en = networkInterface.getInetAddresses();
                while (en.hasMoreElements()) {
                    final InetAddress address = en.nextElement();
                    if (!address.isLoopbackAddress()) {
                        if (address instanceof Inet6Address) {
                            ipv6Result.add(normalizeHostAddress(address));
                        } else {
                            ipv4Result.add(normalizeHostAddress(address));
                        }
                    }
                }
            }

            // prefer ipv4
            if (!ipv4Result.isEmpty()) {
                for (String ip : ipv4Result) {
                    if (ip.startsWith("127.0") || ip.startsWith("192.168")) {
                        continue;
                    }

                    return ip;
                }

                return ipv4Result.get(ipv4Result.size() - 1);
            } else if (!ipv6Result.isEmpty()) {
                return ipv6Result.get(0);
            }
            // If failed to find,fall back to localhost
            final InetAddress localHost = InetAddress.getLocalHost();
            return normalizeHostAddress(localHost);
        } catch (Exception e) {
            LOGGER.error("Failed to obtain local address", e);
        }

        return null;
    }

    public static String normalizeHostAddress(final InetAddress localHost) {
        if (localHost instanceof Inet6Address) {
            return "[" + localHost.getHostAddress() + "]";
        } else {
            return localHost.getHostAddress();
        }
    }
}

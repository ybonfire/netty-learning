package org.ybonfire.pipeline.broker;

import org.ybonfire.pipeline.broker.config.BrokerConfig;
import org.ybonfire.pipeline.broker.server.Broker;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Broker启动器
 *
 * @author Bo.Yuan5
 * @date 2022-08-24 21:36
 */
public class BrokerStartup {

    /**
     * @description: 启动Broker
     * @param:
     * @return:
     * @date: 2022/09/22 10:08:35
     */
    public static void main(String[] args) {
        final List<String> nameServerAddressList = Stream.of("0:0:0:0:0:0:0:0:14690").collect(Collectors.toList());
        final Broker server = new Broker(BrokerConfig.getInstance(), nameServerAddressList);

        Runtime.getRuntime().addShutdownHook(new Thread(server::shutdown));

        server.start();
    }
}

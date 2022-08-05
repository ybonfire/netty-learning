package org.ybonfire.pipeline.producer;

import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 这里添加类的注释【强制】
 *
 * @author Bo.Yuan5
 * @date 2022-08-05 14:18
 */
public class DefaultProducerImplTest {
    public static void main(String[] args) {
        final IProducer producer =
            new DefaultProducerImpl(Stream.of("/0:0:0:0:0:0:0:0:14690").collect(Collectors.toList()));
        producer.start();
    }
}

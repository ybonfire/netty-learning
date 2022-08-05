package org.ybonfire.pipeline.producer.config;

import lombok.Getter;

/**
 * 生产者配置
 *
 * @author Bo.Yuan5
 * @date 2022-06-28 10:24
 */
@Getter
public final class ProducerConfig {

    private final long lingerMillis = 1000L;
}

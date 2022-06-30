package org.ybonfire.pipeline.producer.converter;

/**
 * 参数转换器接口
 *
 * @author Bo.Yuan5
 * @date 2022-06-29 18:30
 */
@FunctionalInterface
public interface IConverter<Src, Dest> {

    /**
     * @description: 参数转换
     * @param:
     * @return:
     * @date: 2022/06/29 18:31:34
     */
    Dest convert(final Src src);
}

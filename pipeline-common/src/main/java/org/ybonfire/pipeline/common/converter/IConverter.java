package org.ybonfire.pipeline.common.converter;

/**
 * 数据转换器接口
 *
 * @author Bo.Yuan5
 * @date 2022-07-28 20:31
 */
public interface IConverter<Src, Dest> {

    /**
     * @description: 参数转换
     * @param:
     * @return:
     * @date: 2022/07/28 20:31:40
     */
    Dest convert(final Src src);
}

package org.ybonfire.pipeline.common.converter;

/**
 * 数据转换器接口
 *
 * @author Bo.Yuan5
 * @date 2022-07-28 20:31
 */
public interface IConverter<Src, Dest> {

    /**
     * @description: 参数转换 Src -> Dest
     * @param:
     * @return:
     * @date: 2022/07/28 20:31:40
     */
    default Dest convert(final Src src) {
        throw new UnsupportedOperationException();
    }

    /**
     * @description: 参数转换 Dest -> Src
     * @param:
     * @return:
     * @date: 2022/09/23 10:24:10
     */
    default Src revert(final Dest dest) {
        throw new UnsupportedOperationException();
    }
}

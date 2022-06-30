package org.ybonfire.pipeline.producer.converter.impl;

import org.ybonfire.pipeline.producer.converter.IConverter;
import org.ybonfire.pipeline.common.model.Node;
import org.ybonfire.pipeline.common.model.NodeRole;
import org.ybonfire.pipeline.common.protocol.NodeResponse;

/**
 * Node参数转换器
 *
 * @author Bo.Yuan5
 * @date 2022-06-29 18:47
 */
public final class NodeConverter implements IConverter<NodeResponse, Node> {

    /**
     * @description: 参数转换
     * @param:
     * @return:
     * @date: 2022/06/29 18:47:46
     */
    @Override
    public Node convert(final NodeResponse src) {
        if (src == null) {
            return null;
        }

        return Node.builder().address(src.getAddress()).role(NodeRole.of(src.getRole())).build();
    }
}

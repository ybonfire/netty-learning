package org.ybonfire.pipeline.producer.converter;

import org.ybonfire.pipeline.common.converter.IConverter;
import org.ybonfire.pipeline.common.model.Node;
import org.ybonfire.pipeline.common.model.NodeRemotingEntity;
import org.ybonfire.pipeline.common.model.NodeRole;

/**
 * Node参数转换器
 *
 * @author Bo.Yuan5
 * @date 2022-06-29 18:47
 */
public final class NodeConverter implements IConverter<NodeRemotingEntity, Node> {

    /**
     * @description: 参数转换
     * @param:
     * @return:
     * @date: 2022/06/29 18:47:46
     */
    @Override
    public Node convert(final NodeRemotingEntity src) {
        if (src == null) {
            return null;
        }

        return Node.builder().address(src.getAddress()).role(NodeRole.of(src.getRole())).build();
    }
}

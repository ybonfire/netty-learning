package org.ybonfire.pipeline.nameserver.converter;

import org.ybonfire.pipeline.common.converter.IConverter;
import org.ybonfire.pipeline.common.model.Node;
import org.ybonfire.pipeline.common.model.NodeRemotingEntity;

/**
 * Node参数转换器
 *
 * @author Bo.Yuan5
 * @date 2022-06-29 18:47
 */
public final class NodeConverter implements IConverter<Node, NodeRemotingEntity> {

    /**
     * @description: 参数转换
     * @param:
     * @return:
     * @date: 2022/07/28 20:48:34
     */
    @Override
    public NodeRemotingEntity convert(final Node node) {
        return null;
    }
}

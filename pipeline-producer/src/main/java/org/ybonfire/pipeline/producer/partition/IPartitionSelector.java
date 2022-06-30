package org.ybonfire.pipeline.producer.partition;

import org.ybonfire.pipeline.common.model.Message;
import org.ybonfire.pipeline.common.model.PartitionInfo;

import java.util.Optional;

/**
 * Partition选择器接口
 *
 * @author Bo.Yuan5
 * @date 2022-06-27 18:25
 */
public interface IPartitionSelector {

    /**
     * @description: 根据消息选择需要投递的目标PartitionId
     * @param:
     * @return:
     * @date: 2022/06/27 18:27:27
     */
    Optional<PartitionInfo> select(final Message message);
}

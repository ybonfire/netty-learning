package org.ybonfire.pipeline.common.model;

import org.ybonfire.pipeline.common.exception.ExceptionTypeEnum;
import org.ybonfire.pipeline.common.util.ExceptionUtil;

/**
 * 结点角色
 *
 * @author Bo.Yuan5
 * @date 2022-06-27 18:43
 */
public enum NodeRole {
    /**
     * Partition Leader
     */
    LEADER(1),
    /**
     * Partition Follower
     */
    FOLLOWER(0);

    NodeRole(final int code) {
        this.code = code;
    }

    private int code;

    public static NodeRole of(final Integer code) {
        if (code == null) {
            throw ExceptionUtil.exception(ExceptionTypeEnum.ILLEGAL_ARGUMENT);
        }

        for (final NodeRole role : NodeRole.values()) {
            if (role.code == code) {
                return role;
            }
        }

        throw ExceptionUtil.exception(ExceptionTypeEnum.ILLEGAL_ARGUMENT);
    }
}

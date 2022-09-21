package org.ybonfire.pipeline.common.model;

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

    public int getCode() {
        return code;
    }

    public static NodeRole of(final Integer code) {
        if (code == null) {
            throw new IllegalArgumentException();
        }

        for (final NodeRole role : NodeRole.values()) {
            if (role.code == code) {
                return role;
            }
        }

        throw new IllegalArgumentException();
    }
}

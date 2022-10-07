package org.ybonfire.pipeline.broker.model;

/**
 * Broker角色枚举
 *
 * @author Bo.Yuan5
 * @date 2022-09-02 15:41
 */
public enum RoleEnum {
    /**
     * Leader
     */
    LEADER(1, "Leader"),
    /**
     * Follower
     */
    FOLLOWER(0, "Follower");

    private int code;
    private String description;

    RoleEnum(int code, String description) {
        this.code = code;
        this.description = description;
    }

    public int getCode() {
        return code;
    }

    public static RoleEnum of(final int code) {
        for (final RoleEnum role : RoleEnum.values()) {
            if (role.code == code) {
                return role;
            }
        }

        return null;
    }
}

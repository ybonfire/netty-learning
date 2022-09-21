package org.ybonfire.pipeline.broker.model;

/**
 * 这里添加类的注释【强制】
 *
 * @author Bo.Yuan5
 * @date 2022-09-02 15:41
 */
public enum Role {
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

    Role(int code, String description) {
        this.code = code;
        this.description = description;
    }

    public static Role of(final int code) {
        for (final Role role : Role.values()) {
            if (role.code == code) {
                return role;
            }
        }

        return null;
    }
}

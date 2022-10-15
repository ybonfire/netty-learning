package org.ybonfire.pipeline.broker.role;

import org.ybonfire.pipeline.broker.model.RoleEnum;

import java.util.concurrent.atomic.AtomicReference;

/**
 * Role管理器
 *
 * @author Bo.Yuan5
 * @date 2022-09-02 15:47
 */
public final class RoleManager {
    private static final AtomicReference<RoleEnum> ROLE_HOLDER = new AtomicReference<>(RoleEnum.LEADER);
    private static final RoleManager INSTANCE = new RoleManager();

    private RoleManager() {}

    /**
     * @description: 设置角色
     * @param:
     * @return:
     * @date: 2022/09/02 16:13:37
     */
    public void set(final RoleEnum role) {
        if (role == null) {
            throw new UnsupportedOperationException();
        }

        ROLE_HOLDER.set(role);
    }

    /**
     * @description: 获取角色
     * @param:
     * @return:
     * @date: 2022/09/02 16:13:45
     */
    public RoleEnum get() {
        return ROLE_HOLDER.get();
    }

    /**
     * 获取RoleManager实例
     *
     * @return {@link RoleManager}
     */
    public static RoleManager getInstance() {
        return INSTANCE;
    }
}

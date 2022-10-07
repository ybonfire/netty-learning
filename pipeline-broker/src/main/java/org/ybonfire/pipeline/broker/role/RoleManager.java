package org.ybonfire.pipeline.broker.role;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.ybonfire.pipeline.broker.model.RoleEnum;

import java.util.concurrent.atomic.AtomicReference;

/**
 * Role管理器
 *
 * @author Bo.Yuan5
 * @date 2022-09-02 15:47
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class RoleManager {
    private static final AtomicReference<RoleEnum> ROLE_HOLDER = new AtomicReference<>(RoleEnum.LEADER);

    /**
     * @description: 设置角色
     * @param:
     * @return:
     * @date: 2022/09/02 16:13:37
     */
    public static void set(final RoleEnum role) {
        ROLE_HOLDER.set(role);
    }

    /**
     * @description: 获取角色
     * @param:
     * @return:
     * @date: 2022/09/02 16:13:45
     */
    public static RoleEnum get() {
        return ROLE_HOLDER.get();
    }
}

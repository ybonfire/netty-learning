package org.ybonfire.netty.common.callback;

import org.ybonfire.netty.common.command.RemotingCommand;

/**
 * 这里添加类的注释【强制】
 *
 * @author Bo.Yuan5
 * @date 2022-06-20 22:07
 */
public interface IRequestCallback {
    /**
     * @description: 请求成功回调
     * @param:
     * @return:
     * @date: 2022/06/20 18:53:48
     */
    void onSuccess(final RemotingCommand response);

    /**
     * @description: 请求失败回调
     * @param:
     * @return:
     * @date: 2022/06/20 21:43:23
     */
    void onException(final Throwable ex);
}

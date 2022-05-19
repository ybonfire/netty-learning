package org.ybonfire.netty.common.client;

import org.ybonfire.netty.common.command.RemotingCommand;
import org.ybonfire.netty.common.remoting.IRemotingService;

/**
 * 客户端接口
 *
 * @author Bo.Yuan5
 * @date 2022-05-18 10:14
 */
public interface IRemotingClient extends IRemotingService {

    /**
     * @description: 同步调用
     * @param:
     * @return: 
     * @date: 2022/05/18 18:20:45
     */
    RemotingCommand request(final String address, final RemotingCommand request, final long timeoutMillis)
        throws InterruptedException;

    /**
     * @description: 异步调用
     * @param:
     * @return:
     * @date: 2022/05/18 18:20:53
     */
    void requestAsync(final String address, final RemotingCommand request, final long timeoutMillis)
        throws InterruptedException;

    /**
     * @description: 单向调用
     * @param:
     * @return:
     * @date: 2022/05/18 18:21:02
     */
    void requestOneWay(final String address, final RemotingCommand request, final long timeoutMillis)
        throws InterruptedException;
}

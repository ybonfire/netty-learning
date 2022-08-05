package org.ybonfire.pipeline.producer.metadata;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

import org.ybonfire.pipeline.common.exception.BaseException;
import org.ybonfire.pipeline.common.exception.ExceptionTypeEnum;
import org.ybonfire.pipeline.common.model.TopicInfo;
import org.ybonfire.pipeline.common.util.ExceptionUtil;
import org.ybonfire.pipeline.producer.client.impl.NameServerClientImpl;

/**
 * 元数据服务
 *
 * @author Bo.Yuan5
 * @date 2022-06-27 21:46
 */
public class NameServers {
    private final AtomicBoolean started = new AtomicBoolean(false);
    private final List<String> nameServerAddressList;
    private final NameServerClientImpl nameServerClient;

    public NameServers(final List<String> nameServerAddressList) {
        this(nameServerAddressList, new NameServerClientImpl());
    }

    public NameServers(final List<String> nameServerAddressList, final NameServerClientImpl nameServerClient) {
        this.nameServerAddressList = nameServerAddressList;
        this.nameServerClient = nameServerClient;
    }

    public void start() {
        if (started.compareAndSet(false, true)) {
            nameServerClient.start();
        }
    }

    public void stop() {
        if (started.compareAndSet(true, false)) {
            nameServerClient.shutdown();
        }
    }

    /**
     * @description: 查询所有Topic信息
     * @param:
     * @return:
     * @date: 2022/06/29 09:55:22
     */
    public List<TopicInfo> selectAllTopicInfo(final long timeoutMillis) {
        if (nameServerAddressList.isEmpty()) {
            throw ExceptionUtil.exception(ExceptionTypeEnum.ILLEGAL_ARGUMENT);
        }

        BaseException e = null;
        try {
            for (int i = 0; i < nameServerAddressList.size(); ++i) {
                final String address = nameServerAddressList.get(i);
                return nameServerClient.selectAllTopicInfo(address, timeoutMillis);
            }
        } catch (BaseException ex) {
            e = ex;
        }

        throw e == null ? ExceptionUtil.exception(ExceptionTypeEnum.UNKNOWN) : e;
    }

    /**
     * @description: 根据topic名称查询Topic信息
     * @param:
     * @return:
     * @date: 2022/06/27 21:36:35
     */
    public Optional<TopicInfo> selectTopicInfo(final String topic, final long timeoutMillis) {
        if (nameServerAddressList.isEmpty()) {
            throw ExceptionUtil.exception(ExceptionTypeEnum.ILLEGAL_ARGUMENT);
        }

        BaseException e = null;
        try {
            for (int i = 0; i < nameServerAddressList.size(); ++i) {
                final String address = nameServerAddressList.get(i);
                return nameServerClient.selectTopicInfo(topic, address, timeoutMillis);
            }
        } catch (BaseException ex) {
            e = ex;
        }

        throw e == null ? ExceptionUtil.exception(ExceptionTypeEnum.UNKNOWN) : e;
    }
}

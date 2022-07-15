package org.ybonfire.pipeline.producer.metadata;

import java.util.List;
import java.util.Optional;

import org.ybonfire.pipeline.common.exception.BaseException;
import org.ybonfire.pipeline.common.exception.ExceptionTypeEnum;
import org.ybonfire.pipeline.common.model.TopicInfo;
import org.ybonfire.pipeline.common.util.ExceptionUtil;
import org.ybonfire.pipeline.producer.client.IProduceClient;
import org.ybonfire.pipeline.producer.client.impl.ProducerClientImpl;

/**
 * 元数据服务
 *
 * @author Bo.Yuan5
 * @date 2022-06-27 21:46
 */
public class NameServers {
    private final List<String> nameServerAddressList;
    private final IProduceClient client;

    public NameServers(final List<String> nameServerAddressList) {
        this(nameServerAddressList, new ProducerClientImpl());
    }

    public NameServers(final List<String> nameServerAddressList, final IProduceClient client) {
        this.nameServerAddressList = nameServerAddressList;
        this.client = client;
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
                return client.selectAllTopicInfo(address, timeoutMillis);
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
                return client.selectTopicInfo(topic, address, timeoutMillis);
            }
        } catch (BaseException ex) {
            e = ex;
        }

        throw e == null ? ExceptionUtil.exception(ExceptionTypeEnum.UNKNOWN) : e;
    }
}

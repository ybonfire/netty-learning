package org.ybonfire.pipeline.broker;

import org.ybonfire.pipeline.broker.store.message.IMessageStoreService;
import org.ybonfire.pipeline.broker.store.message.impl.DefaultMessageStoreServiceImpl;
import org.ybonfire.pipeline.common.model.Message;

/**
 * IMessageStoreService测试类
 *
 * @author yuanbo
 * @date 2022-10-07 22:54
 */
public class MessageStoreTest {
    public static void main(String[] args) {
        final IMessageStoreService messageStoreService = DefaultMessageStoreServiceImpl.getInstance();
        messageStoreService.start();

        final long startTime = System.currentTimeMillis();
        for (int i = 0; i < 50; ++i) {
            final Message message = Message.builder().topic("test").key("key").payload("ybonfire".getBytes()).build();
            messageStoreService.store("test", 5, message);
        }

        System.out.println("cost : " + (System.currentTimeMillis() - startTime) + "ms");
    }
}

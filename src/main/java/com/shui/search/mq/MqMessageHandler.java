package com.shui.search.mq;

import com.shui.config.RabbitConfig;
import com.shui.service.SearchService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *  提交/删除文章时
 *  异步发消息：es 更新/删除索引
 */
@Slf4j
@Component
@RabbitListener(queues = RabbitConfig.ES_QUEUE)
public class MqMessageHandler {

    @Autowired
    SearchService searchService;

    @RabbitHandler
    public void handler(PostMqIndexMessage message) {

        log.info("mq 收到一条消息： {}", message.toString());

        switch (message.getType()) {
            case PostMqIndexMessage.CREATE_OR_UPDATE:
                searchService.createOrUpdateIndex(message);
                break;
            case PostMqIndexMessage.REMOVE:
                searchService.removeIndex(message);
                break;
            default:
                log.error("没找到对应的消息类型，请注意！！ --》 {}", message.toString());
                break;
        }
    }

}

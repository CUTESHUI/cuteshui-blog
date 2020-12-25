package com.shui.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.shui.entity.UserMessage;
import com.shui.service.UserMessageService;
import com.shui.service.WebsocketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class WebsocketServiceImpl implements WebsocketService {

    @Autowired
    private UserMessageService userMessageService;
    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    @Async
    @Override
    public void sendMessCountToUser(Long toUserId) {
        // 未读消息
        int count = userMessageService.count(new QueryWrapper<UserMessage>()
                .eq("to_user_id", toUserId)
                .eq("status", "0")
        );

        // websocket通知 (/user/20/messCount)
        simpMessagingTemplate.convertAndSendToUser(toUserId.toString(), "/messCount", count);
    }

}

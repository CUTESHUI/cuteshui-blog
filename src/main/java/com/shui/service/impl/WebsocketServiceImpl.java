package com.shui.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.shui.entity.MUserMessage;
import com.shui.service.MUserMessageService;
import com.shui.service.WebsocketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class WebsocketServiceImpl implements WebsocketService {

    @Autowired
    MUserMessageService mUserMessageService;

    @Autowired
    SimpMessagingTemplate simpMessagingTemplate;

    @Async // 异步，不影响同一个方法中之前的代码
    @Override
    public void sendMessCountToUser(Long toUserId) {
        // 未读消息
        int count = mUserMessageService.count(new QueryWrapper<MUserMessage>()
                .eq("to_user_id", toUserId)
                .eq("status", "0")
        );

        // websocket通知 (/user/20/messCount)
        simpMessagingTemplate.convertAndSendToUser(toUserId.toString(), "/messCount", count);
    }

}

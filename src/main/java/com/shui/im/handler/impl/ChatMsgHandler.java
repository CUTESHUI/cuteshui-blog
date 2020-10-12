package com.shui.im.handler.impl;

import cn.hutool.json.JSONUtil;
import com.shui.common.lang.Consts;
import com.shui.im.handler.MsgHandler;
import com.shui.im.handler.filter.ExculdeMineChannelContextFilter;
import com.shui.im.message.ChatImMess;
import com.shui.im.message.ChatOutMess;
import com.shui.im.vo.ImMess;
import com.shui.im.vo.ImTo;
import com.shui.im.vo.ImUser;
import com.shui.service.ChatService;
import com.shui.util.SpringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.tio.core.ChannelContext;
import org.tio.core.Tio;
import org.tio.websocket.common.WsRequest;
import org.tio.websocket.common.WsResponse;

import java.util.Date;

@Slf4j
@Service("chatService")
public class ChatMsgHandler implements MsgHandler {
    @Override
    public void handler(String data, WsRequest wsRequest, ChannelContext channelContext) {
        ChatImMess chatImMess = JSONUtil.toBean(data, ChatImMess.class);

        ImUser mine = chatImMess.getMine();
        ImTo to = chatImMess.getTo();

        ImMess imMess = new ImMess();
        imMess.setContent(mine.getContent());
        imMess.setAvatar(mine.getAvatar());
        // 是否是我发送的消息
        imMess.setMine(false);

        imMess.setUsername(mine.getUsername());
        imMess.setFromid(mine.getId());

        imMess.setId(Consts.IM_GROUP_ID);
        imMess.setTimestamp(new Date());
        imMess.setType(to.getType());


        ChatOutMess chatOutMess = new ChatOutMess();
        chatOutMess.setEmit("chatMessage");
        chatOutMess.setData(imMess);

        String result = JSONUtil.toJsonStr(chatOutMess);
        log.info("群聊消息----------> {}", result);

        WsResponse wsResponse = WsResponse.fromText(result, "utf-8");
        // 过滤 发之前
        // 如果当前通道和群聊通道一样，就无法发
        // 避免了重复，大家都用群聊通道
        ExculdeMineChannelContextFilter filter = new ExculdeMineChannelContextFilter();
        filter.setCurrentContext(channelContext);

        Tio.sendToGroup(channelContext.getGroupContext(), Consts.IM_GROUP_NAME, wsResponse, filter);

        //保存群聊信息
        ChatService chatService = (ChatService) SpringUtil.getBean("chatsService");
        chatService.setGroupHistoryMsg(imMess);

    }
}

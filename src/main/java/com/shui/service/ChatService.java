package com.shui.service;

import com.shui.common.lang.Result;
import com.shui.im.dto.ImMess;
import com.shui.im.dto.ImUser;

import java.util.List;

public interface ChatService {

    /**
     * 获取当前IM用户
     */
    ImUser getCurrentUser();


    void setGroupHistoryMsg(ImMess responseMess);

    /**
     * 获取历史消息
     */
    List<Object> getGroupHistoryMsg(int count);

    /**
     * 获取群聊
     */
    Result getMineAndGroupData();
}

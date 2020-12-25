package com.shui.service;

/**
 *
 * @author CUTESHUI
 * @since 2020-09-24
 */
public interface WebsocketService {

    /**
     * 发送未读消息数量给当前用户
     */
    void sendMessCountToUser(Long toUserId);
}

package com.shui.controller;

import com.shui.common.lang.Result;
import com.shui.service.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/chat")
public class ChatController {

    @Autowired
    private ChatService chatService;

    @GetMapping("/getMineAndGroupData")
    public Result getMineAndGroupData() {
        return chatService.getMineAndGroupData();
    }

    @GetMapping("/getGroupHistoryMsg")
    public Result getGroupHistoryMsg() {
        return Result.success(chatService.getGroupHistoryMsg(20));
    }

}

package com.shui.dto;

import com.shui.entity.UserMessage;
import lombok.Data;

@Data
public class UserMessageDTO extends UserMessage {

    private String toUserName;
    private String fromUserName;
    private String postTitle;
    private String commentContent;

}

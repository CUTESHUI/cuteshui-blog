package com.shui.vo;

import com.shui.entity.MUserMessage;
import lombok.Data;

@Data
public class UserMessageVo extends MUserMessage {

    private String toUserName;
    private String fromUserName;
    private String postTitle;
    private String commentContent;

}

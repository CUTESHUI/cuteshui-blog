package com.shui.im.message;

import com.shui.im.dto.ImTo;
import com.shui.im.dto.ImUser;
import lombok.Data;

@Data
public class ChatImMess {

    private ImUser mine;
    private ImTo to;

}

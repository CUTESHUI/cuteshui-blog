package com.shui.im.message;

import com.shui.im.vo.ImTo;
import com.shui.im.vo.ImUser;
import lombok.Data;

@Data
public class ChatImMess {

    private ImUser mine;
    private ImTo to;

}

package com.shui.im.message;

import com.shui.im.dto.ImMess;
import lombok.Data;

@Data
public class ChatOutMess {

    private String emit;
    private ImMess data;

}

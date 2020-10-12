package com.shui.vo;

import com.shui.entity.MPost;
import lombok.Data;

@Data
public class PostVo extends MPost {
    private Long authorId;
    private String authorName;
    private String authorAvatar;
    private String categoryName;
}

package com.shui.dto;

import com.shui.entity.Post;
import lombok.Data;

@Data
public class PostDTO extends Post {
    private Long authorId;
    private String authorName;
    private String authorAvatar;
    private String categoryName;
}

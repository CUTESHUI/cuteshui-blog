package com.shui.dto;

import com.shui.entity.Comment;
import lombok.Data;

@Data
public class CommentDTO extends Comment {
    private Long authorId;
    private String authorName;
    private String authorAvatar;
}

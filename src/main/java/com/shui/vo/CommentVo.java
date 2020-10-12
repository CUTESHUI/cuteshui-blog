package com.shui.vo;

import com.shui.entity.MComment;
import lombok.Data;

@Data
public class CommentVo extends MComment {
    private Long authorId;
    private String authorName;
    private String authorAvatar;
}

package com.shui.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.shui.entity.Comment;
import com.shui.dto.CommentDTO;

/**
 *
 * @author CUTESHUI
 * @since 2020-09-24
 */
public interface CommentService extends IService<Comment> {

    /**
     *  1分页，评论内容id，评论用户id
     */
    IPage<CommentDTO> paging(Page page, Long postId, Long UserId, String order);
}

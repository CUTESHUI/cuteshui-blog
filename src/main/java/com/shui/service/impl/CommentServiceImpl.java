package com.shui.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.shui.entity.Comment;
import com.shui.mapper.CommentMapper;
import com.shui.service.CommentService;
import com.shui.dto.CommentDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author CUTESHUI
 * @since 2020-09-24
 */
@Service
public class CommentServiceImpl extends ServiceImpl<CommentMapper, Comment> implements CommentService {

    @Autowired
    private CommentMapper commentMapper;

    @Override
    public IPage<CommentDTO> paging(Page page, Long postId, Long UserId, String order) {

        QueryWrapper wrapper = new QueryWrapper<Comment>()
                .eq(postId != null,"post_id", postId)
                .eq(UserId != null, "user_id", UserId)
                .orderByDesc(order != null, order);

        // 1分页，评论内容id，评论用户id
        // 返回评论信息列表
        return commentMapper.selectComments(page, wrapper);
    }
}

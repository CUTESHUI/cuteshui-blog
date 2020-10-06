package com.shui.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.shui.entity.MComment;
import com.shui.mapper.MCommentMapper;
import com.shui.service.MCommentService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.shui.vo.CommentVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Wrapper;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author CUTESHUI
 * @since 2020-09-24
 */
@Service
public class MCommentServiceImpl extends ServiceImpl<MCommentMapper, MComment> implements MCommentService {

    @Autowired
    MCommentMapper mCommentMapper;

    @Override
    public IPage<CommentVo> paging(Page page, Long postId, Long UserId, String order) {

        QueryWrapper wrapper = new QueryWrapper<MComment>()
                .eq(postId != null,"post_id", postId)
                .eq(UserId != null, "user_id", UserId)
                .orderByDesc(order != null, order); //倒序

        // 1分页，评论内容id，评论用户id
        // 返回评论信息列表
        return mCommentMapper.selectComments(page, wrapper);
    }
}

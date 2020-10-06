package com.shui.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.shui.entity.MComment;
import com.baomidou.mybatisplus.extension.service.IService;
import com.shui.vo.CommentVo;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author CUTESHUI
 * @since 2020-09-24
 */
public interface MCommentService extends IService<MComment> {

    // 1分页，评论内容id，评论用户id
    IPage<CommentVo> paging(Page page, Long postId, Long UserId, String order);
}

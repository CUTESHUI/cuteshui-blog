package com.shui.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.shui.entity.MPost;
import com.baomidou.mybatisplus.extension.service.IService;
import com.shui.vo.CommentVo;
import com.shui.vo.PostVo;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author CUTESHUI
 * @since 2020-09-24
 */
public interface MPostService extends IService<MPost> {

    // 获取文章列表
    // 1分页信息、2分类信息(提问 分享...)、3用户信息、4置顶、5精选、6排序
    IPage<PostVo> paging(Page page, Long categoryId, Long userId, Integer level, Boolean recommend, String order);

    PostVo selectOnePost(QueryWrapper<MPost> wrapper);

    // 初始化本周热议
    void initWeekRank();

    // 当天文章新增了评论
    void increaseCommentCountAndUnionForWeekRank(long postId, boolean isIncr);

    // 阅读量
    void updateViewCount(PostVo vo);
}

package com.shui.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.shui.entity.Post;
import com.shui.dto.PostDTO;

/**
 *
 * @author CUTESHUI
 * @since 2020-09-24
 */
public interface PostService extends IService<Post> {

    /**
     * 获取文章列表
     * 1分页信息、2分类信息(提问 分享...)、3用户信息、4置顶、5精选、6排序
     */
    IPage<PostDTO> paging(Page page, Long categoryId, Long userId, Integer level, Boolean recommend, String order);

    /**
     *  初始化本周热议
     */
    void initWeekRank();

    /**
     * 当天文章新增了评论
     */
    void increaseCommentCountAndUnionForWeekRank(long postId, boolean isIncr);

    /**
     * 阅读量
     */
    void updateViewCount(PostDTO dto);

    PostDTO selectOnePost(QueryWrapper<Post> wrapper);
}

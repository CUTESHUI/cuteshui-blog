package com.shui.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.shui.common.lang.Result;
import com.shui.entity.Post;
import com.shui.dto.PostDTO;
import org.springframework.web.bind.annotation.PathVariable;

/**
 *
 * @author CUTESHUI
 * @since 2020-09-24
 */
public interface PostService extends IService<Post> {

    PostDTO selectOnePost(QueryWrapper<Post> wrapper);

    /**
     * 获取文章列表
     * 1分页信息、2分类信息(提问 分享...)、3用户信息、4置顶、5精选、6排序
     */
    IPage<PostDTO> paging(Page page, Long categoryId, Long userId, Integer level, Boolean recommend, String order);

    /**
     * 初始化本周热议
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

    /**
     * 分类页面(提问...)地址
     */
    String category(@PathVariable("id") Long id);

    /**
     * 文章详情地址
     */
    String detail(@PathVariable("id") Long id);

    /**
     * 发布编辑文章地址
     */
    String edit();

    /**
     * 提交文章
     */
    Result submit(Post post);

    /**
     * 删除文章
     */
    Result delete(Long id);

    /**
     * 回复
     */
    Result reply(Long jid, String content);

    /**
     * 删除回复
     */
    Result removeComment(Long id);

}

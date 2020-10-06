package com.shui.service.impl;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.shui.entity.MPost;
import com.shui.mapper.MPostMapper;
import com.shui.service.MPostService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.shui.util.RedisUtil;
import com.shui.vo.PostVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Wrapper;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author CUTESHUI
 * @since 2020-09-24
 */
@Service
public class MPostServiceImpl extends ServiceImpl<MPostMapper, MPost> implements MPostService {

    @Autowired
    MPostMapper mPostMapper;

    @Autowired
    RedisUtil redisUtil;

    @Override
    public IPage<PostVo> paging(Page page, Long categoryId, Long userId, Integer level, Boolean recommend, String order) {

        if (level == null) level = -1;

        QueryWrapper wrapper = new QueryWrapper<MPost>()
                .eq(categoryId != null, "category_id", categoryId)
                .eq(userId != null, "user_id", userId)
                .eq(level == 0, "level", 0)
                .gt(level > 0, "level", 0)
                .orderByDesc(order != null, order); //倒序

        // 1分页信息、2分类信息(提问 分享...)、3用户信息、4置顶、5精选、6排序
        // 返回以上文章信息列表
        return mPostMapper.selectPosts(page, wrapper);
    }

    @Override
    public PostVo selectOnePost(QueryWrapper<MPost> wrapper) {

        return mPostMapper.selectOnePost(wrapper);
    }

    @Override
    public void initWeekRank() {

        // 获取过去7天内发表的文章
        QueryWrapper wrapper = new QueryWrapper<MPost>()
                .ge("created", DateUtil.offsetDay(new Date(), -7)) // 偏移量
                .select("id, title, user_id, comment_count, view_count, created"); // 筛选
        List<MPost> posts = this.list(wrapper);

        // 初始化文章的总阅读量
        for (MPost post : posts) {
            String key = "day:rank:" + DateUtil.format(post.getCreated(), DatePattern.PURE_DATE_FORMAT); // 转换日期为DatePattern.PURE_DATE_FORMAT

            // key：日期， value：文章id，scores：文章评论数
            redisUtil.zSet(key, post.getId(), post.getCommentCount());

            // 7天后自动过期 (15号发表，7-（18-15）=4)
            long between = DateUtil.between(new Date(), post.getCreated(), DateUnit.DAY);
            long expireTime = (7 - between) * 24 * 60 * 60; // 有效时间，秒

            redisUtil.expire(key, expireTime);

            // 缓存文章的一些基本信息（id，标题，评论数量，作者）
            this.postInfo(post, expireTime);
        }

        // 本周每日评论数量，合并操作
        this.zUnionAndStoreLast7DaysForWeekRank();
    }

    /**
     *  做并集
     *  本周每日评论数量，合并操作
     */
    private void zUnionAndStoreLast7DaysForWeekRank() {

        // 当天的
        String  currentKey = "day:rank:" + DateUtil.format(new Date(), DatePattern.PURE_DATE_FORMAT);
        // 并集
        String destKey = "week:rank";
        // 除今天外，前6天
        List<String> otherKeys = new ArrayList<>();
        for(int i=-6; i < 0; i++) {
            String temp = "day:rank:" +
                    DateUtil.format(DateUtil.offsetDay(new Date(), i), DatePattern.PURE_DATE_FORMAT);

            otherKeys.add(temp);
        }
        // Zset的并集，是合并value的scores
        redisUtil.zUnionAndStore(currentKey, otherKeys, destKey);
    }

    /**
     *  缓存文章的一些基本信息
     *  postId，标题，评论数量，访问量
     */
    private void postInfo(MPost post, long expireTime) {
        // rank:post:postId 用于热议的post
        String key = "rank:post:" + post.getId();
        boolean hasKey = redisUtil.hasKey(key);

        if(!hasKey) {
            redisUtil.hset(key, "post:id", post.getId(), expireTime);
            redisUtil.hset(key, "post:title", post.getTitle(), expireTime);
            redisUtil.hset(key, "post:commentCount", post.getCommentCount(), expireTime);
            redisUtil.hset(key, "post:viewCount", post.getViewCount(), expireTime);
        }
    }

    /**
     *  当天文章新增了评论，及时更新
     *  缓存更新后的文章基本信息后，重新做并集合
     */
    @Override
    public void increaseCommentCountAndUnionForWeekRank(long postId, boolean isIncr) {
        // 当天当前文章
        String  currentKey = "day:rank:" + DateUtil.format(new Date(), DatePattern.PURE_DATE_FORMAT);
        redisUtil.zIncrementScore(currentKey, postId, isIncr? 1: -1);

        MPost currentPost = this.getById(postId);

        // 7天后自动过期
        long between = DateUtil.between(new Date(), currentPost.getCreated(), DateUnit.DAY);
        long expireTime = (7 - between) * 24 * 60 * 60; // 有效时间

        // 缓存这篇文章的基本信息
        this.postInfo(currentPost, expireTime);
        // 重新做并集
        this.zUnionAndStoreLast7DaysForWeekRank();
    }

    /**
     *  实时更新阅读量
     *  缓存，数据一致性
     */
    @Override
    public void updateViewCount(PostVo vo) {
        String viewKey = "rank:post:" + vo.getId();

        // 从缓存中获取阅读量
        Integer viewCount = (Integer) redisUtil.hget(viewKey, "post:viewCount");

        // 如果没有，就先从数据库中获取后再加一
        if(viewCount != null) {
            vo.setViewCount(viewCount + 1);
        } else {
            vo.setViewCount(vo.getViewCount() + 1);
        }

        // 同步到缓存里面
        redisUtil.hset(viewKey, "post:viewCount", vo.getViewCount());
    }


}

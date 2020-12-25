package com.shui.service.impl;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.map.MapUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.shui.common.lang.Result;
import com.shui.config.RabbitConfig;
import com.shui.dto.CommentDTO;
import com.shui.entity.Comment;
import com.shui.entity.Post;
import com.shui.entity.User;
import com.shui.entity.UserMessage;
import com.shui.mapper.PostMapper;
import com.shui.search.mq.PostMqIndexMessage;
import com.shui.service.PostService;
import com.shui.util.RedisUtil;
import com.shui.dto.PostDTO;
import com.shui.util.ValidationUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.ServletRequestUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 *
 * @author CUTESHUI
 * @since 2020-09-24
 */
@Service
public class PostServiceImpl extends BaseServiceImpl<PostMapper, Post> implements PostService {

    @Override
    public IPage<PostDTO> paging(Page page, Long categoryId, Long userId, Integer level, Boolean recommend, String order) {

        if (level == null) {
            level = -1;
        }

        QueryWrapper wrapper = new QueryWrapper<Post>()
                .eq(categoryId != null, "category_id", categoryId)
                .eq(userId != null, "user_id", userId)
                .eq(level == 0, "level", 0)
                .gt(level > 0, "level", 0)
                .orderByDesc(order != null, order);

        // 1分页信息、2分类信息(提问 分享...)、3用户信息、4置顶、5精选、6排序
        // 返回以上文章信息列表
        return postMapper.selectPosts(page, wrapper);
    }

    @Override
    public PostDTO selectOnePost(QueryWrapper<Post> wrapper) {

        return postMapper.selectOnePost(wrapper);
    }

    @Override
    public void initWeekRank() {

        // 获取过去7天内发表的文章
        QueryWrapper wrapper = new QueryWrapper<Post>()
                .ge("created", DateUtil.offsetDay(new Date(), -7))
                .select("id, title, user_id, comment_count, view_count, created");
        List<Post> posts = this.list(wrapper);

        // 初始化文章的总阅读量
        for (Post post : posts) {
            String key = "day:rank:" + DateUtil.format(post.getCreated(), DatePattern.PURE_DATE_FORMAT);

            // key：日期， value：文章id，scores：文章评论数
            redisUtil.zSet(key, post.getId(), post.getCommentCount());

            // 7天后自动过期 (15号发表，7-（18-15）=4)
            // 有效时间，秒
            long between = DateUtil.between(new Date(), post.getCreated(), DateUnit.DAY);
            long expireTime = (7 - between) * 24 * 60 * 60;

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
    private void postInfo(Post post, long expireTime) {
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

        Post currentPost = this.getById(postId);

        // 7天后自动过期
        long between = DateUtil.between(new Date(), currentPost.getCreated(), DateUnit.DAY);
        long expireTime = (7 - between) * 24 * 60 * 60;

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
    public void updateViewCount(PostDTO vo) {
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

    @Override
    public String category(Long id) {
        int pn = ServletRequestUtils.getIntParameter(request, "pn", 1);

        // 将每个数据库中对应的 id 赋给 currentCategoryId
        request.setAttribute("currentCategorysId", id);
        request.setAttribute("pn", pn);
        return "post/category";
    }

    @Override
    public String detail(Long id) {
        // 获取无分页信息的，指定id的文章，也就是当前文章
        // postId mapper里
        PostDTO dto = postService.selectOnePost(new QueryWrapper<Post>().eq("p.id", id));
        // 断言vo不为空，当vo为空
        Assert.notNull(dto, "文章已被删除");

        // 评论
        // 获取当前文章的评论列表，需要分页
        // 1分页，评论内容id，评论用户id
        IPage<CommentDTO> results = commentService.paging(commentPage(), dto.getId(), null, "created");

        // 阅读量
        postService.updateViewCount(dto);

        request.setAttribute("currentCategoryId", dto.getCategoryId());
        request.setAttribute("post", dto);
        request.setAttribute("commentData", results);
        return "post/detail";
    }

    @Override
    public String edit() {
        // 获取请求头中参数，是postId
        // post/2 -> post/edit?id=2
        String id = request.getParameter("id");
        // 编辑
        if(!StringUtils.isEmpty(id)) {
            // 当前文章
            Post post = postService.getById(id);
            Assert.notNull(post, "该文章已被删除");
            Assert.isTrue(post.getUserId().longValue() == getProfileId().longValue(), "没权限操作此文章");
            request.setAttribute("post", post);
        }
        // 分类信息
        request.setAttribute("categories", categoryService.list());
        return "post/edit";
    }

    @Override
    public Result submit(Post post) {
        // 验证
        if(ValidationUtil.validateBean(post).hasErrors()) {
            return Result.fail(ValidationUtil.validateBean(post).getErrors());
        }
        // 初始化
        if(null == post.getId()) {
            post.setUserId(getProfileId());
            post.setModified(new Date());
            post.setCreated(new Date());
            post.setCommentCount(0);
            post.setEditMode(null);
            post.setLevel(0);
            post.setRecommend(false);
            post.setViewCount(0);
            post.setVoteDown(0);
            post.setVoteUp(0);
            postService.save(post);
        } else {
            Post tempPost = postService.getById(post.getId());
            Assert.isTrue(tempPost.getUserId().longValue() == getProfileId().longValue(), "无权限编辑此文章！");

            tempPost.setTitle(post.getTitle());
            tempPost.setContent(post.getContent());
            tempPost.setCategoryId(post.getCategoryId());
            postService.updateById(tempPost);
        }

        // 通知消息给mq，告知更新或添加
        amqpTemplate.convertAndSend(
                RabbitConfig.ES_EXCHANGE, RabbitConfig.ES_BIND_KEY,
                new PostMqIndexMessage(post.getId(), PostMqIndexMessage.CREATE_OR_UPDATE)
        );

        return Result.success().action("/post/" + post.getId());
    }

    @Override
    public Result delete(Long id) {
        Post post = postService.getById(id);
        Assert.notNull(post, "该文章已被删除");
        Assert.isTrue(post.getUserId().longValue() == getProfileId().longValue(), "无权限删除此文章！");

        postService.removeById(id);

        // 删除相关消息、收藏
        messageService.removeByMap(MapUtil.of("post_id", id));
        userCollectionService.removeByMap(MapUtil.of("post_id", id));

        amqpTemplate.convertAndSend(RabbitConfig.ES_EXCHANGE, RabbitConfig.ES_BIND_KEY,
                new PostMqIndexMessage(post.getId(), PostMqIndexMessage.REMOVE));

        return Result.success().action("/user/index");
    }

    @Override
    public Result reply(Long jid, String content) {
        Assert.notNull(jid, "找不到对应的文章");
        Assert.hasLength(content, "评论内容不能为空");

        Post post = postService.getById(jid);
        Assert.isTrue(post != null, "该文章已被删除");

        Comment comment = new Comment();
        comment.setPostId(jid);
        comment.setContent(content);
        comment.setUserId(getProfileId());
        comment.setCreated(new Date());
        comment.setModified(new Date());
        comment.setLevel(0);
        comment.setVoteDown(0);
        comment.setVoteUp(0);
        commentService.save(comment);

        // 评论数量加一
        post.setCommentCount(post.getCommentCount() + 1);
        postService.updateById(post);

        // 本周热议数量加一
        postService.increaseCommentCountAndUnionForWeekRank(post.getId(), true);

        // 通知作者，有人评论了你的文章
        // 作者自己评论自己文章，不需要通知
        if(!comment.getUserId().equals(post.getUserId())) {
            UserMessage message = new UserMessage();
            message.setPostId(jid);
            message.setCommentId(comment.getId());
            message.setFromUserId(getProfileId());
            message.setToUserId(post.getUserId());
            message.setType(1);
            message.setContent(content);
            message.setCreated(new Date());
            message.setStatus(0);
            messageService.save(message);

            // 及时通知作者（websocket）
            websocketService.sendMessCountToUser(message.getToUserId());
        }

        // 通知被@的人，有人回复了你的文章
        if(content.startsWith("@")) {
            String username = content.substring(1, content.indexOf(" "));
            System.out.println(username);

            User user = userService.getOne(new QueryWrapper<User>().eq("username", username));
            if(user != null) {
                UserMessage message = new UserMessage();
                message.setPostId(jid);
                message.setCommentId(comment.getId());
                message.setFromUserId(getProfileId());
                message.setToUserId(user.getId());
                message.setType(2);
                message.setContent(content);
                message.setCreated(new Date());
                message.setStatus(0);
                messageService.save(message);

                // 即时通知被@的用户
                websocketService.sendMessCountToUser(message.getToUserId());
            }
        }
        return Result.success().action("/post/" + post.getId());
    }

    @Override
    public Result removeComment(Long id) {
        Assert.notNull(id, "评论id不能为空！");

        Comment comment = commentService.getById(id);
        Assert.notNull(comment, "找不到对应评论！");

        if(comment.getUserId().longValue() != getProfileId().longValue()) {
            return Result.fail("不是你发表的评论！");
        }
        commentService.removeById(id);

        // 评论数量减一
        Post post = postService.getById(comment.getPostId());
        post.setCommentCount(post.getCommentCount() - 1);
        postService.saveOrUpdate(post);

        //评论数量减一
        postService.increaseCommentCountAndUnionForWeekRank(comment.getPostId(), false);
        return Result.success(null);
    }

}

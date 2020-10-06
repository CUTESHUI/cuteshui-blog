package com.shui.controller;

import cn.hutool.core.map.MapUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.shui.common.lang.Result;
import com.shui.config.RabbitConfig;
import com.shui.entity.*;
import com.shui.search.mq.PostMqIndexMessage;
import com.shui.service.WebsocketService;
import com.shui.util.ValidationUtil;
import com.shui.vo.CommentVo;
import com.shui.vo.PostVo;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Date;

@Controller
public class PostController extends BaseController{

    // 分类页面(提问...)
    @GetMapping("/category/{id:\\d*}") // "\\d*" 表示接收的数据类型只能是数字类型
    public String category(@PathVariable("id") Long id) {

        int pn = ServletRequestUtils.getIntParameter(request, "pn", 1);

        // 将每个数据库中对应的 id 赋给 currentCategoryId
        request.setAttribute("currentCategorysId", id);
        request.setAttribute("pn", pn);
        return "post/category";
    }

    /**
     *  文章详情页面
     */
    @GetMapping("/post/{id:\\d*}")
    public String detail(@PathVariable("id") Long id) {

        // 获取无分页信息的，指定id的文章，也就是当前文章
        PostVo vo = mPostService.selectOnePost(new QueryWrapper<MPost>()
                .eq("p.id", id)); // postId mapper里
        // 断言vo不为空，当vo为空
        Assert.notNull(vo, "文章已被删除");

        // 评论
        // 获取当前文章的评论列表，需要分页
        // 1分页，评论内容id，评论用户id
        IPage<CommentVo> results = mCommentService.paging(commentPage(), vo.getId(), null, "created");

        // 阅读量
        mPostService.updateViewCount(vo);

        request.setAttribute("currentCategoryId", vo.getCategoryId());
        request.setAttribute("post", vo);
        request.setAttribute("commentData", results);
        return "post/detail";
    }

    /**
     *  发布编辑文章
     */
    @GetMapping("/post/edit")
    public String edit(){
        // 获取请求头中参数，是postId
        // post/2 -> post/edit?id=2
        String id = request.getParameter("id");
        // 编辑
        if(!StringUtils.isEmpty(id)) {
            // 当前文章
            MPost post = mPostService.getById(id);
            Assert.notNull(post, "该文章已被删除");
            Assert.isTrue(post.getUserId().longValue() == getProfileId().longValue(), "没权限操作此文章");
            request.setAttribute("post", post);
        }
        // 分类信息
        request.setAttribute("categories", mCategoryService.list());
        return "post/edit";
    }

    /**
     *  提交文章
     */
    @ResponseBody
    @PostMapping("/post/submit")
    public Result submit(MPost post) {
        // 表单验证
        ValidationUtil.ValidResult validResult = ValidationUtil.validateBean(post);
        if(validResult.hasErrors()) {
            return Result.fail(validResult.getErrors());
        }
        // 初始化并保存
        if(post.getId() == null) {
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
            mPostService.save(post);

        } else {
            MPost tempPost = mPostService.getById(post.getId());
            Assert.isTrue(tempPost.getUserId().longValue() == getProfileId().longValue(), "无权限编辑此文章！");

            tempPost.setTitle(post.getTitle());
            tempPost.setContent(post.getContent());
            tempPost.setCategoryId(post.getCategoryId());
            // 更新到数据库
            mPostService.updateById(tempPost);
        }

         // 通知消息给mq，告知更新或添加
        amqpTemplate.convertAndSend(
                RabbitConfig.es_exchange,
                RabbitConfig.es_bind_key,
                new PostMqIndexMessage(post.getId(), PostMqIndexMessage.CREATE_OR_UPDATE)
        );

        return Result.success().action("/post/" + post.getId());
    }

    /**
     *  删除文章
     */
    @ResponseBody
    @Transactional
    @PostMapping("/post/delete")
    public Result delete(Long id) {

        MPost post = mPostService.getById(id);
        Assert.notNull(post, "该文章已被删除");
        Assert.isTrue(post.getUserId().longValue() == getProfileId().longValue(), "无权限删除此文章！");

        mPostService.removeById(id);

        // 删除相关消息、收藏
        messageService.removeByMap(MapUtil.of("post_id", id));
        mUserCollectionService.removeByMap(MapUtil.of("post_id", id));

        //
        amqpTemplate.convertAndSend(RabbitConfig.es_exchange, RabbitConfig.es_bind_key,
                new PostMqIndexMessage(post.getId(), PostMqIndexMessage.REMOVE));

        return Result.success().action("/user/index");


    }

    /**
     *  回复
     */
    @ResponseBody
    @Transactional
    @PostMapping("/post/reply/")
    public Result reply(Long jid, String content) {
        Assert.notNull(jid, "找不到对应的文章");
        Assert.hasLength(content, "评论内容不能为空");

        MPost post = mPostService.getById(jid);
        Assert.isTrue(post != null, "该文章已被删除");

        MComment comment = new MComment();
        comment.setPostId(jid);
        comment.setContent(content);
        comment.setUserId(getProfileId());
        comment.setCreated(new Date());
        comment.setModified(new Date());
        comment.setLevel(0);
        comment.setVoteDown(0);
        comment.setVoteUp(0);
        // 插入到数据库
        mCommentService.save(comment);

        // 评论数量加一
        post.setCommentCount(post.getCommentCount() + 1);
        mPostService.updateById(post);

        // 本周热议数量加一
        mPostService.increaseCommentCountAndUnionForWeekRank(post.getId(), true);

        // 通知作者，有人评论了你的文章
        // 作者自己评论自己文章，不需要通知
        if(comment.getUserId() != post.getUserId()) {
            MUserMessage message = new MUserMessage();
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

            MUser user = mUserService.getOne(new QueryWrapper<MUser>().eq("username", username));
            if(user != null) {
                MUserMessage message = new MUserMessage();
                message.setPostId(jid);
                message.setCommentId(comment.getId());
                message.setFromUserId(getProfileId());
                message.setToUserId(user.getId());
                message.setType(2);
                message.setContent(content);
                message.setCreated(new Date());
                message.setStatus(0); // 未读
                messageService.save(message);

                // 即时通知被@的用户
                websocketService.sendMessCountToUser(message.getToUserId());
            }
        }
        return Result.success().action("/post/" + post.getId());
    }

    /**
     *  删除回复
     */
    @ResponseBody
    @Transactional
    @PostMapping("/post/jieda-delete/")
    public Result reply(Long id) {

        Assert.notNull(id, "评论id不能为空！");

        MComment comment = mCommentService.getById(id);

        Assert.notNull(comment, "找不到对应评论！");

        if(comment.getUserId().longValue() != getProfileId().longValue()) {
            return Result.fail("不是你发表的评论！");
        }
        mCommentService.removeById(id);

        // 评论数量减一
        MPost post = mPostService.getById(comment.getPostId());
        post.setCommentCount(post.getCommentCount() - 1);
        mPostService.saveOrUpdate(post);

        //评论数量减一
        mPostService.increaseCommentCountAndUnionForWeekRank(comment.getPostId(), false);

        return Result.success(null);
    }



}

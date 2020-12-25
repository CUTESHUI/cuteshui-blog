package com.shui.controller;

import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.shui.common.lang.Result;
import com.shui.entity.Post;
import com.shui.entity.User;
import com.shui.entity.UserCollection;
import com.shui.entity.UserMessage;
import com.shui.shiro.AccountProfile;
import com.shui.util.UploadUtil;
import com.shui.util.ValidationUtil;
import com.shui.dto.UserMessageDTO;
import org.apache.shiro.SecurityUtils;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 用户相关
 * @author CUTESHUI
 * @since 2020-09-24
 */
@Controller
public class UserController extends BaseController {

    @GetMapping("/user/index")
    public String index() {
        return "/user/index";
    }

    /**
     *  用户中心
     */
    @GetMapping("/user/home")
    public String home() {
        User user = userService.getById(getProfileId());

        List<Post> posts = postService.list(new QueryWrapper<Post>()
                .eq("user_id", getProfileId())
                // 30天内
                //.gt("created", DateUtil.offsetDay(new Date(), -30))
                .orderByDesc("created")
        );

        request.setAttribute("user", user);
        request.setAttribute("posts", posts);
        return "user/home";
    }

    @GetMapping("/user/set")
    public String set() {
        User user = userService.getById(getProfileId());
        request.setAttribute("user", user);
        return "user/set";
    }

    /**
     *  基本设置，我的资料编辑
     */
    @ResponseBody
    @PostMapping("/user/set")
    public Result doSet(User user) {
        // 校验
        if(ValidationUtil.validateBean(user).hasErrors()) {
            return Result.fail(ValidationUtil.validateBean(user).getErrors());
        }

        if(StrUtil.isNotBlank(user.getAvatar())) {

            User temp = userService.getById(getProfileId());
            temp.setAvatar(user.getAvatar());
            // 当前用户的头像更新到数据库
            userService.updateById(temp);

            AccountProfile profile = getProfile();
            profile.setAvatar(user.getAvatar());

            SecurityUtils.getSubject().getSession().setAttribute("profile", profile);

            return Result.success().action("/user/set#avatar");
        }
        // 改昵称前，首先判断昵称：是否为空、昵称在数据库中是否有了( 数量>1 )
        if(StrUtil.isBlank(user.getUsername())) {
            return Result.fail("昵称不能为空");
        }
        int count = userService.count(new QueryWrapper<User>()
                .eq("username", getProfile().getUsername())
                .ne("id", getProfileId()));
        if(count > 0) {
            return Result.fail("改昵称已被占用");
        }
        // 更新昵称
        User temp = userService.getById(getProfileId());
        temp.setUsername(user.getUsername());
        temp.setGender(user.getGender());
        temp.setSign(user.getSign());
        userService.updateById(temp);

        AccountProfile profile = getProfile();
        profile.setUsername(temp.getUsername());
        profile.setSign(temp.getSign());
        SecurityUtils.getSubject().getSession().setAttribute("profile", profile);

        return Result.success().action("/user/set#info");
    }

    @ResponseBody
    @PostMapping("/user/upload")
    public Result uploadAvatar(@RequestParam(value = "file") MultipartFile file) throws IOException {
        return uploadUtil.upload(UploadUtil.TYPE_AVATAR, file);
    }

    /**
     *  重置密码
     */
    @ResponseBody
    @PostMapping("/user/repass")
    public Result repass(String nowPass, String pass, String prePass) {
        if(!pass.equals(prePass)) {
            return Result.fail("两次密码不相同");
        }

        User user = userService.getById(getProfileId());

        String nowPassMd5 = SecureUtil.md5(nowPass);
        if(!nowPassMd5.equals(user.getPassword())) {
            return Result.fail("密码不正确");
        }

        user.setPassword(SecureUtil.md5(pass));
        userService.updateById(user);

        return Result.success().action("/user/set#pass");
    }

    /**
     *  我发布的文章
     */
    @ResponseBody
    @GetMapping("/user/public")
    public Result userP() {
        IPage page = postService.page(getPage(), new QueryWrapper<Post>()
                .eq("user_id", getProfileId())
                .orderByDesc("created"));
        return Result.success(page);
    }

    /**
     *  我收藏的文章
     */
    @ResponseBody
    @GetMapping("/user/collection")
    public Result collection() {
        IPage page = postService.page(getPage(), new QueryWrapper<Post>()
                .inSql("id", "SELECT post_id FROM m_user_collection where user_id = " + getProfileId())
        );
        return Result.success(page);
    }

    /**
     *  判断用户是否收藏了文章
     */
    @ResponseBody
    @PostMapping("/collection/find/")
    public Result collectionFind(Long pid) {
        int count = userCollectionService.count(new QueryWrapper<UserCollection>()
                .eq("user_id", getProfileId())
                .eq("post_id", pid)
        );
        // 查收藏表的记录，count > 0：有记录 收藏了
        return Result.success(MapUtil.of("collection", count > 0 ));
    }

    /**
     *  添加收藏
     */
    @ResponseBody
    @PostMapping("/collection/add/")
    public Result collectionAdd(Long pid) {
        Post post = postService.getById(pid);
        Assert.isTrue(post != null, "该文章已被删除");

        int count = userCollectionService.count(new QueryWrapper<UserCollection>()
                .eq("user_id", getProfileId())
                .eq("post_id", pid)
        );
        if(count > 0) {
            return Result.fail("你已经收藏");
        }

        UserCollection collection = new UserCollection();
        collection.setUserId(getProfileId());
        collection.setPostId(pid);
        collection.setCreated(new Date());
        collection.setModified(new Date());

        collection.setPostUserId(post.getUserId());

        userCollectionService.save(collection);
        return Result.success();
    }

    /**
     *  取消收藏
     */
    @ResponseBody
    @PostMapping("/collection/remove/")
    public Result collectionRemove(Long pid) {
        Post post = postService.getById(pid);
        Assert.isTrue(post != null, "该帖子已被删除");

        userCollectionService.remove(new QueryWrapper<UserCollection>()
                .eq("user_id", getProfileId())
                .eq("post_id", pid));

        return Result.success();
    }

    @GetMapping("/user/mess")
    public String mess() {

        IPage<UserMessageDTO> page = messageService.paging(getPage(), new QueryWrapper<UserMessage>()
                .eq("to_user_id", getProfileId())
                .orderByDesc("created")
        );

        // 把消息改成已读状态
        List<Long> ids = new ArrayList<>();
        for(UserMessageDTO dto : page.getRecords()) {
            if(dto.getStatus() == 0) {
                ids.add(dto.getId());
            }
        }
        messageService.updateToReaded(ids);

        request.setAttribute("pageData", page);
        return "user/mess";
    }

    /**
     * 删除消息
     */
    @ResponseBody
    @PostMapping("/message/remove/")
    public Result msgRemove(Long id, @RequestParam(defaultValue = "false") Boolean all) {

        boolean remove = messageService.remove(new QueryWrapper<UserMessage>()
                .eq("to_user_id", getProfileId())
                .eq(!all, "id", id));

        return remove ? Result.success() : Result.fail("删除失败");
    }

    /**
     * 未读消息数量
     */
    @ResponseBody
    @RequestMapping("/message/nums/")
    public Map msgNums() {
        int count = messageService.count(new QueryWrapper<UserMessage>()
                .eq("to_user_id", getProfileId())
                .eq("status", "0")
        );
        return MapUtil.builder("status", 0)
                .put("count", count).build();
    }

}

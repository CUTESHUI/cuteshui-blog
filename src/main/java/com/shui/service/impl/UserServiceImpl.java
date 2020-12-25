package com.shui.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.shui.common.lang.Result;
import com.shui.dto.UserMessageDTO;
import com.shui.entity.Post;
import com.shui.entity.User;
import com.shui.entity.UserCollection;
import com.shui.entity.UserMessage;
import com.shui.mapper.UserMapper;
import com.shui.service.UserService;
import com.shui.shiro.AccountProfile;
import com.shui.util.UploadUtil;
import com.shui.util.ValidationUtil;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.UnknownAccountException;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 *
 * @author CUTESHUI
 * @since 2020-09-24
 */
@Service
public class UserServiceImpl extends BaseServiceImpl<UserMapper, User> implements UserService {

    @Override
    public Result register(User user) {
        // 邮箱
        int count = this.count(new QueryWrapper<User>()
                .eq("email", user.getEmail())
                .or()
                .eq("username", user.getUsername())
        );
        if(count > 0) {
            return Result.fail("用户名或邮箱已被占用");
        }

        User temp = new User();
        temp.setUsername(user.getUsername());
        temp.setPassword(SecureUtil.md5(user.getPassword()));
        temp.setEmail(user.getEmail());
        temp.setAvatar("/res/images/avatar/default.png");

        temp.setCreated(new Date());
        temp.setPoint(0);
        temp.setVipLevel(0);
        temp.setCommentCount(0);
        temp.setPostCount(0);
        temp.setGender("0");
        this.save(temp);

        return Result.success();
    }

    /**
     *  登录
     */
    @Override
    public AccountProfile login(String email, String password) {
        // 获取一个用户信息
        User user = this.getOne(new QueryWrapper<User>()
                .eq("email", email));

        if(user == null) {
            throw new UnknownAccountException();
        }
        if(!user.getPassword().equals(password)){
            throw new IncorrectCredentialsException();
        }

        user.setLasted(new Date());
        this.updateById(user);

        AccountProfile profile = new AccountProfile();
        BeanUtil.copyProperties(user, profile);

        return profile;
    }

    @Override
    public String home() {
        User user = userService.getById(getProfileId());

        List<Post> posts = postService.list(new QueryWrapper<Post>()
                .eq("user_id", getProfileId())
                .orderByDesc("created")
        );

        request.setAttribute("user", user);
        request.setAttribute("posts", posts);
        return "user/home";
    }

    @Override
    public String set() {
        User user = userService.getById(getProfileId());
        request.setAttribute("user", user);
        return "user/set";
    }

    @Override
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

    @Override
    public void uploadAvatar(MultipartFile file) {
        try {
            uploadUtil.upload(UploadUtil.TYPE_AVATAR, file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
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

    @Override
    public Result userPost() {
        IPage page = postService.page(getPage(), new QueryWrapper<Post>()
                .eq("user_id", getProfileId())
                .orderByDesc("created"));
        return Result.success(page);
    }

    @Override
    public Result collection() {
        IPage page = postService.page(getPage(), new QueryWrapper<Post>()
                .inSql("id", "SELECT post_id FROM m_user_collection where user_id = " + getProfileId())
        );
        return Result.success(page);
    }

    @Override
    public Result collectionFind(Long pid) {
        int count = userCollectionService.count(new QueryWrapper<UserCollection>()
                .eq("user_id", getProfileId())
                .eq("post_id", pid)
        );
        // 查收藏表的记录，count > 0：有记录 收藏了
        return Result.success(MapUtil.of("collection", count > 0 ));
    }

    @Override
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

    @Override
    public Result collectionRemove(Long pid) {
        Post post = postService.getById(pid);
        Assert.isTrue(post != null, "该帖子已被删除");

        userCollectionService.remove(new QueryWrapper<UserCollection>()
                .eq("user_id", getProfileId())
                .eq("post_id", pid));

        return Result.success();
    }

    @Override
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

    @Override
    public Result msgRemove(Long id, Boolean all) {
        boolean remove = messageService.remove(new QueryWrapper<UserMessage>()
                .eq("to_user_id", getProfileId())
                .eq(!all, "id", id));

        return remove ? Result.success() : Result.fail("删除失败");
    }

    @Override
    public Map msgNums() {
        int count = messageService.count(new QueryWrapper<UserMessage>()
                .eq("to_user_id", getProfileId())
                .eq("status", "0")
        );
        return MapUtil.builder("status", 0).put("count", count).build();
    }


}

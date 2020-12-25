package com.shui.service;

import com.shui.common.lang.Result;
import com.shui.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;
import com.shui.shiro.AccountProfile;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

/**
 *
 * @author CUTESHUI
 * @since 2020-09-24
 */
public interface UserService extends IService<User> {

    /**
     * 用户注册
     */
    Result register(User user);

    /**
     * 用户登录
     */
    AccountProfile login(String email, String password);

    /**
     *  用户中心地址
     */
    String home();

    /**
     * 基本设置地址
     */
    String set();

    /**
     *  处理设置
     */
    Result doSet(User user);

    /**
     * 上传头像
     */
    void uploadAvatar(@RequestParam(value = "file") MultipartFile file);

    /**
     *  重置密码
     */
    Result repass(String nowPass, String pass, String prePass);

    /**
     *  我发布的文章
     */
    Result userPost();

    /**
     *  我收藏的文章
     */
    Result collection();

    /**
     *  判断用户是否收藏了文章
     */
    Result collectionFind(Long pid);

    /**
     *  添加收藏
     */
    Result collectionAdd(Long pid);

    /**
     *  取消收藏
     */
    Result collectionRemove(Long pid);

    /**
     * 消息地址
     */
    String mess();

    /**
     * 删除消息
     */
    Result msgRemove(Long id, @RequestParam(defaultValue = "false") Boolean all);

    /**
     * 未读消息数量
     */
    Map msgNums();
}

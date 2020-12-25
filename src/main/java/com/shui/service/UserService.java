package com.shui.service;

import com.shui.common.lang.Result;
import com.shui.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;
import com.shui.shiro.AccountProfile;

/**
 *
 * @author CUTESHUI
 * @since 2020-09-24
 */
public interface UserService extends IService<User> {

    Result register(User user);

    AccountProfile login(String email, String password);
}

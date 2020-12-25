package com.shui.service;

import com.shui.common.lang.Result;
import com.shui.entity.User;

import javax.servlet.http.HttpServletResponse;

public interface AuthService {

    /**
     *  通用图片验证码
     */
    void kaptcha(HttpServletResponse resp);

    /**
     * 处理登录请求
     */
    Result doLogin(String email, String password);

    /**
     * 处理注册请求
     */
    Result doRegister(User user, String repass, String vercode);
}

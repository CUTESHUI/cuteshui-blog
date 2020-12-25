package com.shui.service.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import com.shui.common.lang.Result;
import com.shui.entity.User;
import com.shui.service.AuthService;
import com.shui.util.ValidationUtil;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.*;
import org.springframework.stereotype.Service;
import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.IOException;

@Service
public class AuthServiceImpl extends BaseServiceImpl implements AuthService {

    private static final String KAPTCHA_SESSION_KEY = "KAPTCHA_SESSION_KEY";

    @Override
    public void kaptcha(HttpServletResponse resp) {
        // 验证码文本、图片
        String text = producer.createText();
        BufferedImage image = producer.createImage(text);

        request.getSession().setAttribute(KAPTCHA_SESSION_KEY, text);
        resp.setHeader("Cache-Control", "no-store, no-cache");
        resp.setContentType("image/jpeg");

        try {
            ServletOutputStream outputStream = resp.getOutputStream();
            ImageIO.write(image, "jpg", outputStream);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public Result doLogin(String email, String password) {
        if(StrUtil.isEmpty(email) || StrUtil.isBlank(password)) {
            return Result.fail("邮箱或密码不能为空");
        }
        // 令牌
        UsernamePasswordToken token = new UsernamePasswordToken(email, SecureUtil.md5(password));
        try {
            SecurityUtils.getSubject().login(token);

        } catch (AuthenticationException e) {
            if (e instanceof UnknownAccountException) {
                return Result.fail("用户不存在");
            } else if (e instanceof LockedAccountException) {
                return Result.fail("用户被禁用");
            } else if (e instanceof IncorrectCredentialsException) {
                return Result.fail("密码错误");
            } else {
                return Result.fail("用户认证失败");
            }
        }

        return Result.success().action("/");
    }

    @Override
    public Result doRegister(User user, String repass, String vercode) {
        // 校验
        if(ValidationUtil.validateBean(user).hasErrors()) {
            return Result.fail(ValidationUtil.validateBean(user).getErrors());
        }

        if(!user.getPassword().equals(repass)) {
            return Result.fail("两次输入密码不相同");
        }

        String capthca = (String) request.getSession().getAttribute(KAPTCHA_SESSION_KEY);
        System.out.println(capthca);
        if(vercode == null || !vercode.equalsIgnoreCase(capthca)) {
            return Result.fail("验证码输入不正确");
        }

        // 完成注册
        Result result = userService.register(user);
        return result.action("/login");
    }


}

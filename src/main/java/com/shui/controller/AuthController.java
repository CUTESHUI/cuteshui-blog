package com.shui.controller;

import com.shui.common.lang.Result;
import com.shui.entity.User;
import com.shui.service.AuthService;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;

@Controller
public class AuthController {

    @Autowired
    private AuthService authService;

    @GetMapping("/capthca.jpg")
    public void kaptcha(HttpServletResponse resp) {
        authService.kaptcha(resp);
    }

    @GetMapping("/login")
    public String login() {
        return "auth/login";
    }

    @ResponseBody
    @PostMapping("/login")
    public Result doLogin(String email, String password) {
       return authService.doLogin(email, password);
    }

    @GetMapping("/register")
    public String register() {
        return "auth/reg";
    }

    @ResponseBody
    @PostMapping("/register")
    public Result doRegister(User user, String repass, String vercode) {
        return authService.doRegister(user, repass, vercode);
    }

    @RequestMapping("/user/logout")
    public String logout() {
        SecurityUtils.getSubject().logout();
        return "redirect:/";
    }

}

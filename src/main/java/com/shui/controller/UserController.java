package com.shui.controller;

import com.shui.common.lang.Result;
import com.shui.entity.User;
import com.shui.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

/**
 * 用户相关
 * @author CUTESHUI
 * @since 2020-09-24
 */
@Controller
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/user/index")
    public String index() {
        return "/user/index";
    }

    @GetMapping("/user/home")
    public String home() {
        return userService.home();
    }

    @GetMapping("/user/set")
    public String set() {
        return userService.set();
    }

    @ResponseBody
    @PostMapping("/user/set")
    public Result doSet(User user) {
        return userService.doSet(user);
    }

    @ResponseBody
    @PostMapping("/user/upload")
    public void uploadAvatar(@RequestParam(value = "file") MultipartFile file) {
        userService.uploadAvatar(file);
    }

    @ResponseBody
    @PostMapping("/user/repass")
    public Result repass(String nowPass, String pass, String prePass) {
        return userService.repass(nowPass, pass, prePass);
    }

    @ResponseBody
    @GetMapping("/user/public")
    public Result userPost() {
       return userService.userPost();
    }

    @ResponseBody
    @GetMapping("/user/collection")
    public Result collection() {
        return userService.collection();
    }

    @ResponseBody
    @PostMapping("/collection/find/")
    public Result collectionFind(Long pid) {
        return userService.collectionFind(pid);
    }

    @ResponseBody
    @PostMapping("/collection/add/")
    public Result collectionAdd(Long pid) {
        return userService.collectionAdd(pid);
    }

    @ResponseBody
    @PostMapping("/collection/remove/")
    public Result collectionRemove(Long pid) {
        return userService.collectionRemove(pid);
    }

    @GetMapping("/user/mess")
    public String mess() {
        return userService.mess();
    }

    @ResponseBody
    @PostMapping("/message/remove/")
    public Result msgRemove(Long id, @RequestParam(defaultValue = "false") Boolean all) {
        return userService.msgRemove(id, all);
    }

    @ResponseBody
    @RequestMapping("/message/nums/")
    public Map msgNums() {
        return userService.msgNums();
    }

}

package com.shui.controller;

import com.shui.common.lang.Result;
import com.shui.entity.Post;
import com.shui.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 文章相关
 * @author CUTESHUI
 * @since 2020-09-24
 */
@Controller
public class PostController {

    @Autowired
    private PostService postService;

    @GetMapping("/category/{id:\\d*}") // "\\d*" 表示接收的数据类型只能是数字类型
    public String category(@PathVariable("id") Long id) {
        return postService.category(id);
    }

    @GetMapping("/post/{id:\\d*}")
    public String detail(@PathVariable("id") Long id) {
        return postService.detail(id);
    }

    @GetMapping("/post/edit")
    public String edit(){
        return postService.edit();
    }

    @ResponseBody
    @PostMapping("/post/submit")
    public Result submit(Post post) {
        return postService.submit(post);
    }

    @ResponseBody
    @Transactional
    @PostMapping("/post/delete")
    public Result delete(Long id) {
        return postService.delete(id);
    }

    @ResponseBody
    @Transactional
    @PostMapping("/post/reply/")
    public Result reply(Long jid, String content) {
        return postService.reply(jid, content);
    }

    @ResponseBody
    @Transactional
    @PostMapping("/post/jieda-delete/")
    public Result removeComment(Long id) {
        return postService.removeComment(id);
    }

}

package com.shui.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.shui.service.*;
import com.shui.shiro.AccountProfile;
import com.shui.util.UploadUtil;
import org.apache.shiro.SecurityUtils;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.ServletRequestUtils;

import javax.servlet.http.HttpServletRequest;

/**
 * 基础Controller，所有Controller必须继承
 * @author CUTESHUI
 * @since 2020-09-24
 */
public class BaseController {

    @Autowired
    HttpServletRequest request;
    @Autowired
    PostService postService;
    @Autowired
    CommentService commentService;
    @Autowired
    UserService userService;
    @Autowired
    UserMessageService messageService;
    @Autowired
    UserCollectionService userCollectionService;
    @Autowired
    CategoryService categoryService;
    @Autowired
    AmqpTemplate amqpTemplate;
    @Autowired
    WebsocketService websocketService;
    @Autowired
    SearchService searchService;
    @Autowired
    ChatService chatService;
    @Autowired
    UploadUtil uploadUtil;

    /**
     * 分页信息
     * pn：当前页面 page number
     * size：当前页面要查多少条数据
     */
    public Page getPage() {
        int pn = ServletRequestUtils.getIntParameter(request, "pn", 1);
        int size = ServletRequestUtils.getIntParameter(request, "size", 5);
        return new Page(pn,size);
    }

    public Page commentPage() {
        int pn = ServletRequestUtils.getIntParameter(request, "pn", 1);
        int size = ServletRequestUtils.getIntParameter(request, "size", 3);
        return new Page(pn,size);
    }

    /**
     * 当前登录用户
     */
    public AccountProfile getProfile() {
        return (AccountProfile) SecurityUtils.getSubject().getPrincipal();
    }

    public Long getProfileId() {
        return getProfile().getId();
    }

}

package com.shui.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.shui.service.*;
import com.shui.shiro.AccountProfile;
import org.apache.shiro.SecurityUtils;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.ServletRequestUtils;

import javax.servlet.http.HttpServletRequest;

public class BaseController {

    @Autowired
    HttpServletRequest request;

    @Autowired
    MPostService mPostService;

    @Autowired
    MCommentService mCommentService;

    @Autowired
    MUserService mUserService;

    @Autowired
    MUserMessageService messageService;

    @Autowired
    MUserCollectionService mUserCollectionService;

    @Autowired
    MCategoryService mCategoryService;

    @Autowired
    AmqpTemplate amqpTemplate;

    @Autowired
    WebsocketService websocketService;

    @Autowired
    SearchService searchService;

    // 分页信息
    public Page getPage() {
        // pn：当前页面 page number
        // size：当前页面要查多少条数据
        int pn = ServletRequestUtils.getIntParameter(request, "pn", 1);
        int size = ServletRequestUtils.getIntParameter(request, "size", 5);
        return new Page(pn,size);
    }

    public Page commentPage() {
        // pn：当前页面 page number
        // size：当前页面要查多少条数据
        int pn = ServletRequestUtils.getIntParameter(request, "pn", 1);
        int size = ServletRequestUtils.getIntParameter(request, "size", 3);
        return new Page(pn,size);
    }

    // 当前登录用户
    protected AccountProfile getProfile() {
        return (AccountProfile) SecurityUtils.getSubject().getPrincipal();
    }

    protected Long getProfileId() {
        return getProfile().getId();
    }

}

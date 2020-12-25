package com.shui.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 首页、默认页相关
 * @author CUTESHUI
 * @since 2020-09-24
 */
@Controller
public class indexController extends BaseController{

    @RequestMapping({"", "/", "index"})
    public String index() {
        // 获取当前文章列表
        // 1分页信息、2分类信息、3用户、4置顶、5精选、6排序
        IPage results = postService.paging(getPage(), null, null, null, null, "created");

        request.setAttribute("pageData", results);

        // 默认 CurrentCategoryId 设为 0
        request.setAttribute("CurrentCategoryId", 0);
        return "index";
    }

    /**
     *  ES的搜索功能
     *  q：关键字
     */
    @RequestMapping("/search")
    public String search(String q) {
        IPage searchData = searchService.search(getPage(), q);

        request.setAttribute("q", q);
        request.setAttribute("searchData", searchData);
        return "search";
    }
}

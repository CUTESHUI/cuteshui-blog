package com.shui.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.shui.service.IndexService;
import org.springframework.stereotype.Service;

@Service
public class IndexServiceImpl extends BaseServiceImpl implements IndexService {

    @Override
    public String index() {
        // 获取当前文章列表
        // 1分页信息、2分类信息、3用户、4置顶、5精选、6排序
        IPage results = postService.paging(getPage(), null, null, null, null, "created");

        request.setAttribute("pageData", results);

        // 默认 CurrentCategoryId 设为 0
        request.setAttribute("CurrentCategoryId", 0);
        return "index";
    }

    @Override
    public String search(String q) {
        IPage searchData = searchService.search(getPage(), q);

        request.setAttribute("q", q);
        request.setAttribute("searchData", searchData);
        return "search";
    }
}

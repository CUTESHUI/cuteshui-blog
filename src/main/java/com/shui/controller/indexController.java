package com.shui.controller;

import com.shui.service.IndexService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 首页、默认页相关
 * @author CUTESHUI
 * @since 2020-09-24
 */
@Controller
public class indexController {

    @Autowired
    private IndexService indexService;

    @RequestMapping({"", "/", "index"})
    public String index() {
        return indexService.index();
    }

    @RequestMapping("/search")
    public String search(String q) {
        return indexService.search(q);
    }
}

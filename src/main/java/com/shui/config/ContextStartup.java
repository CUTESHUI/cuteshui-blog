package com.shui.config;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.shui.entity.Category;
import com.shui.service.CategoryService;
import com.shui.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.web.context.ServletContextAware;

import javax.servlet.ServletContext;
import java.util.List;

/**
 * 项目启动时就会调用run里的内容，也就可以自定义，注入数据库中的信息
 */
@Component
public class ContextStartup implements ApplicationRunner, ServletContextAware {

    @Autowired
    private CategoryService categoryService;
    @Autowired
    private ServletContext servletContext;
    @Autowired
    PostService postService;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        // 0 表示上线
        List<Category> categories = categoryService.list(new QueryWrapper<Category>()
                .eq("status", 0));
        servletContext.setAttribute("categorys", categories);

        // 初始化 本周热议
        postService.initWeekRank();
    }

    @Override
    public void setServletContext(ServletContext servletContext) {
        this.servletContext = servletContext;
    }
}

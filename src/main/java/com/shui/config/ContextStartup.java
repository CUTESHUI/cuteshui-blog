package com.shui.config;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.shui.entity.MCategory;
import com.shui.service.MCategoryService;
import com.shui.service.MPostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.web.context.ServletContextAware;

import javax.servlet.ServletContext;
import java.util.List;

/**
 *  项目启动时就会调用run里的内容，也就可以自定义，注入数据库中的信息
 */
@Component
public class ContextStartup implements ApplicationRunner, ServletContextAware {

    @Autowired
    MCategoryService mCategoryService;
    ServletContext servletContext;

    @Autowired
    MPostService mPostService;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        List<MCategory> categories = mCategoryService.list(new QueryWrapper<MCategory>()
                .eq("status", 0)); // 0 表示上线
        servletContext.setAttribute("categorys", categories);

        // 初始化 本周热议
        mPostService.initWeekRank();
    }

    @Override
    public void setServletContext(ServletContext servletContext) {
        this.servletContext = servletContext;
    }
}

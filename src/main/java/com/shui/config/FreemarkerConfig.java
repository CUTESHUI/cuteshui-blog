package com.shui.config;

import com.shui.template.PostsTemplate;
import com.shui.template.TimeAgoMethod;
import com.shui.template.WeekRankTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import com.jagregory.shiro.freemarker.ShiroTags;

import javax.annotation.PostConstruct;

@Configuration
public class FreemarkerConfig {

    @Autowired
    private freemarker.template.Configuration configuration;
    @Autowired
    private PostsTemplate postsTemplate;
    @Autowired
    private WeekRankTemplate weekRankTemplate;

    @PostConstruct
    public void setUp() {
        // 将timeAgo函数和一些模版...注入到freemarker配置中，这样就能用 <@timeAgo> <@posts> <@weekrankw>
        configuration.setSharedVariable("timeAgo", new TimeAgoMethod());
        configuration.setSharedVariable("posts", postsTemplate);
        configuration.setSharedVariable("weekrank", weekRankTemplate);
        configuration.setSharedVariable("shiro", new ShiroTags());
    }

}

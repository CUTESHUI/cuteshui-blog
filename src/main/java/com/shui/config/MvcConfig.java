package com.shui.config;

import com.shui.common.lang.Consts;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class MvcConfig implements WebMvcConfigurer {

    @Autowired
    Consts consts;
    @Bean
    ModelMapper modelMapper() {
        return new ModelMapper();
    }

    /**
     *  自定义资源映射
     *
     *  addResourceHandler：对外暴露的访问路径
     *  addResourceLocations：文件放置的目录
     *
     *      默认映射的文件夹有：
     *
     *      META-INF/resources/
     *      resources/
     *      static/
     *      public/
     *
     *      优先级顺序为：META-INF/resources > resources > static > public
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/upload/avatar/**")
                .addResourceLocations("file:///" + consts.getUploadDir() + "/avatar/");
    }

}

package com.shui.template;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.shui.common.templates.DirectiveHandler;
import com.shui.common.templates.TemplateDirective;
import com.shui.service.MPostService;
import com.shui.vo.PostVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PostsTemplate extends TemplateDirective {

    @Autowired
    MPostService mPostService;

    /**
     *  标签名字
     */
    @Override
    public String getName() {
        return"posts";
    }

    @Override
    public void execute(DirectiveHandler handler) throws Exception {
        // 可以在设置值
        // 例如 <@posts size=3 level=1>：level=1就让这个模版变为置顶
        //     <@posts categoryId=currentCategoryId pn=pn size=2>
        //     categoryId=currentCategoryId 就根据categoryId显示对应的文章页面
        Integer level = handler.getInteger("level");
        Integer pn = handler.getInteger("pn", 1);
        Integer size = handler.getInteger("size", 2);
        Long categoryId = handler.getLong("categoryId");

        IPage<PostVo> pages = mPostService.paging(new Page(pn, size), categoryId, null, level, null, "created");

        // RESULTS：返回值名称
        // pages: 返回结果
        //      列表
        //      protected static String RESULTS = "results";
        handler.put( RESULTS, pages).render();


    }
}

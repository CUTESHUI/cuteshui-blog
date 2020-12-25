package com.shui.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.shui.common.lang.Result;
import com.shui.dto.PostDTO;
import com.shui.entity.Post;
import com.shui.service.AdminService;
import com.shui.service.PostService;
import com.shui.service.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

@Service
public class AdminServiceImpl extends BaseServiceImpl implements AdminService {

    @Override
    public Result initEsData() {
        Page page = new Page();
        // 每页10000条
        int size = 10000;
        page.setSize(size);

        long total = 0;

        // 数据量大的时候要分页
        for (int i = 1; i < 1000; i ++) {
            // 第i页
            page.setCurrent(i);

            IPage<PostDTO> paging = postService.paging(page, null, null, null, null, null);
            // es初始化了多少页
            int num = searchService.initEsData(paging.getRecords());

            total += num;

            // 当一页查不出10000条的时候，说明是最后一页了
            if(paging.getRecords().size() < size) {
                break;
            }
        }

        return Result.success("ES索引初始化成功，共 " + total + " 条记录！", null);
    }

    @Override
    public Result jetSet(Long id, Integer rank, String field) {
        Post post = postService.getById(id);
        Assert.notNull(post, "该帖子已被删除");

        if("delete".equals(field)) {
            postService.removeById(id);
            return Result.success();

        } else if("status".equals(field)) {
            post.setRecommend(rank == 1);

        }  else if("stick".equals(field)) {
            post.setLevel(rank);
        }
        postService.updateById(post);
        return Result.success();
    }
}

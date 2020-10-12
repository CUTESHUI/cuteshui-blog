package com.shui.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.shui.search.mq.PostMqIndexMessage;
import com.shui.vo.PostVo;

import java.util.List;

public interface SearchService {

    IPage search(Page page, String keyword);

    int initEsData(List<PostVo> records);

    void createOrUpdateIndex(PostMqIndexMessage message);

    void removeIndex(PostMqIndexMessage message);
}

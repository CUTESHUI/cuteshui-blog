package com.shui.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.shui.search.mq.PostMqIndexMessage;
import com.shui.dto.PostDTO;

import java.util.List;

/**
 *
 * @author CUTESHUI
 * @since 2020-09-24
 */
public interface SearchService {

    IPage search(Page page, String keyword);

    /**
     * 初始化页数
     */
    int initEsData(List<PostDTO> records);

    /**
     * 订阅mq，更新es索引
     */
    void createOrUpdateIndex(PostMqIndexMessage message);

    /**
     * 订阅mq，删除es索引
     */
    void removeIndex(PostMqIndexMessage message);
}

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

    int initEsData(List<PostDTO> records);

    void createOrUpdateIndex(PostMqIndexMessage message);

    void removeIndex(PostMqIndexMessage message);
}

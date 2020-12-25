package com.shui.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.shui.entity.UserMessage;
import com.shui.dto.UserMessageDTO;

import java.util.List;

/**
 *
 * @author CUTESHUI
 * @since 2020-09-24
 */
public interface UserMessageService extends IService<UserMessage> {

    IPage<UserMessageDTO> paging(Page page, QueryWrapper<UserMessage> wrapper);

    void updateToReaded(List<Long> ids);

}

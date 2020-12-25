package com.shui.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.shui.entity.UserMessage;
import com.shui.mapper.UserMessageMapper;
import com.shui.service.UserMessageService;
import com.shui.dto.UserMessageDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 *
 * @author CUTESHUI
 * @since 2020-09-24
 */
@Service
public class UserMessageServiceImpl extends BaseServiceImpl<UserMessageMapper, UserMessage> implements UserMessageService {

    @Override
    public IPage<UserMessageDTO> paging(Page page, QueryWrapper<UserMessage> wrapper) {
        return userMessageMapper.selectMessages(page, wrapper);
    }

    @Override
    public void updateToReaded(List<Long> ids) {
        if(ids.isEmpty()) {
            return;
        }

        userMessageMapper.updateToReaded(new QueryWrapper<UserMessage>()
                .in("id", ids)
        );
    }
}

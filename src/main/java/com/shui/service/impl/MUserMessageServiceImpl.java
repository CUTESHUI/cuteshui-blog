package com.shui.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.shui.entity.MUserMessage;
import com.shui.mapper.MUserMessageMapper;
import com.shui.service.MUserMessageService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.shui.vo.UserMessageVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author CUTESHUI
 * @since 2020-09-24
 */
@Service
public class MUserMessageServiceImpl extends ServiceImpl<MUserMessageMapper, MUserMessage> implements MUserMessageService {

    @Autowired
    MUserMessageMapper mUserMessageMapper;




    @Override
    public IPage<UserMessageVo> paging(Page page, QueryWrapper<MUserMessage> wrapper) {
        return mUserMessageMapper.selectMessages(page, wrapper);
    }

    @Override
    public void updateToReaded(List<Long> ids) {
        if(ids.isEmpty()) return;

        mUserMessageMapper.updateToReaded(new QueryWrapper<MUserMessage>()
                .in("id", ids)
        );
    }
}

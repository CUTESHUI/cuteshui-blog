package com.shui.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.shui.entity.MUserMessage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.shui.vo.UserMessageVo;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author CUTESHUI
 * @since 2020-09-24
 */
public interface MUserMessageService extends IService<MUserMessage> {

    IPage<UserMessageVo> paging(Page page, QueryWrapper<MUserMessage> wrapper);

    void updateToReaded(List<Long> ids);

}

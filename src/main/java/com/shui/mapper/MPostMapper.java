package com.shui.mapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.shui.entity.MPost;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.shui.vo.PostVo;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

/**
 *
 * @author CUTESHUI
 * @since 2020-09-24
 */

@Component
public interface MPostMapper extends BaseMapper<MPost> {

    IPage<PostVo> selectPosts(Page page, @Param(Constants.WRAPPER) QueryWrapper wrapper);

    PostVo selectOnePost(@Param(Constants.WRAPPER) QueryWrapper<MPost> wrapper);
}

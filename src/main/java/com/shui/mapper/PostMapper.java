package com.shui.mapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.shui.entity.Post;
import com.shui.dto.PostDTO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

/**
 *
 * @author CUTESHUI
 * @since 2020-09-24
 */
@Component
public interface PostMapper extends BaseMapper<Post> {

    IPage<PostDTO> selectPosts(Page page, @Param(Constants.WRAPPER) QueryWrapper wrapper);

    PostDTO selectOnePost(@Param(Constants.WRAPPER) QueryWrapper<Post> wrapper);
}

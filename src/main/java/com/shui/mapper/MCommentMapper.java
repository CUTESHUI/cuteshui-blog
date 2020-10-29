package com.shui.mapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.shui.entity.MComment;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.shui.vo.CommentVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

/**
 *
 * @author CUTESHUI
 * @since 2020-09-24
 */

@Component
public interface MCommentMapper extends BaseMapper<MComment> {

    IPage<CommentVo> selectComments(Page page, @Param(Constants.WRAPPER) QueryWrapper wrapper);
}

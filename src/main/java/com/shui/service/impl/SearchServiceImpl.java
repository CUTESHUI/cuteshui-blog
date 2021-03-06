package com.shui.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.shui.entity.Post;
import com.shui.search.model.PostDocment;
import com.shui.search.mq.PostMqIndexMessage;
import com.shui.search.repository.PostRepository;
import com.shui.service.PostService;
import com.shui.service.SearchService;
import com.shui.dto.PostDTO;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.index.query.MultiMatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class SearchServiceImpl extends BaseServiceImpl implements SearchService {

    @Override
    public IPage search(Page page, String keyword) {

        // 分页信息
        // mybatis plus的page 转成 jpa的page
        Long current = page.getCurrent() - 1;
        Long size = page.getSize();
        Pageable pageable = PageRequest.of(current.intValue(), size.intValue());

        // 搜索es，得到jpa的pageData
        // 多字段查询
        // 让 keywords 匹配 "title", "authorName", "categoryName"
        MultiMatchQueryBuilder multiMatchQueryBuilder = QueryBuilders.multiMatchQuery(keyword,
                "title", "authorName", "categoryName");

        org.springframework.data.domain.Page<PostDocment> documents = postRepository.search(multiMatchQueryBuilder, pageable);

        // 结果信息
        // jpa的pageData 转成 mybatis plus的pageData
        IPage pageData = new Page(page.getCurrent(), page.getSize(), documents.getTotalElements());
        pageData.setRecords(documents.getContent());
        return pageData;
    }

    @Override
    public int initEsData(List<PostDTO> records) {
        if(records == null || records.isEmpty()) {
            return 0;
        }

        List<PostDocment> documents = new ArrayList<>();
        for(PostDTO vo : records) {
            // 映射转换
            // records 转为 PostDocument
            PostDocment postDocment = modelMapper.map(vo, PostDocment.class);
            documents.add(postDocment);
        }
        // 保存到es
        postRepository.saveAll(documents);
        return documents.size();
    }

    @Override
    public void createOrUpdateIndex(PostMqIndexMessage message) {
        Long postId = message.getPostId();
        PostDTO postVo = postService.selectOnePost(new QueryWrapper<Post>()
                .eq("p.id", postId));

        // bean之间转换
        PostDocment postDocment = modelMapper.map(postVo, PostDocment.class);

        postRepository.save(postDocment);

        log.info("es 索引更新成功！ ---> {}", postDocment.toString());
    }

    @Override
    public void removeIndex(PostMqIndexMessage message) {
        Long postId = message.getPostId();

        postRepository.deleteById(postId);
        log.info("es 索引删除成功！ ---> {}", message.toString());
    }
}

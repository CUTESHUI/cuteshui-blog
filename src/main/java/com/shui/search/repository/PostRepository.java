package com.shui.search.repository;

import com.shui.search.model.PostDocment;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

// 操作实体咧
@Repository
public interface PostRepository extends ElasticsearchRepository<PostDocment, Long> {

    // 符合jpa命名规范的接口

}

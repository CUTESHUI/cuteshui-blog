package com.shui.schedules;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.shui.entity.Post;
import com.shui.service.PostService;
import com.shui.util.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * 定时刷新阅读量
 * @author CUTESHUI
 * @since 2020-09-24
 */
@Slf4j
@Component
public class ViewCountSyncTask {

    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private PostService postService;

    @Scheduled(cron = "0/5 * * * * *") //每5秒同步一次
    public void task() {

        Set<String> keys = redisTemplate.keys("rank:post:*");

        List<String> ids = new ArrayList<>();
        for (String key : keys) {
            // 获取所有需要更新的id
            if(redisUtil.hHasKey(key, "post:viewCount")){
                // rank:post:id(每个表都有的主键)
                ids.add(key.substring("rank:post:".length()));
            }
        }

        if(ids.isEmpty()) {
            return;
        }

        // 需要更新阅读量的文章
        List<Post> posts = postService.list(new QueryWrapper<Post>().in("id", ids));

        posts.stream().forEach((post) ->{
            // 从缓存中获取阅读量，然后更新字面量
            Integer viewCount = (Integer) redisUtil.hget("rank:post:" + post.getId(), "post:viewCount");
            post.setViewCount(viewCount);
        });

        if(posts.isEmpty()) {
            return;
        }

        boolean isSuccess = postService.updateBatchById(posts);

        // 如果成功更新到数据库,删除缓存里的
        if(isSuccess) {
            ids.stream().forEach((id) -> {
                redisUtil.hdel("rank:post:" + id, "post:viewCount");
                log.info("文章id为 "+id+" 的阅读量"+ " ------> 同步成功");
            });
        }
    }
}

package com.shui.template;

import com.shui.common.templates.DirectiveHandler;
import com.shui.common.templates.TemplateDirective;
import com.shui.util.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class WeekRankTemplate extends TemplateDirective {

    @Autowired
    RedisUtil redisUtil;

    @Override
    public String getName() {
        return "weenkrank";
    }

    @Override
    public void execute(DirectiveHandler handler) throws Exception {
        String weekRankKey = "week:rank";

        // 获取 week:rank 从大到小的集合，集合由很多 day:rank(postId, comment) 组成
        // key：日期，value：文章id，scores：文章评论数
        Set<ZSetOperations.TypedTuple> weekRank = redisUtil.getZSetRank(weekRankKey, 0, 6);

        // 每一天存一个map，最后放进list，前端从list取
        List<Map> weekRankPosts = new ArrayList<>();

        for (ZSetOperations.TypedTuple dayRank : weekRank) {
            Map<String, Object> map = new HashMap<>(16);

            Object postId = dayRank.getValue();
            // rank:post:postId 用于热议的post
            String postInfoHashKey = "rank:post:" + postId;

            map.put("id", postId);
            map.put("title", redisUtil.hget(postInfoHashKey, "post:title"));
            map.put("commentCount", dayRank.getScore());

            weekRankPosts.add(map);
        }

        handler.put(RESULTS, weekRankPosts).render();
    }
}

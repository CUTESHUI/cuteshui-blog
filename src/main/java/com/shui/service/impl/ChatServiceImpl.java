package com.shui.service.impl;

import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.RandomUtil;
import com.shui.common.lang.Consts;
import com.shui.common.lang.Result;
import com.shui.im.dto.ImMess;
import com.shui.im.dto.ImUser;
import com.shui.service.ChatService;
import com.shui.shiro.AccountProfile;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author CUTESHUI
 * @since 2020-09-24
 */
@Slf4j
@Service("chatsService")
public class ChatServiceImpl extends BaseServiceImpl implements ChatService {

    @Override
    public ImUser getCurrentUser() {
        AccountProfile profile = (AccountProfile) SecurityUtils.getSubject().getPrincipal();

        ImUser user = new ImUser();

        if(profile != null) {
            user.setId(profile.getId());
            user.setAvatar(profile.getAvatar());
            user.setUsername(profile.getUsername());
            user.setStatus(ImUser.ONLINE_STATUS);

        } else {
            user.setAvatar("http://tp1.sinaimg.cn/5619439268/180/40030060651/1");

            // 匿名用户处理
            Long imUserId = (Long) SecurityUtils.getSubject().getSession().getAttribute("imUserId");
            user.setId(imUserId != null ? imUserId : RandomUtil.randomLong());

            SecurityUtils.getSubject().getSession().setAttribute("imUserId", user.getId());

            user.setSign("never give up!");
            user.setUsername("匿名用户");
            user.setStatus(ImUser.ONLINE_STATUS);
        }

        return user;
    }

    @Override
    public void setGroupHistoryMsg(ImMess imMess) {
        redisUtil.lSet(Consts.IM_GROUP_HISTORY_MSG_KEY, imMess, 24 * 60 * 60);
    }

    @Override
    public List<Object> getGroupHistoryMsg(int count) {
        long length = redisUtil.lGetListSize(Consts.IM_GROUP_HISTORY_MSG_KEY);
        return redisUtil.lGet(Consts.IM_GROUP_HISTORY_MSG_KEY, length - count < 0 ? 0 : length - count, length);
    }

    @Override
    public Result getMineAndGroupData() {

        //默认群
        Map<String, Object> group = new HashMap<>(16);
        group.put("name", "社区群聊");
        group.put("type", "group");
        group.put("avatar", "http://tp1.sinaimg.cn/5619439268/180/40030060651/1");
        group.put("id", Consts.IM_GROUP_ID);
        group.put("members", 0);

        ImUser user = chatService.getCurrentUser();
        return Result.success(MapUtil.builder()
                .put("group", group)
                .put("mine", user)
                .map());
    }
}

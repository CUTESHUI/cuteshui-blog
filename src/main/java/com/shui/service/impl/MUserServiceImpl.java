package com.shui.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.crypto.SecureUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.shui.common.lang.Result;
import com.shui.entity.MUser;
import com.shui.mapper.MUserMapper;
import com.shui.service.MUserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.shui.shiro.AccountProfile;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.UnknownAccountException;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author CUTESHUI
 * @since 2020-09-24
 */
@Service
public class MUserServiceImpl extends ServiceImpl<MUserMapper, MUser> implements MUserService {

    /**
     *  注册
     */
    @Override
    public Result register(MUser user) {
        // 邮箱
        int count = this.count(new QueryWrapper<MUser>()
                .eq("email", user.getEmail())
                .or()
                .eq("username", user.getUsername())
        );
        if(count > 0) {
            return Result.fail("用户名或邮箱已被占用");
        }

        MUser temp = new MUser();
        temp.setUsername(user.getUsername());
        temp.setPassword(SecureUtil.md5(user.getPassword()));
        temp.setEmail(user.getEmail());
        temp.setAvatar("/res/images/avatar/default.png");

        temp.setCreated(new Date());
        temp.setPoint(0);
        temp.setVipLevel(0);
        temp.setCommentCount(0);
        temp.setPostCount(0);
        temp.setGender("0"); // 默认男-0
        this.save(temp);

        return Result.success();
    }

    /**
     *  登录
     */
    @Override
    public AccountProfile login(String email, String password) {
        // 获取一个用户信息
        MUser user = this.getOne(new QueryWrapper<MUser>()
                .eq("email", email));

        if(user == null) {
            throw new UnknownAccountException();
        }
        if(!user.getPassword().equals(password)){
            throw new IncorrectCredentialsException();
        }

        user.setLasted(new Date());
        this.updateById(user);

        AccountProfile profile = new AccountProfile();
        BeanUtil.copyProperties(user, profile);

        return profile;
    }
}

package com.shui.service;

import com.shui.common.lang.Result;
import com.shui.entity.MUser;
import com.baomidou.mybatisplus.extension.service.IService;
import com.shui.shiro.AccountProfile;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author CUTESHUI
 * @since 2020-09-24
 */
public interface MUserService extends IService<MUser> {

    Result register(MUser user);

    AccountProfile login(String email, String password);
}

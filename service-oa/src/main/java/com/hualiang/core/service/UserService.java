package com.hualiang.core.service;

import java.util.Map;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.hualiang.model.User;

/**
 * Date:2022/7/11
 * Author:ybc
 * Description:
 */
public interface UserService extends IService<User> {

    Integer register(User user);

    IPage<User> getUserPage(Map<String, String> option, Integer pageNum);
}

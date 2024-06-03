package com.hualiang.core.service.impl;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hualiang.core.mapper.UserMapper;
import com.hualiang.core.service.UserService;
import com.hualiang.model.User;

/**
 * Date:2022/7/11
 * Author:ybc
 * Description:
 */
@Service
@Transactional
public class UserServiceImpl extends ServiceImpl<UserMapper,User> implements UserService {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public Integer register(User newUser) {
        User user = baseMapper.selectOne(new QueryWrapper<User>().eq("username", newUser.getUsername()));
        if (user != null) {
            return null;
        } else {
            newUser.setPassword(passwordEncoder.encode(newUser.getPassword()));
            baseMapper.insert(newUser);
            return newUser.getUid();
        }
    }

    @Override
    public IPage<User> getUserPage(Map<String, String> option, Integer pageNum) {
        return baseMapper.selectAllWithRole(new Page<User>(pageNum, 5), option);
    }
}

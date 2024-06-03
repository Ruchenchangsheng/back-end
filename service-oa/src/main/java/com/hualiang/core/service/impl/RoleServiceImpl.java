package com.hualiang.core.service.impl;

import com.hualiang.core.mapper.RoleMapper;
import com.hualiang.core.service.RoleService;
import com.hualiang.model.Role;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import org.springframework.stereotype.Service;

/**
 * <p>
 * 角色 服务实现类
 * </p>
 *
 * @author Hualiang
 * @since 2023-11-02
 */
@Service
public class RoleServiceImpl extends ServiceImpl<RoleMapper, Role> implements RoleService {

    @Override
    public Integer doAssign(Integer userId, Integer[] roleIds) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'doAssign'");
    }

}

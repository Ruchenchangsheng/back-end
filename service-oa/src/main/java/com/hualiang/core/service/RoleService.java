package com.hualiang.core.service;

import com.hualiang.model.Role;

import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 角色 服务类
 * </p>
 *
 * @author Hualiang
 * @since 2023-11-02
 */
public interface RoleService extends IService<Role> {

    Integer doAssign(Integer userId, Integer[] roleIds);
}

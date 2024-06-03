package com.hualiang.core.service;

import com.hualiang.model.Perm;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 菜单表 服务类
 * </p>
 *
 * @author Hualiang
 * @since 2023-11-02
 */
public interface PermService extends IService<Perm> {

    List<String> getPermStringByUserId(Integer userId);
    
    List<Perm> getPermsByUserId(Integer userId);

    Integer doAssign(Integer roleId, Integer[] permIds);
}

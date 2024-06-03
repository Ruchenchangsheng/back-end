package com.hualiang.core.mapper;

import com.hualiang.model.Perm;

import java.util.List;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * <p>
 * 菜单表 Mapper 接口
 * </p>
 *
 * @author Hualiang
 * @since 2023-11-02
 */
public interface PermMapper extends BaseMapper<Perm> {

    List<Perm> getPermsByUserId(Integer userId);
}

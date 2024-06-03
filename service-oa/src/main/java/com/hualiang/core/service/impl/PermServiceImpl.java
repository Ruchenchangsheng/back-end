package com.hualiang.core.service.impl;

import com.hualiang.core.mapper.PermMapper;
import com.hualiang.core.service.PermService;
import com.hualiang.model.Perm;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

/**
 * <p>
 * 菜单表 服务实现类
 * </p>
 *
 * @author Hualiang
 * @since 2023-11-02
 */
@Service
public class PermServiceImpl extends ServiceImpl<PermMapper, Perm> implements PermService {

    @Override
    public List<String> getPermStringByUserId(Integer userId) {
        // 1 判断是否是管理员，如果是管理员，查询所有按钮列表
        List<Perm> permList = null;
        permList = baseMapper.getPermsByUserId(userId);

        // 3 从查询出来的数据里面，获取可以操作按钮值的list集合，返回
        List<String> permsList = permList.stream()
                .map(item -> item.getAuthorities())
                .collect(Collectors.toList());

        return permsList;
    }

    @Override
    public Integer doAssign(Integer roleId, Integer[] permIds) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'doAssign'");
    }

    @Override
    public List<Perm> getPermsByUserId(Integer userId) {
        return baseMapper.getPermsByUserId(userId);
    }

}

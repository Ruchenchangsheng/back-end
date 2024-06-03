package com.hualiang.core.service;

import com.hualiang.core.mapper.UserMapper;
import com.hualiang.model.User;
import com.hualiang.security.custom.NewUserDetails;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service("userDetailsService")
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserMapper usersMapper;
    @Autowired
    private PermService permService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User users = usersMapper.selectWithRoleByField("username", username);
        if (users == null) {
            throw new UsernameNotFoundException("用户名不存在！");
        }
        List<String> userPermsList = permService.getPermStringByUserId(users.getUid());
        
        List<SimpleGrantedAuthority> authList = new ArrayList<>();
        
        for (String perm : userPermsList) {
            authList.add(new SimpleGrantedAuthority(perm.trim()));
        }
        return new NewUserDetails(users, authList);
    }
}

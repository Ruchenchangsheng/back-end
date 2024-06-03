package com.hualiang.security.custom;

import com.hualiang.common.result.Result;
import com.hualiang.model.User;

import lombok.Getter;

import org.springframework.security.core.GrantedAuthority;
import java.util.Collection;

public class NewUserDetails extends org.springframework.security.core.userdetails.User {
    
    @Getter
    private User userInfo;  //创建我们的user实体类属性

    public NewUserDetails(User user, Collection<? extends GrantedAuthority> authorities) 	{
        super(user.getUsername(), user.getPassword(), authorities); //这里让他去执行它父类的构造函数
        this.userInfo = user; //将传过来的用户实体类赋值给userinfo属性
    }

    public NewUserDetails(String username, String password, boolean enabled, boolean accountNonExpired, boolean credentialsNonExpired, boolean accountNonLocked, Collection<? extends GrantedAuthority> authorities) {
        super(username, password, enabled, accountNonExpired, credentialsNonExpired, accountNonLocked, authorities);
    }

    public Result<String> getResult(String token){
        return Result.ok(token).message("登录成功");
    }

}
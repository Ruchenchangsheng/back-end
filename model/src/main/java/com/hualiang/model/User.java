package com.hualiang.model;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.hualiang.model.groups.UpdateGroup;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class User {

    @NotNull(message = "用户ID不能为空", groups = UpdateGroup.class)
    @TableId
    private Integer uid;

    @NotBlank(message = "用户名不能为空")
    private String username;

    @NotBlank(message = "密码不能为空")
    private String password;

    private String realName;

    private Character gender;

    private Integer age;

    private String phone;

    @Email(message = "邮箱格式不正确")
    private String email;

    @NotNull(message = "角色ID不能为空")
    private Integer rid;

    @TableField(exist = false)
    private String roleName;

}
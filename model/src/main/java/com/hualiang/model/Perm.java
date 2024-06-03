package com.hualiang.model;

import com.baomidou.mybatisplus.annotation.TableId;
import com.hualiang.model.groups.UpdateGroup;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Perm {

    @TableId
    @NotNull(message = "权限ID不能为空", groups = UpdateGroup.class)
    private Integer pid;

    @NotBlank(message = "权限名不能为空")
    private String name;

    @NotBlank(message = "权限标识不能为空")
    private String authorities;

    @NotNull(message = "权限状态不能为空")
    private Boolean status;

}

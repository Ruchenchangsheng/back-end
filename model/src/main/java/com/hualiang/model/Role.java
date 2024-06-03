package com.hualiang.model;

import com.baomidou.mybatisplus.annotation.TableId;
import com.hualiang.model.groups.UpdateGroup;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class Role {

    @TableId
    @NotNull(message = "角色ID不能为空", groups = UpdateGroup.class)
    private Integer rid;

    @NotBlank(message = "角色名不能为空")
    private String roleName;

    @NotBlank(message = "角色描述不能为空")
    private String description;

    public Role(Integer rid, String roleName) {
        this.rid = rid;
        this.roleName = roleName;
    }

    public Role(Integer rid, String roleName, String description) {
        this.rid = rid;
        this.roleName = roleName;
        this.description = description;
    }

}

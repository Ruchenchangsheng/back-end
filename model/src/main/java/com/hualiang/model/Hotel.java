package com.hualiang.model;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.hualiang.model.groups.UpdateGroup;
import com.hualiang.model.util.FileUtils;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class Hotel {

    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    @TableField(exist = false)
    private String[] url;
    
    @TableField(exist = false)
    private String manager;

    @TableId
    @NotNull(message = "酒店ID不能为空", groups = UpdateGroup.class)
    private Integer hotelId;

    @NotBlank(message = "酒店名不能为空")
    private String hotelName;

    @NotBlank(message = "酒店地址不能为空")
    private String address;

    @NotNull(message = "经营者ID不能为空")
    private Integer managerId;

    @NotBlank(message = "酒店电话不能为空")
    private String phone;

    @NotBlank(message = "酒店简介不能为空")
    private String message;

    @NotBlank(message = "酒店图片不能为空")
    private String photo;

    public Hotel(String hotelName, String address, Integer managerId, String phone, String photo, String message) {
        this.hotelName = hotelName;
        this.address = address;
        this.managerId = managerId;
        this.phone = phone;
        this.photo = photo;
        this.message = message;
    }

    public String[] getURL() {
        if (url == null) {
            String[] path = photo.split("\\|");
            url = new String[path.length - 1];
            for (int i = 1; i < path.length; i++)
                url[i - 1] = FileUtils.pathToUrl(path[0] + "\\" + path[i]);
        }
        return url;
    }
    
    public String[] getPicURL() {
        String[] url = getURL();
        String[] url2 = new String[url.length - 1];
        System.arraycopy(url, 1, url2, 0, url2.length);
        return url2;
    }

    public String getPath() {
        return photo.split("\\|")[0];
    }

}
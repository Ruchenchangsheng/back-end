package com.hualiang.model;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.hualiang.model.groups.UpdateGroup;
import com.hualiang.model.util.FileUtils;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Room {

    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    @TableField(exist = false)
    private String[] url;

    @TableField(exist = false)
    private String photo;

    @TableId
    @NotNull(message = "房间ID不能为空", groups = UpdateGroup.class)
    private Integer roomId;

    @NotBlank(message = "房型不能为空")
    private String type;

    @NotNull(message = "房间大小不能为空")
    private Integer size;

    @NotNull(message = "酒店ID不能为空")
    private Integer hotelId;

    @NotNull(message = "房间价格不能为空")
    private Integer price;

    private Integer userId;

    private Integer status = 0;

    private Integer typeId;

    private int numOfGuests;

//    private String message;

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
    
    public String getUUID() {
        String path = getPath();
        return path.substring(path.lastIndexOf("\\") + 1);
    }

}
package com.hualiang.model;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import com.baomidou.mybatisplus.annotation.TableField;
import com.hualiang.model.groups.UpdateGroup;
import com.hualiang.model.util.FileUtils;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class HotelRegisteration {

    private static DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    @TableField(exist = false)
    private String[] url;

    @TableField(exist = false)
    private String manager;
    
    @NotNull(message = "注册申请ID不能为空", groups = UpdateGroup.class)
    private Integer id;

    @NotBlank(message = "酒店名不能为空")
    private String hotelName;

    @NotNull(message = "经营者ID不能为空")
    private Integer managerId;

    @NotBlank(message = "酒店地址不能为空")
    private String address;

    @NotBlank(message = "酒店电话不能为空")
    private String phone;

    @NotBlank(message = "酒店照片不能为空")
    private String photo;

    @NotBlank(message = "酒店简介不能为空")
    private String message;

    private Timestamp registerTime = new Timestamp(System.currentTimeMillis());

    private Timestamp reviewTime;
    
    private Boolean status;

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

    public String registerTimeToString() {
        return df.format(registerTime);
    }

    public String reviewTimeToString() {
        return df.format(reviewTime);
    }

    public Hotel getHotel() {
        return new Hotel(this.hotelName, this.address, this.managerId, this.phone, this.photo, this.message);
    }
}
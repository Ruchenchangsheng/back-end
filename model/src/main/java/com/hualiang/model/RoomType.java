package com.hualiang.model;

import java.util.Map;

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
public class RoomType {

    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    @TableField(exist = false)
    private String[] url;

    @TableId
    @NotNull(message = "房型ID不能为空", groups = UpdateGroup.class)
    private Integer typeId;

    @NotNull(message = "酒店ID不能为空")
    private Integer hotelId;

    @NotBlank(message = "房型名称不能为空")
    private String typeName;

    @NotBlank(message = "房型描述不能为空")
    private String message;

    @NotBlank(message = "房型图片不能为空")
    private String photo;

    public String[] toURL() {
        if (url == null) {
            String[] path = photo.split("\\|");
            url = new String[path.length - 1];
            for (int i = 1; i < path.length; i++)
                url[i - 1] = FileUtils.pathToUrl(path[0] + "\\" + path[i]);
        }
        return url;
    }

    public String toURLString() {
        StringBuffer sb = new StringBuffer();
        if (url == null) {
            String[] path = photo.split("\\|");
            for (int i = 1; i < path.length; i++)
                sb.append(FileUtils.pathToUrl(path[0] + "\\" + path[i]) + (i == path.length - 1 ? "" : "|"));
        } else {
            for (int i = 0; i < url.length; i++)
                sb.append(url[i] + (i == url.length - 1 ? "" : "|"));
        }
        return sb.toString();
    }

    public String[] toPicURL() {
        String[] url = toURL();
        String[] url2 = new String[url.length - 1];
        System.arraycopy(url, 1, url2, 0, url2.length);
        return url2;
    }

    public String toPath() {
        return photo.split("\\|")[0];
    }

    public String toUUID() {
        String path = toPath();
        return path.substring(path.lastIndexOf("\\") + 1);
    }

    public RoomType(Map<String, String> map) {
        this.typeId = map.get("typeId") != null ? Integer.parseInt(map.get("typeId")) : null;
        this.hotelId = Integer.parseInt(map.get("hotelId"));
        this.typeName = map.get("typeName");
        this.message = map.get("message");
        this.photo = map.get("photo");
    }

}

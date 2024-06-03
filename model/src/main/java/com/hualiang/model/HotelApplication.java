package com.hualiang.model;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Map;

import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hualiang.model.groups.UpdateGroup;
import com.hualiang.model.util.FileUtils;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class HotelApplication {

    private static DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @TableField(exist = false)
    private String hotelName;

    @NotNull(message = "申请ID不能为空", groups = UpdateGroup.class)
    private Integer id;

    @NotNull(message = "酒店ID不能为空")
    private Integer hotelId;

    @NotBlank(message = "申请标题不能为空")
    private String title;

    @NotBlank(message = "申请内容不能为空")
    private String data;

    private Boolean status;

    private Timestamp submitTime = new Timestamp(System.currentTimeMillis());

    private Timestamp reviewTime;

    public HotelApplication(Integer hotelId, String title, Object data) {
        this.hotelId = hotelId;
        this.title = title;
        setObject(data);
    }

    public Map<String, String> getMap() {
        try {
            return new ObjectMapper().readValue(data, new TypeReference<Map<String, String>>() { });
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public Map<String, String> getTypeMap() {
        Map<String, String> typeMap = getMap();
        String photo = typeMap.get("photo");
        String[] path = photo.split("\\|");
        typeMap.put("size", path.length - 1 + "");
        typeMap.put("pic", FileUtils.pathToUrl(path[0] + "\\" + path[1]));
        StringBuffer sb = new StringBuffer();
        if (path.length > 2) {
            for (int i = 2; i < path.length; i++)
                sb.append(FileUtils.pathToUrl(path[0] + "\\" + path[i]) + (i == path.length - 1 ? "" : "|"));
        }
        typeMap.put("url", sb.toString());
        return typeMap;
    }

    public <T> T getObject(Class<T> clazz) {
        try {
            return new ObjectMapper().readValue(data, clazz);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void setObject(Object object) {
        try {
            data = new ObjectMapper().writeValueAsString(object);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public String getMessage() {
        return "提交于 " + submitTimeToString() + " ，" + (reviewTime != null ? "审核于 " + reviewTimeToString() : "待审核");
    }

    public String submitTimeToString() {
        return df.format(submitTime);
    }

    public String reviewTimeToString() {
        return df.format(reviewTime);
    }
}
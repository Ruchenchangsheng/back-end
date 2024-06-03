package com.hualiang.core.controller.api;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import com.hualiang.model.SearchRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.constraints.NotBlank;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory; // 导入日志工厂类
import com.hualiang.common.result.Result;
import com.hualiang.common.result.ResultCodeEnum;
import com.hualiang.core.service.HotelService;
import com.hualiang.model.Hotel;
import com.hualiang.model.HotelRegisteration;
import com.hualiang.model.User;
import com.hualiang.model.util.FileUtils;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "酒店管理接口", description = "包含酒店的增删改查以及提交和处理酒店注册与酒店申请等接口")
@RestController
@RequestMapping("/hotel")
public class HotelController {

    @Autowired
    private HotelService hotelService;

    private static final Logger logger = LoggerFactory.getLogger(HotelController.class); // 声明日志记录器


    @Operation(summary = "提交酒店注册申请")
    @PreAuthorize("hasAuthority('registeration.add')")
    @PostMapping("/registeration/register")
    public Result<Integer> register(@Validated @RequestBody HotelRegisteration hotelRegisteration) throws IOException {
        String[] tempPath = hotelRegisteration.getPhoto().split("\\|", 2);
        String dirPath = FileUtils.renameDir(tempPath[0], UUID.randomUUID().toString());
        hotelRegisteration.setPhoto(dirPath + "|" + tempPath[1]);
        Integer RegID = hotelService.register(hotelRegisteration);
        if (RegID != null) {
            return Result.ok(RegID).message("注册申请提交成功");
        } else {
            FileUtils.deleteDir(dirPath);
            return Result.fail(RegID).message("注册申请提交失败");
        }
    }

    @Operation(summary = "处理酒店注册申请")
    @PreAuthorize("hasAuthority('registeration.handle')")
    @GetMapping("/registeration/handle/{regId}/{status}")
    public Result<Integer> handleRegisteration(@PathVariable Integer regId, @PathVariable Boolean status) {
        Integer HID = hotelService.review(regId, status);
        return HID != null ? Result.ok(HID).message("注册申请处理成功") : Result.fail(HID).message("注册申请处理失败");
    }

    @Operation(summary = "添加酒店")
    @PreAuthorize("hasAuthority('hotel.add')")
    @PostMapping("/crud")
    public Result<Object> addHotel(@Validated @RequestBody Hotel hotel) {
        Boolean result = hotelService.save(hotel);
        return result ? Result.ok().message("添加成功") : Result.fail().message("添加失败");
    }

    @Operation(summary = "删除酒店")
    @PreAuthorize("hasAuthority('hotel.delete')")
    @DeleteMapping("/crud/{hotelId}")
    public Result<Object> delHotel(@PathVariable Integer hotelId) {
        Integer result = hotelService.delete(hotelId);
        return result > 0 ? Result.ok().message("删除成功") : Result.fail().message("删除失败");
    }

    @Operation(summary = "更新酒店")
    @PreAuthorize("hasAuthority('hotel.update')")
    @PutMapping("/crud")
    public Result<Object> updateHotel(@RequestBody Hotel hotel) {
        Hotel old = hotelService.getById(hotel.getHotelId());
        hotel.setPhoto(old.getPhoto());
        Boolean result = hotelService.updateById(hotel);
        return result ? Result.ok().message("更新成功") : Result.fail().message("更新失败");
    }

    @Operation(summary = "上传酒店或房型照片", description = "该接口同时用于上传酒店照片和房型照片，type为hotel时上传酒店照片，type为room时上传房型照片")
    @PreAuthorize("hasAuthority('photo.upload')")
    @SuppressWarnings("null")
    @PostMapping("/upload/{type}")
    public Result<String> uploadPhoto(@PathVariable String type, MultipartFile file, HttpSession session) throws IOException {
        try {
            String fileName = file.getOriginalFilename();
            String suffix = fileName.substring(fileName.lastIndexOf("."));
            fileName = UUID.randomUUID().toString() + suffix;
            User user = (User) session.getAttribute("user");
            String url = "upload/" + type + "/temp_" + user.getUid();
            Path dirPath = Paths.get(System.getProperty("user.dir"), "src", "main", "resources", "static", url);
            Path finalPath = dirPath.resolve(fileName);
            Files.createDirectories(dirPath);
            file.transferTo(finalPath);
            return Result.ok(finalPath.toString());
        } catch (IOException e) {
            logger.error("文件上传失败", e); // 记录错误日志
            throw e; // 抛出异常
        }
    }


    @Validated 
    @Operation(summary = "更新酒店照片")
    @PreAuthorize("hasAuthority('hotel.update')")
    @PutMapping("/crud/photo/{hotelId}/{isExecute}")
    public Result<Object> updateHotelPhoto(@PathVariable Integer hotelId, @PathVariable Boolean isExecute, @NotBlank @RequestBody String photoPath) {
        Hotel hotel = hotelService.getById(hotelId);
        String[] tempPath = photoPath.split("\\|", 2);
        if (isExecute) {
            String dirPath = FileUtils.renameDir(tempPath[0], UUID.randomUUID().toString());
            String oldPath = hotel.getPath();
            hotel.setPhoto(dirPath + "|" + tempPath[1]);
            Boolean result = hotelService.updateById(hotel);
            if (result) {
                FileUtils.deleteDir(oldPath);
                return Result.ok().message("酒店照片更新成功");
            } else {
                FileUtils.deleteDir(dirPath);
                return Result.fail().message("酒店照片更新失败");
            }
        } else {
            FileUtils.deleteDir(tempPath[0]);
            return Result.build(null, ResultCodeEnum.CANCEL_OPERATION);
        }
    }


    //Mobile
    @Operation(summary = "按条件查找酒店")
//    @PreAuthorize("hasAuthority('hotel.display')")
    @PostMapping("/searchHotel")
    public List<Hotel> findAvailableHotels(@RequestBody SearchRequest request) {
        return hotelService.findAvailableHotels(request.getAddress(), request.getStartDate(), request.getEndDate(), request.getNumOfGuests());
    }

}

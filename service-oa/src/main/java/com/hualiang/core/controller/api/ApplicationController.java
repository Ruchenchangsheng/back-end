package com.hualiang.core.controller.api;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hualiang.common.result.Result;
import com.hualiang.core.service.ApplicationService;
import com.hualiang.model.Facility;
import com.hualiang.model.RoomType;
import com.hualiang.model.util.FileUtils;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotEmpty;

@Tag(name = "酒店申请管理接口", description = "包含房间设施和房型的增删改查以及提交和处理申请等接口")
@RestController
@RequestMapping("/applicate")
public class ApplicationController {

    @Autowired
    private ApplicationService applicationService;

    @Operation(summary = "添加或提交房间设施和房型", description = "该接口同时用于添加或提交房间设施和房型，" +
            "type为facility时添加房间设施，type为roomType时添加房型；level为Admin时直接添加，为Manager时提交申请")
    @PreAuthorize("hasAnyAuthority('facility.add', 'type.add')")
    @PostMapping("{level}/{type}/crud")
    public Result<Integer> add(@PathVariable String level, @PathVariable String type, @RequestBody Map<String, String> map) {
        Integer ID;
        if (type.equals("facility")) {
            ID = level.equals("Admin") ? applicationService.addFacility(new Facility(map)) :
                    applicationService.submit(Integer.parseInt(map.get("hotelId")), "新增", "添加一条数据", new Facility(map));
        } else {
            String[] tempPath = map.get("photo").split("\\|", 2);
            String dirPath = FileUtils.renameDir(tempPath[0], UUID.randomUUID().toString());
            map.replace("photo", dirPath + "|" + tempPath[1]);
            ID = level.equals("Admin") ? applicationService.addType(new RoomType(map)) :
                    applicationService.submit(Integer.parseInt(map.get("hotelId")), "新增", "添加一条数据", new RoomType(map));
        }
        return ID != null ? Result.ok(ID) : Result.fail(ID);
    }

    @Operation(summary = "更新或提交房间设施和房型", description = "该接口同时用于更新或提交房间设施和房型，" +
            "type为facility时更新房间设施列表，type为roomType时更新房型列表；level为Admin时直接更新，为Manager时提交申请")
    @PreAuthorize("hasAnyAuthority('facility.update', 'type.update')")
    @PutMapping("{level}/{type}/crud")
    public Result<Integer> update(@PathVariable String level, @PathVariable String type, @RequestBody Map<String, String> map) {
        Integer ID;
        if (type.equals("facility")) {
            if (level.equals("Admin")) {
                ID = applicationService.updateFacility(new Facility(map));
            } else {
                ID = applicationService.submit(Integer.parseInt(map.get("hotelId")), "更新", "修改一条数据", new Facility(map));
            }
            return ID != null ? Result.ok(ID) : Result.fail(ID);
        } else {
            RoomType roomType = new RoomType(map);
            String oldPath = applicationService.getRoomType(roomType.getTypeId()).toPath();
            String[] tempPath = roomType.getPhoto().split("\\|", 2);
            String dirPath = FileUtils.renameDir(tempPath[0], UUID.randomUUID().toString());
            roomType.setPhoto(dirPath + "|" + tempPath[1]);
            if (level.equals("Admin")) {
                ID = applicationService.updateType(roomType);
                if (ID > 0) {
                    FileUtils.deleteDir(oldPath);
                }
            } else {
                ID = applicationService.submit(Integer.parseInt(map.get("hotelId")), "更新", "修改一条数据", roomType);
            }
            if (ID != null) {
                return Result.ok(ID);
            } else {
                FileUtils.deleteDir(dirPath);
                return Result.fail(ID);
            }
        }
    }

    @Operation(summary = "删除房间设施和房型", description = "该接口同时用于删除房间设施和房型，type为facility时删除房间设施，type为roomType时删除房型")
    @PreAuthorize("hasAnyAuthority('facility.delete', 'type.delete')")
    @DeleteMapping("/{type}/crud/{id}")
    public Result<Object> delete(@PathVariable String type, @PathVariable Integer id) {
        Integer result = type.equals("facility") ? applicationService.deleteFacility(id) : applicationService.deleteType(id);
        return result > 0 ? Result.ok().message("删除成功") : Result.fail().message("删除失败");
    }

    @Validated
    @Operation(summary = "更新房间内设施")
    @PreAuthorize("hasAuthority('room.facilities.update')")
    @PutMapping("/facility/update/{roomId}")
    public Result<Object> upadteFacilityToRoom(@PathVariable Integer roomId, @NotEmpty @RequestBody List<Integer> facilities) {
        Integer result = applicationService.upadteFacilityToRoom(facilities, roomId);
        return result == facilities.size() ? Result.ok().message("房间设施更新成功") : Result.fail().message("房间设施更新失败");
    }

    @Operation(summary = "处理酒店申请")
    @PreAuthorize("hasAuthority('application.handle')")
    @GetMapping("/handle/{id}/{status}")
    public Result<String> handleApplication(@PathVariable Integer id, @PathVariable Boolean status) {
        String ID = applicationService.review(id, status);
        return ID != null ? Result.ok(ID).message("申请处理成功") : Result.fail(ID).message("申请处理失败");
    }
}

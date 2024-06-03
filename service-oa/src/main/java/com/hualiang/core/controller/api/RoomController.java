package com.hualiang.core.controller.api;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.hualiang.model.*;
import com.hualiang.security.custom.NewUserDetails;
import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
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
import com.hualiang.core.service.RoomService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "房间管理接口", description = "包含房间的增删改查以及房间预订等接口")
@RestController
@RequestMapping("/room")
public class RoomController {

    @Autowired
    private RoomService roomService;

    @Operation(summary = "预定房间")
    @PreAuthorize("hasAuthority('reserve.add')")
    @PostMapping("/reservation/reserve")
    public Result<Integer> reserve(@Validated @RequestBody Reservation reservation, HttpSession session) {
        Integer ResID = roomService.reserve(reservation);
        if (ResID != null) {
            User user = (User) session.getAttribute("user");
            List<Integer> reserveList = roomService.getPendingRoomIdByUID(user.getUid());
            session.setAttribute("reserveList", reserveList);
            return Result.ok(ResID).message("房间预订成功");
        }
        return Result.fail(ResID).message("房间预订失败");
    }

    @Operation(summary = "处理房间预订申请")
    @PreAuthorize("hasAnyAuthority('reserve.handle', 'reserve.delete')")
    @GetMapping({ "/reservation/handle/{id}/{status}", "/reservation/handle/{id}" })
    public Result<Object> handleReservation(@PathVariable Integer id, @PathVariable(required = false) Boolean status) {
        Integer result = roomService.review(id, status);
        return result > 0 ? Result.ok().message("处理成功") : Result.fail().message("处理失败");
    }

    @Operation(summary = "删除房间入住记录")
    @PreAuthorize("hasAuthority('checkin.delete')")
    @DeleteMapping("/checkin/delete/{id}")
    public Result<Object> deleteCheckin(@PathVariable Integer id) {
        Integer result = roomService.deleteCheckinRecord(id);
        return result > 0 ? Result.ok().message("记录删除成功") : Result.fail().message("记录删除失败");
    }

    @Operation(summary = "添加房间")
    @PreAuthorize("hasAuthority('room.add')")
    @PostMapping("/crud")
    public Result<Integer> add(@Validated @RequestBody Room room) {
        Integer RID = roomService.addRoom(room);
        return RID != null ? Result.ok(RID).message("添加成功") : Result.fail(RID).message("添加失败");
    }

    @Operation(summary = "删除房间")
    @PreAuthorize("hasAuthority('room.delete')")
    @DeleteMapping("/crud/{id}")
    public Result<Object> del(@PathVariable Integer id) {
        Boolean result = roomService.removeById(id);
        return result ? Result.ok().message("删除成功") : Result.fail().message("删除失败");
    }

    @Operation(summary = "更新房间")
    @PreAuthorize("hasAuthority('room.update')")
    @PutMapping("/crud")
    public Result<Object> update(@RequestBody Room room) {
        Boolean result = roomService.updateById(room);
        return result ? Result.ok().message("更新成功") : Result.fail().message("更新失败");
    }

    @Operation(summary = "更新房间入住状态")
    @PreAuthorize("hasAuthority('room.update')")
    @PutMapping("/status/{roomId}/{status}/{userId}")
    public Result<Integer> updateStatus(@PathVariable Integer roomId, @PathVariable Integer status, @PathVariable Integer userId) {
        if (userId == 0) { userId = null; }
        Integer result = roomService.updateRoomStatus(roomId, status, userId);
        return result > 0 ? Result.ok(result).message("房间状态更新成功") : Result.fail(result).message("房间状态更新失败");
    }


    //Mobile
    @PreAuthorize("hasAnyAuthority('room.display')")
    @GetMapping("/roomList/{hotelId}")
    public ResponseEntity<List<Room>> getRoomListWitchHotelId(@PathVariable("hotelId")int hotelId) {
        List<Room> rooms = roomService.searchRoomsByHotelId(hotelId);
        return ResponseEntity.ok(rooms);
    }

    @PreAuthorize("hasAnyAuthority('room.detail')")
    @GetMapping("/roomDetail/{roomId}")
    public ResponseEntity<List<RoomDetail>> getRoomDetail(@PathVariable("roomId") int roomId){
        List<RoomDetail> roomDetails = roomService.searchRoomDetail(roomId);
        return ResponseEntity.ok(roomDetails);
    }

    @Operation(summary = "(手机)预定房间")
    @PreAuthorize("hasAuthority('reserve.add')")
    @PostMapping("/order/booking")
    public ResponseEntity<?> reserveRoom(@Validated @RequestBody Reservation reservation) {
        Integer reservationId = roomService.reserve(reservation);
        if (reservationId != null) {
            System.out.println("Successfully reserve room");
            return ResponseEntity.ok().body(Map.of("reservationId", reservationId));
        } else {
            System.out.println("Failed reserve room");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Failed to reserve room"));
        }
    }
}

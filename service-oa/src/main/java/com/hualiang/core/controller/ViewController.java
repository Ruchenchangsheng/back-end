package com.hualiang.core.controller;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.hualiang.core.mapper.ReservationMapper;
import com.hualiang.core.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.hualiang.model.Hotel;
import com.hualiang.model.HotelApplication;
import com.hualiang.model.HotelRegisteration;
import com.hualiang.model.Reservation;
import com.hualiang.model.Room;
import com.hualiang.model.User;
import com.hualiang.common.utils.IpUtil;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@Controller
@SuppressWarnings("unchecked")
public class ViewController {

    @Autowired
    private UserService userService;
    @Autowired
    private HotelService hotelService;
    @Autowired
    private ApplicationService applicationService;
    @Autowired
    private RoomService roomService;
    @Autowired
    private ReservationService reservationService;

    @GetMapping("/")
    public String root() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() && !(authentication instanceof AnonymousAuthenticationToken)) {
            return "index";
        }
        return "login";
    }
    
    @GetMapping("/unauth")
    public String unauth(HttpServletRequest request, Model model) {
        String ip = IpUtil.getIpAddress(request);
        String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        model.addAttribute("ip", ip);
        model.addAttribute("time", time);
        return "unauth";
    }

    @PreAuthorize("hasAuthority('user.select')")
    @GetMapping("/user/index/{pageNum}")
    public String getUserPage(@PathVariable Integer pageNum, @RequestParam(defaultValue = "uid") String option, @RequestParam(defaultValue = "") String value, Model model) {
        Map<String, String> map = new HashMap<>();
        map.put("option", option);
        map.put("value", value);
        IPage<User> page = userService.getUserPage(map, pageNum);
        model.addAttribute("map", map);
        model.addAttribute("page", page);
        return "user_index";
    }

    @PreAuthorize("hasAuthority('hotel.select')")
    @GetMapping("/hotel/index/{pageNum}")
    public String getHotelPage(@PathVariable Integer pageNum, @RequestParam(defaultValue = "hotel_id") String option,
                               @RequestParam(defaultValue = "") String value, Model model, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user.getRoleName().equals("Manager")) { option = "manager_id"; value = user.getUid().toString(); }
        Map<String, String> map = new HashMap<>();
        if (option.equals("manager_id")) {
            map.put("option", "hotel_id");
            map.put("value", "");
            map.put(option, value);
        } else {
            map.put("option", option);
            map.put("value", value);
        }
        IPage<Hotel> page = hotelService.getHotelPage(map, pageNum);
        model.addAttribute("map", map);
        model.addAttribute("page", page);
        return "hotel_index";
    }

    @PreAuthorize("hasAuthority('hotel.display')")
    @GetMapping("/hotel/display")
    public String getHotelList(@RequestParam(defaultValue = "hotel_id") String option, @RequestParam(defaultValue = "") String value, Model model) {
        Map<String, String> map = new HashMap<>();
        map.put("option", option);
        map.put("value", value);
        IPage<Hotel> page = hotelService.getAll(map);
        model.addAttribute("list", page.getRecords());
        return "hotel_display";
    }


    @PreAuthorize("hasAuthority('hotel.update')")
    @GetMapping("/hotel/modify/{hotelId}")
    public String modifyPage(@PathVariable Integer hotelId, Model model) {
        Hotel hotel = hotelService.getById(hotelId);
        model.addAttribute("hotel", hotel);
        return "hotel_modify";
    }

    @PreAuthorize("hasAuthority('registeration.handle')")
    @GetMapping("/hotel/registeration/pending")
    public String getPendingRegisteration(Model model) {
        List<HotelRegisteration> pendingList = hotelService.getPendingRegisteration();
        model.addAttribute("list", pendingList);
        model.addAttribute("type", "pending");
        return "registeration_hotel";
    }

    @PreAuthorize("hasAuthority('registeration.handle')")
    @GetMapping("/hotel/registeration/history")
    public String searchRegisteration(Model model) {
        Map<String, String> map = new HashMap<>();
        map.put("option", "id");
        map.put("value", "");
        List<HotelRegisteration> searchList = hotelService.searchRegisteration(map);
        model.addAttribute("list", searchList);
        model.addAttribute("type", "history");
        return "registeration_hotel";
    }

    @PreAuthorize("hasAnyAuthority('room.select', 'room.display')")
    @GetMapping("/hotel/{hotelId}/room/{type}/{pageNum}")
    public String getRoomPage(@PathVariable Integer hotelId, @PathVariable String type, @PathVariable Integer pageNum,
                              @RequestParam(defaultValue = "room_id") String option,
                              @RequestParam(defaultValue = "") String value, Model model, HttpSession session) {
        Map<String,String> map = new HashMap<>();
        map.put("option", option);
        map.put("value", value);
        map.put("hotel_id", hotelId.toString());
        model.addAttribute("hotelId", hotelId);
        if (type.equals("index")) {
            model.addAttribute("roomType", roomService.getRoomTypeName(hotelId));
        } else {
            map.put("status", "0");
            User user = (User) session.getAttribute("user");
            List<Integer> reserveList = roomService.getPendingRoomIdByUID(user.getUid());
            model.addAttribute("reserveList", reserveList);
        }
        IPage<Room> page = roomService.getRoomPage(map, pageNum);
        model.addAttribute("map", map);
        model.addAttribute("page", page);
        return "room_" + type;
    }

    @PreAuthorize("hasAnyAuthority('reserve.add', 'reserve.handle')")
    @GetMapping("/room/reservation/pending")
    public String getPendingReservation(Model model) {
        IPage<Reservation> page = roomService.getPendingReservation();
        model.addAttribute("list", page.getRecords());
        return "reservation_hotel";
    }
    
    @PreAuthorize("hasAnyAuthority('reserve.add', 'reserve.handle')")
    @GetMapping("/room/reservation/history")
    public String getReservationHistory(Model model, HttpSession session) {
        User user = (User) session.getAttribute("user");
        List<String> roles = (List<String>) session.getAttribute("roles");
        if (roles.contains("Guest")) {
            Map<String, String> map = new HashMap<>();
            map.put("option", "id");
            map.put("value", "");
            map.put("user_id", user.getUid().toString());
            IPage<Reservation> page = roomService.searchReservation(map);
            model.addAttribute("list", page.getRecords());
        } else {
            List<Reservation> reservations = roomService.getReservationByMID(user.getUid());
            model.addAttribute("list", reservations);
        }
        return "reservation_history";
    }

    @PreAuthorize("hasAuthority('checkin.history')")
    @GetMapping("/hotel/room/{roomId}/checkin/history")
    public String getCheckinHistory(@PathVariable String roomId, @RequestParam(defaultValue = "id") String option, @RequestParam(defaultValue = "") String value, Model model) {
        Map<String, String> map = new HashMap<>();
        map.put("option", option);
        map.put("value", value);
        map.put("room_id", roomId);
        map.put("checkin_time", "not null");
        IPage<Reservation> page = roomService.searchReservation(map);
        model.addAttribute("list", page.getRecords());
        model.addAttribute("id", roomId);
        return "checkin_history";
    }

    @PreAuthorize("hasAuthority('facility.select')")
    @GetMapping("/hotel/{hotelId}/room/{roomId}/facility/{count}")
    public String selectFacility(@PathVariable Integer hotelId, @PathVariable Integer roomId, @PathVariable Integer count, @RequestParam(defaultValue = "") String name, Model model) {
        model.addAttribute("list", applicationService.getAllFacility(name, hotelId));
        model.addAttribute("facilities", applicationService.getAllFacilityByRoom(roomId));
        model.addAttribute("count", count);
        model.addAttribute("roomId", roomId);
        return "facility_index";
    }

    @PreAuthorize("hasAuthority('type.select')")
    @GetMapping("/hotel/{hotelId}/room/roomType/{count}")
    public String selectType(@PathVariable Integer hotelId, @PathVariable Integer count, @RequestParam(defaultValue = "") String name, Model model) {
        model.addAttribute("list", applicationService.getAllRoomType(name, hotelId));
        model.addAttribute("count", count);
        return "type_index";
    }

    @PreAuthorize("hasAuthority('application.handle')")
    @GetMapping("/applicate/index")
    public String selectApplication(@RequestParam(defaultValue = "id") String option, @RequestParam(defaultValue = "") String value, Model model, HttpSession session) {
        List<HotelApplication> list = applicationService.getPendingApplication(option, value);
        model.addAttribute("list", list);
        return "application_hotel";
    }
    
    @PreAuthorize("hasAuthority('application.history')")
    @GetMapping("/applicate/history/{type}")
    public String selectApplicationHistory(@PathVariable String type, @RequestParam(defaultValue = "id") String option, @RequestParam(defaultValue = "") String value, Model model, HttpSession session) {
        Map<String, String> map = new HashMap<>();
        User manager = (User) session.getAttribute("user");
        String uid = type.equals("manager") ? manager.getUid().toString() : "not null";
        map.put("option", option);
        map.put("value", value);
        map.put("manager_id", uid);
        List<HotelApplication> list = applicationService.searchApplication(map);
        model.addAttribute("list", list);
        return "application_history";
    }


    //mobile application
    @PreAuthorize("hasAuthority('hotel.display')")
    @GetMapping("/public/hotels")
    public ResponseEntity<List<Hotel>> getAllHotelsWithCities(){
        List<Hotel> hotels = hotelService.selectAllHotels();
        return ResponseEntity.ok(hotels);
    }

}

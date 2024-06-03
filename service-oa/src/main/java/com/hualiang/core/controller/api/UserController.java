package com.hualiang.core.controller.api;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;

import com.hualiang.core.service.ReservationService;
import com.hualiang.model.Reservation;
import com.hualiang.model.ReservationDetail;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hualiang.common.annotation.RequestLimit;
import com.hualiang.common.result.Result;
import com.hualiang.common.result.ResultCodeEnum;
import com.hualiang.common.utils.MailUtils;
import com.hualiang.core.service.UserService;
import com.hualiang.model.User;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "用户管理接口", description = "包含用户的增删改查以及登录注册等接口")
@Validated
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private ReservationService reservationService;

    private static String html;

    static {
        InputStream in = HotelController.class.getResourceAsStream("/verification.xml");
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(in));
        StringBuffer content = new StringBuffer();
        String line;
        try {
            while ((line = bufferedReader.readLine()) != null) {
                content.append(line);
            }
            html = content.toString();
            bufferedReader.close();
        } catch (IOException e) {
            html = null;
            e.printStackTrace();
        }
    }

    @RequestLimit
    @Operation(summary = "用户注册")
    @PostMapping("/register")
    public Result<Integer> register(@NotBlank(message = "验证码不能为空！") String code, @Validated @RequestBody User users, HttpSession session) {
        String verifyCode = (String) session.getAttribute("captcha");
        if (verifyCode != null && code.equals(verifyCode)) {
            Integer UID = userService.register(users);
            return UID != null ? Result.ok(UID).message("注册成功，请重新登录") : Result.fail(UID).message("注册失败，用户名已存在");
        } else {
            return Result.build(null, ResultCodeEnum.VERIFICATION_CODE_FAILED);
        }
    }

    @RequestLimit
    @Operation(summary = "发送验证邮箱")
    @GetMapping("/email")
    public Result<String> verifyEmail(@Email(message = "邮箱格式不正确") String email, HttpSession session) {
        String code = String.valueOf((int) ((Math.random() * 9 + 1) * 100000));
        session.setAttribute("captcha", code);
        String replacedContent = html.replace("<span>", "<span>" + code);
        boolean status = MailUtils.sendMail(email, replacedContent, "酒店预订系统");
        return status ? Result.ok(code).message("验证码发送成功") : Result.build(null, ResultCodeEnum.VERIFICATION_CODE_ERROR);
    }

    @Operation(summary = "添加用户")
    @PreAuthorize("hasAuthority('user.add')")
    @PostMapping("/crud")
    public Result<Integer> addUser(@Validated @RequestBody User user) {
        Integer UID = userService.register(user);
        return UID != null ? Result.ok(UID).message("添加成功") : Result.fail(UID).message("用户名已存在");
    }

    @Operation(summary = "删除用户")
    @PreAuthorize("hasAuthority('user.delete')")
    @DeleteMapping("/crud/{id}")
    public Result<Object> delUser(@PathVariable("id") Integer id) {
        Boolean result = userService.removeById(id);
        return result ? Result.ok().message("删除成功") : Result.fail().message("删除失败");
    }

    @Operation(summary = "修改用户信息")
    @PreAuthorize("hasAuthority('user.update')")
    @PutMapping("/crud")
    public Result<Object> updateUser(@RequestBody User user) {
        user.setPassword(userService.getById(user.getUid()).getPassword());
        Boolean result = userService.updateById(user);
        return result ? Result.ok().message("修改成功") : Result.fail().message("修改失败");
    }
    
    @Operation(summary = "修改用户个人信息")
    @PutMapping("/crud/myself")
    public Result<Object> updateMyself(@RequestBody User newUser, HttpSession session) {
        User user = (User) session.getAttribute("user");
        newUser.setUid(user.getUid());
        newUser.setPassword(user.getPassword());
        Boolean result = userService.updateById(newUser);
        if (result) {
            session.setAttribute("user", newUser);
            return Result.ok().message("修改成功");
        }
        return Result.fail().message("修改失败");
    }

    @Operation(summary = "修改用户个人密码")
    @PutMapping("/repwd")
    public Result<Object> repwd(@RequestBody Map<String,String> list, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (passwordEncoder.matches(list.get("oldPwd"), user.getPassword())) {
            user.setPassword(passwordEncoder.encode(list.get("newPwd")));
            Boolean result = userService.updateById(user);
            return result ? Result.ok().message("修改成功") : Result.fail().message("修改失败");
        } else {
            return Result.fail().message("原密码错误");
        }
    }

    @RequestLimit(maxCount = 1, second = 10)
    @Operation(summary = "获取用户信息")
    @PreAuthorize("hasAuthority('user.query')")
    @GetMapping("/crud/{uid}")
    public Result<User> getUser(@PathVariable("uid") Integer uid) {
        User user = userService.getById(uid);
        return user != null ? Result.ok(user).message("客户信息获取成功") : Result.fail(user).message("找不到该用户ID");
    }


    //Mobile
    @Operation(summary = "用户获取订单详情")
    @PreAuthorize("hasAnyAuthority('order.detail')")
    @GetMapping("/getOrder/detail/{orderId}")
    public ResponseEntity<List<ReservationDetail>> getOrderDetails(@PathVariable("orderId") Integer orderId){
        List<ReservationDetail> res = reservationService.getUserReservationDetailsByUid(orderId);
        return ResponseEntity.ok(res);
    }
    @PreAuthorize("hasAnyAuthority('reserve.history')")
    @GetMapping("/checkorder/{uid}")
    public ResponseEntity<List<Reservation>> getOrderByUid(@PathVariable("uid") Integer uid){
        List<Reservation> reservations = reservationService.getUserReservationsByUid(uid);
        return ResponseEntity.ok(reservations);
    }

}

package com.music.controller;

import com.music.common.Result;
import com.music.dto.LoginDTO;
import com.music.dto.RegisterDTO;
import com.music.dto.UserUpdateDTO;
import com.music.service.UserService;
import com.music.vo.LoginVO;
import com.music.vo.UserVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "用户模块", description = "注册、登录、用户信息查询与修改")
@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @Operation(summary = "用户注册", description = "创建新用户。公开接口，无需登录。")
    @PostMapping("/register")
    public Result<?> register(@RequestBody RegisterDTO dto) {
        userService.register(dto);
        return Result.success();
    }

    @Operation(summary = "用户登录", description = "校验账号密码，返回 JWT token 和用户信息。公开接口，无需登录。")
    @PostMapping("/login")
    public Result<LoginVO> login(@RequestBody LoginDTO dto) {
        LoginVO vo = userService.login(dto);
        return Result.success(vo);
    }

    @Operation(summary = "获取当前用户信息", description = "返回登录用户信息。需登录（携带 Authorization: Bearer <token>）。")
    @GetMapping("/info")
    public Result<UserVO> info(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        UserVO vo = userService.getUserInfo(userId);
        return Result.success(vo);
    }

    @Operation(summary = "修改用户信息", description = "修改当前用户昵称/头像。需登录（携带 Authorization: Bearer <token>）。")
    @PutMapping("/info")
    public Result<UserVO> updateInfo(@RequestBody UserUpdateDTO dto, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        UserVO vo = userService.updateUserInfo(userId, dto.getNickname(), dto.getAvatarUrl());
        return Result.success(vo);
    }
}

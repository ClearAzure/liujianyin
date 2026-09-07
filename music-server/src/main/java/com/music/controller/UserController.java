package com.music.controller;

import com.music.common.Result;
import com.music.dto.LoginDTO;
import com.music.dto.RegisterDTO;
import com.music.service.UserService;
import com.music.vo.LoginVO;
import com.music.vo.UserVO;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/register")
    public Result<?> register(@RequestBody RegisterDTO dto) {
        userService.register(dto);
        return Result.success();
    }

    @PostMapping("/login")
    public Result<LoginVO> login(@RequestBody LoginDTO dto) {
        LoginVO vo = userService.login(dto);
        return Result.success(vo);
    }

    @GetMapping("/info")
    public Result<UserVO> info(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        UserVO vo = userService.getUserInfo(userId);
        return Result.success(vo);
    }
}

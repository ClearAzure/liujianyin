package com.music.controller;

import com.music.common.Result;
import com.music.dto.ArtistUpdateDTO;
import com.music.service.ArtistService;
import com.music.service.UserService;
import com.music.vo.ArtistVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "歌手模块", description = "歌手列表与详情")
@RestController
@RequestMapping("/api/artist")
@RequiredArgsConstructor
public class ArtistController {

    private final ArtistService artistService;
    private final UserService userService;

    @Operation(summary = "歌手列表", description = "返回所有歌手（含歌曲数）。公开接口，无需登录。")
    @GetMapping("/list")
    public Result<List<ArtistVO>> list() {
        return Result.success(artistService.list());
    }

    @Operation(summary = "歌手详情", description = "根据歌手ID获取歌手信息及其歌曲列表。公开接口，无需登录。")
    @GetMapping("/{id}")
    public Result<ArtistVO> getDetail(
            @Parameter(description = "歌手ID") @PathVariable Long id) {
        return Result.success(artistService.getDetail(id));
    }

    @Operation(summary = "更新歌手头像", description = "修改歌手头像URL。仅管理员。")
    @PutMapping("/{id}")
    public Result<?> updateAvatar(
            @Parameter(description = "歌手ID") @PathVariable Long id,
            @RequestBody ArtistUpdateDTO dto,
            HttpServletRequest request) {

        Long userId = (Long) request.getAttribute("userId");
        userService.requireAdmin(userId);

        artistService.updateAvatar(id, dto.getAvatarUrl());
        return Result.success();
    }
}

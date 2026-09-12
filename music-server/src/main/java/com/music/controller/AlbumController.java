package com.music.controller;

import com.music.common.Result;
import com.music.dto.AlbumUpdateDTO;
import com.music.service.AlbumService;
import com.music.service.UserService;
import com.music.vo.AlbumVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "专辑模块", description = "专辑列表与详情")
@RestController
@RequestMapping("/api/album")
@RequiredArgsConstructor
public class AlbumController {

    private final AlbumService albumService;
    private final UserService userService;

    @Operation(summary = "专辑列表", description = "返回所有专辑（含歌曲数）。公开接口，无需登录。")
    @GetMapping("/list")
    public Result<List<AlbumVO>> list() {
        return Result.success(albumService.list());
    }

    @Operation(summary = "专辑详情", description = "根据专辑ID获取专辑信息及其歌曲列表。公开接口，无需登录。")
    @GetMapping("/{id}")
    public Result<AlbumVO> getDetail(
            @Parameter(description = "专辑ID") @PathVariable Long id) {
        return Result.success(albumService.getDetail(id));
    }

    @Operation(summary = "更新专辑封面", description = "修改专辑封面URL。仅管理员。")
    @PutMapping("/{id}")
    public Result<?> updateCover(
            @Parameter(description = "专辑ID") @PathVariable Long id,
            @RequestBody AlbumUpdateDTO dto,
            HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        userService.requireAdmin(userId);
        albumService.updateCover(id, dto.getCoverUrl());
        return Result.success();
    }
}

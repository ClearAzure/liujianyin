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

    @Operation(summary = "更新歌手", description = "修改歌手名称/头像/简介。仅管理员。")
    @PutMapping("/{id}")
    public Result<?> update(
            @Parameter(description = "歌手ID") @PathVariable Long id,
            @RequestBody ArtistUpdateDTO dto,
            HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        userService.requireAdmin(userId);
        artistService.update(id, dto.getName(), dto.getAvatarUrl(), dto.getDescription());
        return Result.success();
    }

    @Operation(summary = "删除歌手", description = "删除歌手及其名下所有歌曲/专辑。仅管理员。")
    @DeleteMapping("/{id}")
    public Result<?> delete(
            @Parameter(description = "歌手ID") @PathVariable Long id,
            HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        userService.requireAdmin(userId);
        artistService.delete(id);
        return Result.success();
    }
}

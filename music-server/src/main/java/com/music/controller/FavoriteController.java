package com.music.controller;

import com.music.common.Result;
import com.music.dto.FavoriteAddDTO;
import com.music.service.FavoriteService;
import com.music.vo.MusicVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "收藏模块", description = "收藏歌曲、取消收藏、收藏列表")
@RestController
@RequestMapping("/api/favorite")
@RequiredArgsConstructor
public class FavoriteController {

    private final FavoriteService favoriteService;

    @Operation(summary = "收藏歌曲", description = "将指定歌曲加入收藏。需登录。")
    @PostMapping("/add")
    public Result<?> add(@RequestBody FavoriteAddDTO dto, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        favoriteService.add(userId, dto.getMusicId());
        return Result.success();
    }

    @Operation(summary = "取消收藏", description = "将指定歌曲移出收藏。需登录。")
    @DeleteMapping("/{musicId}")
    public Result<?> remove(
            @Parameter(description = "歌曲ID") @PathVariable Long musicId,
            HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        favoriteService.remove(userId, musicId);
        return Result.success();
    }

    @Operation(summary = "我的收藏列表", description = "返回当前用户收藏的歌曲列表。需登录。")
    @GetMapping("/list")
    public Result<List<MusicVO>> list(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        List<MusicVO> list = favoriteService.list(userId);
        return Result.success(list);
    }
}

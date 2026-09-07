package com.music.controller;

import com.music.common.Result;
import com.music.dto.FavoriteAddDTO;
import com.music.service.FavoriteService;
import com.music.vo.MusicVO;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/favorite")
@RequiredArgsConstructor
public class FavoriteController {

    private final FavoriteService favoriteService;

    @PostMapping("/add")
    public Result<?> add(@RequestBody FavoriteAddDTO dto, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        favoriteService.add(userId, dto.getMusicId());
        return Result.success();
    }

    @DeleteMapping("/{musicId}")
    public Result<?> remove(@PathVariable Long musicId, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        favoriteService.remove(userId, musicId);
        return Result.success();
    }

    @GetMapping("/list")
    public Result<List<MusicVO>> list(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        List<MusicVO> list = favoriteService.list(userId);
        return Result.success(list);
    }
}

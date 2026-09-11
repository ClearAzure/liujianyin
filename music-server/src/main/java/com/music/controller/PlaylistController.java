package com.music.controller;

import com.music.common.Result;
import com.music.dto.AddMusicDTO;
import com.music.dto.PlaylistCreateDTO;
import com.music.dto.PlaylistUpdateDTO;
import com.music.entity.Playlist;
import com.music.service.PlaylistService;
import com.music.vo.PlaylistVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "歌单模块", description = "歌单的创建、查询、增删歌曲、修改与删除")
@RestController
@RequestMapping("/api/playlist")
@RequiredArgsConstructor
public class PlaylistController {

    private final PlaylistService playlistService;

    //增
    @Operation(summary = "创建歌单", description = "为当前用户创建一个歌单。需登录。")
    @PostMapping("/create")
    public Result<Playlist> create(@RequestBody PlaylistCreateDTO dto, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        Playlist playlist = playlistService.create(userId, dto.getName());
        return Result.success(playlist);
    }
    //查
    @Operation(summary = "我的歌单列表", description = "返回当前用户的所有歌单（含歌曲列表）。需登录。")
    @GetMapping("/my")
    public Result<List<PlaylistVO>> my(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        List<PlaylistVO> list = playlistService.getMyPlaylists(userId);
        return Result.success(list);
    }
    //改
    @Operation(summary = "修改歌单", description = "修改歌单名称/封面。需登录且是歌单所有者。")
    @PutMapping("/{id}")
    public Result<?> update(
            @Parameter(description = "歌单ID") @PathVariable Long id,
            @RequestBody PlaylistUpdateDTO dto,
            HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        playlistService.update(id, userId, dto.getName(), dto.getCoverUrl());
        return Result.success();
    }
    //删
    @Operation(summary = "删除歌单", description = "删除指定歌单。需登录且是歌单所有者。")
    @DeleteMapping("/{id}")
    public Result<?> delete(
            @Parameter(description = "歌单ID") @PathVariable Long id,
            HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        playlistService.delete(id, userId);
        return Result.success();
    }

    //歌单内部:歌单下的歌曲操作
    @Operation(summary = "歌单详情(查询歌单里面的歌曲)", description = "根据歌单ID获取歌单详情（含歌曲列表）。需登录。")
    @GetMapping("/{id}")
    public Result<PlaylistVO> getDetail(
            @Parameter(description = "歌单ID") @PathVariable Long id) {
        PlaylistVO vo = playlistService.getDetail(id);
        return Result.success(vo);
    }
    @Operation(summary = "添加歌曲到歌单", description = "将指定歌曲加入指定歌单。需登录。")
    @PostMapping("/addMusic")
    public Result<?> addMusic(@RequestBody AddMusicDTO dto) {
        playlistService.addMusic(dto.getPlaylistId(), dto.getMusicId());
        return Result.success();
    }
    @Operation(summary = "从歌单移除歌曲", description = "从指定歌单移除指定歌曲。需登录且是歌单所有者。")
    @DeleteMapping("/{id}/music/{musicId}")
    public Result<?> removeMusic(
            @Parameter(description = "歌单ID") @PathVariable Long id,
            @Parameter(description = "歌曲ID") @PathVariable Long musicId,
            HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");

        playlistService.removeMusic(id, userId, musicId);

        return Result.success();
    }



    //增删改查歌单
    //增删查 歌单里面的歌曲
}

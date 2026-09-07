package com.music.controller;

import com.music.common.Result;
import com.music.dto.AddMusicDTO;
import com.music.dto.PlaylistCreateDTO;
import com.music.dto.PlaylistUpdateDTO;
import com.music.entity.Playlist;
import com.music.service.PlaylistService;
import com.music.vo.PlaylistVO;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/playlist")
@RequiredArgsConstructor
public class PlaylistController {

    private final PlaylistService playlistService;

    @PostMapping("/create")
    public Result<Playlist> create(@RequestBody PlaylistCreateDTO dto, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        Playlist playlist = playlistService.create(userId, dto.getName());
        return Result.success(playlist);
    }

    @GetMapping("/my")
    public Result<List<PlaylistVO>> my(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        List<PlaylistVO> list = playlistService.getMyPlaylists(userId);
        return Result.success(list);
    }

    @GetMapping("/{id}")
    public Result<PlaylistVO> getDetail(@PathVariable Long id) {
        PlaylistVO vo = playlistService.getDetail(id);
        return Result.success(vo);
    }

    @PostMapping("/addMusic")
    public Result<?> addMusic(@RequestBody AddMusicDTO dto) {
        playlistService.addMusic(dto.getPlaylistId(), dto.getMusicId());
        return Result.success();
    }

    @DeleteMapping("/{id}/music/{musicId}")
    public Result<?> removeMusic(@PathVariable Long id, @PathVariable Long musicId, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        playlistService.removeMusic(id, userId, musicId);
        return Result.success();
    }

    @PutMapping("/{id}")
    public Result<?> update(@PathVariable Long id, @RequestBody PlaylistUpdateDTO dto, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        playlistService.update(id, userId, dto.getName(), dto.getCoverUrl());
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Long id, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        playlistService.delete(id, userId);
        return Result.success();
    }
}

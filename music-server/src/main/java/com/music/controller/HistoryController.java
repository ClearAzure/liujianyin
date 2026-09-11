package com.music.controller;

import com.music.common.Result;
import com.music.dto.HistoryAddDTO;
import com.music.service.HistoryService;
import com.music.vo.MusicVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "历史模块", description = "播放历史的记录与查询")
@RestController
@RequestMapping("/api/history")
@RequiredArgsConstructor
public class HistoryController {

    private final HistoryService historyService;

    @Operation(summary = "记录播放历史", description = "记录一次播放历史。需登录。")
    @PostMapping("/add")
    public Result<?> add(@RequestBody HistoryAddDTO dto, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        historyService.add(userId, dto.getMusicId());
        return Result.success();
    }

    @Operation(summary = "最近播放列表", description = "返回当前用户最近播放的歌曲（按歌曲去重，最近 50 首）。需登录。")
    @GetMapping("/list")
    public Result<List<MusicVO>> list(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        List<MusicVO> list = historyService.list(userId);
        return Result.success(list);
    }
}

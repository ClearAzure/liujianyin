package com.music.controller;

import com.music.common.Result;
import com.music.dto.HistoryAddDTO;
import com.music.service.HistoryService;
import com.music.vo.MusicVO;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/history")
@RequiredArgsConstructor
public class HistoryController {

    private final HistoryService historyService;

    @PostMapping("/add")
    public Result<?> add(@RequestBody HistoryAddDTO dto, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        historyService.add(userId, dto.getMusicId());
        return Result.success();
    }

    @GetMapping("/list")
    public Result<List<MusicVO>> list(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        List<MusicVO> list = historyService.list(userId);
        return Result.success(list);
    }
}

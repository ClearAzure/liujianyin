package com.music.controller;

import com.music.common.Result;
import com.music.service.MusicService;
import com.music.vo.MusicVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/music")
@RequiredArgsConstructor
public class MusicController {

    private final MusicService musicService;

    //搜索音乐接口，返回搜索结果列表
    @GetMapping("/search")
    public Result<List<MusicVO>> search(@RequestParam String keyword) {//RequestParam是用来获取query参数的注解，keyword是前端传过来的搜索关键字
        List<MusicVO> list = musicService.search(keyword);
        return Result.success(list);
    }

    //获取音乐详情接口，返回音乐详情信息
    @GetMapping("/{id}")
    public Result<MusicVO> getDetail(@PathVariable Long id) {
        MusicVO vo = musicService.getDetail(id);
        return Result.success(vo);
    }

    //获取随机音乐接口，返回随机音乐信息
    @GetMapping("/random")
    public Result<MusicVO> random() {
        MusicVO vo = musicService.random();
        return Result.success(vo);
    }
}

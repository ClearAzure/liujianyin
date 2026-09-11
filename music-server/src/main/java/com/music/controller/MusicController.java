package com.music.controller;

import com.music.common.Result;
import com.music.service.MusicService;
import com.music.vo.MusicVO;
import com.music.vo.PageResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "音乐模块", description = "歌曲搜索、详情、播放、随机、热门推荐")
@RestController
@RequestMapping("/api/music")
@RequiredArgsConstructor//构造器注入。而且因为只有一个构造方法，Spring 可以自动使用它，不需要再写 @Autowired。
public class MusicController {

    private final MusicService musicService;

    @Operation(summary = "搜索音乐", description = "按歌名关键字模糊搜索在架歌曲，返回歌曲列表。公开接口，无需登录。")
    @GetMapping("/search")
    public Result<List<MusicVO>> search(
            @Parameter(description = "搜索关键字（歌名模糊匹配）") @RequestParam String keyword) {//取URL 里的 Query Parameter（查询参数
        List<MusicVO> list = musicService.search(keyword);
        return Result.success(list);
    }

    @Operation(summary = "获取音乐详情", description = "根据歌曲ID获取歌曲信息（不增加播放量）。公开接口，无需登录。(废弃接口)")
    @GetMapping("/{id}")
    public Result<MusicVO> getDetail(
            @Parameter(description = "歌曲ID") @PathVariable Long id) {
        MusicVO vo = musicService.getVO(id);
        return Result.success(vo);
    }

    @Operation(summary = "记录一次播放", description = "播放次数 +1 并返回最新播放量。需登录（携带 Authorization: Bearer <token>）。")
    @PostMapping("/{id}/play")
    public Result<Long> play(
            @Parameter(description = "歌曲ID") @PathVariable Long id) {
        Long count = musicService.incrementPlayCount(id);
        return Result.success(count);
    }

    @Operation(summary = "获取随机音乐", description = "随机返回一首在架歌曲。公开接口，无需登录。")
    @GetMapping("/random")
    public Result<MusicVO> random() {
        MusicVO vo = musicService.random();
        return Result.success(vo);
    }

    @Operation(summary = "热门推荐", description = "按播放量倒序分页返回在架歌曲列表。公开接口，无需登录。")
    @GetMapping("/hot")
    public Result<PageResult<MusicVO>> hot(
            @Parameter(description = "页码，从 1 开始") @RequestParam(value = "page", defaultValue = "1") int page,
            @Parameter(description = "每页条数") @RequestParam(value = "size", defaultValue = "20") int size) {
        PageResult<MusicVO> result = musicService.hot(page, size);
        return Result.success(result);
    }
}

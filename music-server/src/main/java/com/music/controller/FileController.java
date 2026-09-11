package com.music.controller;

import com.music.common.Result;
import com.music.service.FileService;
import com.music.service.SongUploadService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

@Tag(name = "文件模块", description = "文件上传（音乐/封面/歌词）与一键上传歌曲")
@RestController
@RequestMapping("/api/file")
@RequiredArgsConstructor
public class FileController {

    private final FileService fileService;//上传文件到MinIO,返回url
    private final SongUploadService songUploadService;//将url及其基本信息保存到数据库

    @Operation(summary = "上传音乐文件", description = "上传 mp3 到 MinIO，返回 { url }。需登录。")
    @PostMapping("/upload/music")
    public Result<?> uploadMusic(
            @Parameter(description = "音乐文件") @RequestParam("file") MultipartFile file) {
        String url = fileService.uploadMusic(file);
        Map<String, String> result = new HashMap<>();
        result.put("url", url);
        return Result.success(result);
    }

    @Operation(summary = "上传封面图片", description = "上传封面图片到 MinIO，返回 { url }。需登录。")
    @PostMapping("/upload/image")
    public Result<?> uploadCover(
            @Parameter(description = "图片文件") @RequestParam("file") MultipartFile file) {
        String url = fileService.uploadCover(file);
        Map<String, String> result = new HashMap<>();
        result.put("url", url);
        return Result.success(result);
    }

    @Operation(summary = "上传歌词文件", description = "上传 lrc 歌词到 MinIO，返回 { url }。需登录。")
    @PostMapping("/upload/lyric")
    public Result<?> uploadLyric(
            @Parameter(description = "歌词文件") @RequestParam("file") MultipartFile file) {
        String url = fileService.uploadLyric(file);
        Map<String, String> result = new HashMap<>();
        result.put("url", url);
        return Result.success(result);
    }

    @Operation(summary = "一键上传歌曲", description = "上传歌曲（音频+封面+歌词）并自动创建歌手/专辑、写入 music 表。返回 name、artistId、artistName、albumId、albumName、musicUrl、coverUrl、lyricUrl、musicId、success 等字段。")
    @PostMapping("/upload/song")
    public Result<?> uploadSong(
@Parameter(description = "歌曲名")                @RequestParam("name") String name,
@Parameter(description = "歌手名（不存在则自动创建）")@RequestParam("artistName") String artistName,
@Parameter(description = "专辑名（可选）")         @RequestParam(value = "albumName", required = false) String albumName,
@Parameter(description = "时长（秒，可选，默认 0）") @RequestParam(value = "duration", required = false, defaultValue = "0") Integer duration,
@Parameter(description = "音乐文件（可选）")        @RequestParam(value = "musicFile", required = false) MultipartFile musicFile,
@Parameter(description = "封面图片（可选）")        @RequestParam(value = "coverFile", required = false) MultipartFile coverFile,
@Parameter(description = "歌词文件（可选）")        @RequestParam(value = "lyricFile", required = false) MultipartFile lyricFile) {
        Map<String, Object> result = songUploadService.uploadSong(
                name, artistName, albumName, duration, musicFile, coverFile, lyricFile);
        return Result.success(result);
    }
}

package com.music.controller;

import com.music.common.Result;
import com.music.service.FileService;
import com.music.service.SongUploadService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/file")
@RequiredArgsConstructor
public class FileController {

    private final FileService fileService;
    private final SongUploadService songUploadService;

    @PostMapping("/upload/music")
    public Result<?> uploadMusic(@RequestParam("file") MultipartFile file) {
        String url = fileService.uploadMusic(file);
        Map<String, String> result = new HashMap<>();
        result.put("url", url);
        return Result.success(result);
    }

    @PostMapping("/upload/image")
    public Result<?> uploadCover(@RequestParam("file") MultipartFile file) {
        String url = fileService.uploadCover(file);
        Map<String, String> result = new HashMap<>();
        result.put("url", url);
        return Result.success(result);
    }

    @PostMapping("/upload/lyric")
    public Result<?> uploadLyric(@RequestParam("file") MultipartFile file) {
        String url = fileService.uploadLyric(file);
        Map<String, String> result = new HashMap<>();
        result.put("url", url);
        return Result.success(result);
    }

    /**
     * 临时接口：一键上传歌曲（mp3 + 封面 + 歌词）
     * POST /api/file/upload/song
     * multipart/form-data:
     *   name        - 歌曲名
     *   artistName  - 歌手名（不存在则自动创建）
     *   albumName   - 专辑名（可选）
     *   musicFile   - mp3文件
     *   coverFile   - 封面图片（可选）
     *   lyricFile   - lrc歌词文件（可选）
     */
    @PostMapping("/upload/song")
    public Result<?> uploadSong(
            @RequestParam("name") String name,
            @RequestParam("artistName") String artistName,
            @RequestParam(value = "albumName", required = false) String albumName,
            @RequestParam(value = "musicFile", required = false) MultipartFile musicFile,
            @RequestParam(value = "coverFile", required = false) MultipartFile coverFile,
            @RequestParam(value = "lyricFile", required = false) MultipartFile lyricFile) {
        Map<String, Object> result = songUploadService.uploadSong(
                name, artistName, albumName, musicFile, coverFile, lyricFile);
        return Result.success(result);
    }
}

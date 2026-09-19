package com.music.service;

import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

/**
 * 歌曲上传服务接口
 */
public interface SongUploadService {

    /**
     * 一键上传歌曲：查找/创建歌手、创建专辑、上传文件、写入 music 表
     *
     * @param name              歌曲名
     * @param artistName        歌手名（不存在则自动创建）
     * @param albumName         专辑名（可空）
     * @param duration          时长（秒，可空）
     * @param musicFile         音乐文件（可空）
     * @param coverFile         封面图片（可空）
     * @param lyricFile         歌词文件（可空）
     * @param artistAvatarFile  歌手头像图片（可空）
     * @return 上传结果 Map，包含 name、artistId、artistName、albumId、albumName、musicUrl、coverUrl、lyricUrl、musicId、success 等字段
     */
    Map<String, Object> uploadSong(
            String name,
            String artistName,
            String albumName,
            Integer duration,
            MultipartFile musicFile,
            MultipartFile coverFile,
            MultipartFile lyricFile,
            MultipartFile artistAvatarFile
    );
}

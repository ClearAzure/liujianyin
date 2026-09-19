package com.music.service;

import org.springframework.web.multipart.MultipartFile;

/**
 * 文件服务接口
 */
public interface FileService {

    /**
     * 上传音乐文件到 MinIO
     *
     * @param file 音乐文件
     * @return 文件访问 URL
     */
    String uploadMusic(MultipartFile file);

    /**
     * 上传封面图片到 MinIO
     *
     * @param file 图片文件
     * @return 文件访问 URL
     */
    String uploadCover(MultipartFile file);

    /**
     * 上传歌词文件到 MinIO
     *
     * @param file 歌词文件
     * @return 文件访问 URL
     */
    String uploadLyric(MultipartFile file);
}

package com.music.service;

import com.music.config.MinioConfig;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FileService {

    private final MinioClient minioClient;
    private final MinioConfig minioConfig;

    /**
     * 上传音乐文件到 MinIO。
     *
     * @param file 音乐文件
     * @return 文件访问URL
     */
    public String uploadMusic(MultipartFile file) {
        return upload(file, minioConfig.getBucketMusic(), "music/");
    }//将文件放在minioConfig.getBucketMusic()桶里的music/目录中

    /**
     * 上传封面图片到 MinIO。
     *
     * @param file 图片文件
     * @return 文件访问URL
     */
    public String uploadCover(MultipartFile file) {
        return upload(file, minioConfig.getBucketCover(), "cover/");
    }

    /**
     * 上传歌词文件到 MinIO。
     *
     * @param file 歌词文件
     * @return 文件访问URL
     */
    public String uploadLyric(MultipartFile file) {
        return upload(file, minioConfig.getBucketLyric(), "lyric/");
    }

    /**
     * 通用上传(真正的上传逻辑)：生成 UUID 文件名，上传到指定桶和目录前缀。
     *
     * @param file 文件
     * @param bucket 桶名
     * @param prefix 对象名前缀（目录）
     * @return 文件访问URL（endpoint + "/" + bucket + "/" + objectName）
     * @throws RuntimeException 上传失败时抛出
     */
    private String upload(MultipartFile file, String bucket, String prefix) {
        try {
            String originalName = file.getOriginalFilename();//获取原来的文件名

            String ext = originalName != null && originalName.contains(".")
                    ? originalName.substring(originalName.lastIndexOf("."))
                    : "";//如果原文件名存在 .，就把最后一个 . 后面的东西取出来；否则返回空字符串

            String objectName = prefix + UUID.randomUUID().toString().substring(0, 8) + ext;//生成一个新的文件名，格式为 prefix + 8位UUID + 原文件扩展名

            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucket)//指定桶名
                            .object(objectName)//指定对象名（文件名）
                            .stream(file.getInputStream(), file.getSize(), -1)//指定文件流、文件大小、未知大小
                            .contentType(file.getContentType())//指定文件类型
                            .build()//构建上传文件参数
            );

            return minioConfig.getEndpoint() + "/" + bucket + "/" + objectName;//自己拼接返回文件访问URL，格式为 endpoint + "/" + bucket + "/" + objectName
        } catch (Exception e) {
            throw new RuntimeException("文件上传失败: " + e.getMessage(), e);
        }
    }
}

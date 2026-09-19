package com.music.service.impl;

import com.music.config.MinioConfig;
import com.music.service.FileService;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

/**
 * 文件服务实现类
 */
@Service
@RequiredArgsConstructor
public class FileServiceImpl implements FileService {

    private final MinioClient minioClient;
    private final MinioConfig minioConfig;

    @Override
    public String uploadMusic(MultipartFile file) {
        return upload(file, minioConfig.getBucketMusic(), "music/");
    }

    @Override
    public String uploadCover(MultipartFile file) {
        return upload(file, minioConfig.getBucketCover(), "cover/");
    }

    @Override
    public String uploadLyric(MultipartFile file) {
        return upload(file, minioConfig.getBucketLyric(), "lyric/");
    }

    /**
     * 通用上传：生成 UUID 文件名，上传到指定桶和目录前缀
     *
     * @param file   文件
     * @param bucket 桶名
     * @param prefix 对象名前缀（目录）
     * @return 文件访问 URL
     * @throws RuntimeException 上传失败时抛出
     */
    private String upload(MultipartFile file, String bucket, String prefix) {
        try {
            String originalName = file.getOriginalFilename();

            String ext = originalName != null && originalName.contains(".")
                    ? originalName.substring(originalName.lastIndexOf("."))
                    : "";

            String objectName = prefix + UUID.randomUUID().toString().substring(0, 8) + ext;

            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucket)
                            .object(objectName)
                            .stream(file.getInputStream(), file.getSize(), -1)
                            .contentType(file.getContentType())
                            .build()
            );

            // 对外 URL 用 publicEndpoint（客户端浏览器直接访问）；没配置时退回 endpoint（本地开发）
            String base = (minioConfig.getPublicEndpoint() != null && !minioConfig.getPublicEndpoint().isBlank())
                    ? minioConfig.getPublicEndpoint()
                    : minioConfig.getEndpoint();
            return base + "/" + bucket + "/" + objectName;
        } catch (Exception e) {
            throw new RuntimeException("文件上传失败：" + e.getMessage(), e);
        }
    }
}

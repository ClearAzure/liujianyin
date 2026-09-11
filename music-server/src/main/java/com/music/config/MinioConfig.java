package com.music.config;

import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.SetBucketPolicyArgs;
import jakarta.annotation.PostConstruct;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Data
@Slf4j
@Configuration
@ConfigurationProperties(prefix = "minio")//读取配置文件中 minio 开头的配置,自动绑定到这个 Java 类的字段上。
public class MinioConfig {
    private String endpoint;//MinIO服务的URL
    private String accessKey;//MinIO服务的访问密钥
    private String secretKey;//MinIO服务的秘密密钥

    private String bucketMusic;//音乐桶
    private String bucketCover;//封面桶
    private String bucketLyric;//歌词桶

    @Bean
    public MinioClient minioClient() {
        return buildClient();
    }

    private MinioClient buildClient() {//配置MinioClient,根据配置文件中的服务器地址和账号密码，创建一个 MinIO 客户端(java操作MinIO用到的类/对象)。
        return MinioClient.builder()
                .endpoint(endpoint)
                .credentials(accessKey, secretKey)
                .build();
    }
    //Spring 创建完这个 Bean，并完成依赖注入之后，自动执行这个方法一次。
    @PostConstruct
    public void initBuckets() {
        try {
            MinioClient client = buildClient();
            String[] buckets = {bucketMusic, bucketCover, bucketLyric};
            for (String bucket : buckets) {

                boolean exists = client.bucketExists(BucketExistsArgs.builder().bucket(bucket).build());
                if (!exists) {//如果桶不存在,就创建桶
                    client.makeBucket(MakeBucketArgs.builder().bucket(bucket).build());
                    log.info("MinIO bucket [{}] 创建成功", bucket);
                }

                // 始终确保公开读策略生效,允许任何人读取这个 Bucket 里面的对象。
                String policy = String.format(
                    "{\"Version\":\"2012-10-17\"," +
                     "\"Statement\":" +
                            "[{\"Effect\":\"Allow\"," +
                            "\"Principal\":{\"AWS\":[\"*\"]}," +
                            "\"Action\":[\"s3:GetObject\"]," +
                            "\"Resource\":[\"arn:aws:s3:::%s/*\"]}]" +
                    "}",
                    bucket
                );
                //把刚才写好的“公开读取规则”设置给这个 Bucket。
                client.setBucketPolicy(SetBucketPolicyArgs.builder().bucket(bucket).config(policy).build());

                log.info("MinIO bucket [{}] 公开读策略已设置", bucket);
            }
        } catch (Exception e) {
            log.error("MinIO bucket 初始化失败", e);
        }
    }
}

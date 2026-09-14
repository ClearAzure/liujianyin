# 运行时镜像：直接用你现成的 JDK，不重新拉 maven/temurin 镜像。
# jar 通过数据卷挂载进来，改 jar 不用重建镜像。
FROM debian:bookworm-slim

# 解压现成的 JDK（--strip-components=1 去掉 tar 里最外层目录，直接落到 /opt/java）
COPY jdk17.tar.gz /tmp/jdk17.tar.gz
RUN mkdir -p /opt/java \
    && tar -xzf /tmp/jdk17.tar.gz -C /opt/java --strip-components=1 \
    && rm /tmp/jdk17.tar.gz \
    && /opt/java/bin/java -version

EXPOSE 8080

# 启动时读 prod 配置（连容器内的 mysql/minio）
ENTRYPOINT ["/opt/java/bin/java", "-jar", "/app/music-server-1.0.0.jar", "--spring.profiles.active=prod"]

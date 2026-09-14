这个目录放后端 jar 包。

本地打包（IntelliJ 里 Maven package，或命令行 mvn -DskipTests package），
产物在 music-server/target/music-server-1.0.0.jar，
把它复制到本目录并保持文件名不变：

    scp target/music-server-1.0.0.jar root@192.168.100.128:/你的部署目录/music-server/jar/

然后在服务器上执行 ./restart.sh 即可让新 jar 生效，无需重建镜像。

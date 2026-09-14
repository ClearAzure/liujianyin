#!/usr/bin/env bash
# 一键启动三容器（首次会拉取 mysql/minio 镜像并构建 backend 镜像）
set -e
cd "$(dirname "$0")"

# 先建好 jar 目录，避免被 docker 当成 root 目录创建
mkdir -p music-server/jar

echo "==> 启动（自动拉镜像 + 构建 backend）..."
docker compose up -d --build

echo ""
docker compose ps
echo ""
echo "查看后端日志： docker compose logs -f backend"
echo "若后端报连不上 MySQL，等 30 秒再执行： docker compose restart backend"

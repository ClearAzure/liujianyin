#!/usr/bin/env bash
# 停止并删除容器（保留 MySQL/MinIO 数据卷，数据不丢）
cd "$(dirname "$0")"
docker compose down
echo "已停止。数据仍在卷里；若连数据也要清空请手动执行： docker compose down -v"

#!/usr/bin/env bash
# 本地重新打包、scp 覆盖 jar 后，只重启后端（不重建镜像）
set -e
cd "$(dirname "$0")"
docker compose restart backend
docker compose logs -f backend

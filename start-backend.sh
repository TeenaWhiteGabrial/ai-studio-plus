#!/usr/bin/env bash

set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
ENV_FILE="${ENV_FILE:-$ROOT_DIR/.env.docker}"
COMPOSE_FILE="$ROOT_DIR/docker-compose.yml"

if ! command -v docker >/dev/null 2>&1; then
  echo "docker 未安装或不在 PATH 中"
  exit 1
fi

if ! docker compose version >/dev/null 2>&1; then
  echo "docker compose 不可用"
  exit 1
fi

if [[ ! -f "$COMPOSE_FILE" ]]; then
  echo "未找到 docker-compose.yml: $COMPOSE_FILE"
  exit 1
fi

if [[ ! -f "$ENV_FILE" ]]; then
  echo "未找到环境变量文件: $ENV_FILE"
  echo "可先执行: cp .env.docker.example .env.docker"
  exit 1
fi

echo "使用环境变量文件: $ENV_FILE"
echo "启动整套服务: ai-studio-service + ai-studio-admin-web + ai-studio-console-web + ai-studio-portal-web"

cd "$ROOT_DIR"
docker compose --env-file "$ENV_FILE" up -d --build

echo "启动完成。查看状态:"
echo "docker compose --env-file $ENV_FILE ps"
echo "查看日志:"
echo "docker compose --env-file $ENV_FILE logs -f"

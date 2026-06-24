#!/usr/bin/env bash

set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
ENV_FILE="${ENV_FILE:-$ROOT_DIR/.env.docker}"
COMPOSE_FILE="$ROOT_DIR/docker-compose.yml"
COMPOSE_CMD=()
CONTAINERS=(
  ai-studio-service
  ai-studio-admin-web
  ai-studio-console-web
  ai-studio-portal-web
)
CONTAINER_PATTERNS=(
  "_ai-studio-service"
  "_ai-studio-admin-web"
  "_ai-studio-console-web"
  "_ai-studio-portal-web"
)

has_cmd() {
  command -v "$1" >/dev/null 2>&1
}

detect_compose() {
  if has_cmd docker-compose; then
    COMPOSE_CMD=(docker-compose)
    return 0
  fi

  if docker compose version >/dev/null 2>&1; then
    COMPOSE_CMD=(docker compose)
    return 0
  fi

  echo "未找到可用的 Docker Compose，请安装 docker-compose 或 docker compose"
  exit 1
}

compose() {
  "${COMPOSE_CMD[@]}" --env-file "$ENV_FILE" -f "$COMPOSE_FILE" "$@"
}

remove_existing_containers() {
  local container_name
  local existing_name
  local pattern

  for container_name in "${CONTAINERS[@]}"; do
    if docker inspect "$container_name" >/dev/null 2>&1; then
      echo "删除旧容器: $container_name"
      docker rm -f "$container_name" >/dev/null
    fi
  done

  while IFS= read -r existing_name; do
    [[ -z "$existing_name" ]] && continue
    for pattern in "${CONTAINER_PATTERNS[@]}"; do
      if [[ "$existing_name" == *"$pattern" ]]; then
        echo "删除兼容性残留容器: $existing_name"
        docker rm -f "$existing_name" >/dev/null
        break
      fi
    done
  done < <(docker ps -a --format '{{.Names}}')
}

if ! command -v docker >/dev/null 2>&1; then
  echo "docker 未安装或不在 PATH 中"
  exit 1
fi

detect_compose

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
echo "使用 Compose 命令: ${COMPOSE_CMD[*]}"
echo "启动整套服务: ai-studio-service + ai-studio-admin-web + ai-studio-console-web + ai-studio-portal-web"

cd "$ROOT_DIR"
remove_existing_containers
compose up -d --build

echo "启动完成。查看状态:"
echo "${COMPOSE_CMD[*]} --env-file $ENV_FILE -f $COMPOSE_FILE ps"
echo "查看日志:"
echo "${COMPOSE_CMD[*]} --env-file $ENV_FILE -f $COMPOSE_FILE logs -f"

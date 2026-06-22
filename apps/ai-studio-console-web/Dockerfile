FROM registry.cn-hangzhou.aliyuncs.com/misaka-images/node:22.21.0 AS build
WORKDIR /app

RUN corepack enable

COPY package.json pnpm-lock.yaml .npmrc ./
COPY apps/ai-studio-console-web /app/apps/ai-studio-console-web

WORKDIR /app/apps/ai-studio-console-web
RUN pnpm install --no-frozen-lockfile
RUN pnpm build

FROM registry.cn-hangzhou.aliyuncs.com/misaka-images/nginx:alpine

COPY apps/ai-studio-console-web/docker/nginx.conf /etc/nginx/conf.d/default.conf
COPY --from=build /app/apps/ai-studio-console-web/dist /usr/share/nginx/html/ai-studio-plus-console-web

EXPOSE 80

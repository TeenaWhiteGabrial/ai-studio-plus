FROM registry.cn-hangzhou.aliyuncs.com/misaka-images/node:22.21.0 AS build
WORKDIR /app

RUN corepack enable

COPY package.json pnpm-lock.yaml .npmrc ./
COPY apps/ai-studio-admin-web /app/apps/ai-studio-admin-web

WORKDIR /app/apps/ai-studio-admin-web
RUN pnpm install --no-frozen-lockfile
RUN pnpm build

FROM registry.cn-hangzhou.aliyuncs.com/misaka-images/nginx:alpine

COPY apps/ai-studio-admin-web/docker/nginx.conf /etc/nginx/conf.d/default.conf
COPY --from=build /app/apps/ai-studio-admin-web/dist /usr/share/nginx/html/ai-studio-plus-admin-web

EXPOSE 80

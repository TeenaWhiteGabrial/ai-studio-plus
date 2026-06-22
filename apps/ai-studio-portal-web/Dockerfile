FROM registry.cn-hangzhou.aliyuncs.com/misaka-images/node:22.21.0 AS build
WORKDIR /app

RUN corepack enable

COPY package.json pnpm-lock.yaml .npmrc ./
COPY apps/ai-studio-portal-web /app/apps/ai-studio-portal-web

WORKDIR /app/apps/ai-studio-portal-web
RUN pnpm install --no-frozen-lockfile
RUN pnpm build:dev

FROM registry.cn-hangzhou.aliyuncs.com/misaka-images/node:22.21.0
WORKDIR /app

ENV NODE_ENV=production
ENV HOST=0.0.0.0
ENV PORT=3000

COPY --from=build /app/apps/ai-studio-portal-web/.output /app/.output

EXPOSE 3000

CMD ["node", ".output/server/index.mjs"]

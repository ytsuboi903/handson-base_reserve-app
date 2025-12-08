# マルチステージビルド: ビルドステージ
FROM node:18-alpine AS build
WORKDIR /app

# package.jsonとpackage-lock.jsonをコピー
COPY package*.json ./

# 依存関係をインストール
RUN npm ci

# ソースコードをコピー
COPY . .

# アプリケーションをビルド
RUN npm run build

# ランタイムステージ
FROM nginx:alpine
WORKDIR /usr/share/nginx/html

# ビルドしたファイルをコピー
COPY --from=build /app/dist .

# nginx設定ファイルをコピー（APIプロキシ用）
COPY nginx.conf /etc/nginx/conf.d/default.conf

# ポート80を公開
EXPOSE 80

# nginxを起動
CMD ["nginx", "-g", "daemon off;"]



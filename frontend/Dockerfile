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
# OpenShift用の設定を使用（一時ディレクトリを/tmpに変更）
COPY nginx-openshift.conf /etc/nginx/conf.d/default.conf

# OpenShift用: 必要なディレクトリを作成し、適切な権限を設定
# /tmpディレクトリを使用（OpenShiftで書き込み可能）
RUN mkdir -p /tmp/nginx && \
    chmod -R 777 /tmp \
    /usr/share/nginx/html \
    /etc/nginx/conf.d \
    /var/log/nginx

# ポート80を公開
EXPOSE 80

# nginxを起動
CMD ["nginx", "-g", "daemon off;"]



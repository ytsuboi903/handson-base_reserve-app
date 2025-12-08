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

# 一時ディレクトリ設定ファイルをコピー
COPY nginx-temp-paths.conf /etc/nginx/conf.d/nginx-temp-paths.conf

# OpenShift用: 必要なディレクトリを作成し、適切な権限を設定
# /tmpディレクトリを使用（OpenShiftで書き込み可能）
RUN mkdir -p /tmp/nginx/client_temp \
    /tmp/nginx/proxy_temp \
    /tmp/nginx/fastcgi_temp \
    /tmp/nginx/uwsgi_temp \
    /tmp/nginx/scgi_temp && \
    chmod -R 777 /tmp/nginx && \
    chmod -R 755 /usr/share/nginx/html && \
    chmod -R 755 /etc/nginx/conf.d && \
    ([ -d /var/log/nginx ] && chmod -R 777 /var/log/nginx || true) && \
    # メイン設定ファイルのpidディレクティブを/tmp/nginx.pidに変更
    sed -i.bak 's|^pid .*|pid /tmp/nginx.pid;|' /etc/nginx/nginx.conf && \
    rm -f /etc/nginx/nginx.conf.bak || true && \
    # メイン設定ファイルのhttpブロック内に一時ディレクトリ設定をインクルード
    # awkコマンドを使用してhttpブロックの直後にincludeを追加
    awk '/^http {/ {print; print "    include /etc/nginx/conf.d/nginx-temp-paths.conf;"; next}1' /etc/nginx/nginx.conf > /tmp/nginx.conf.new && \
    mv /tmp/nginx.conf.new /etc/nginx/nginx.conf || true

# ポート80を公開
EXPOSE 80

# nginxを起動
CMD ["nginx", "-g", "daemon off;"]

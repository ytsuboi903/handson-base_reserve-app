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
RUN mkdir -p /tmp/nginx/client_temp \
    /tmp/nginx/proxy_temp \
    /tmp/nginx/fastcgi_temp \
    /tmp/nginx/uwsgi_temp \
    /tmp/nginx/scgi_temp && \
    chmod -R 777 /tmp/nginx && \
    chmod -R 755 /usr/share/nginx/html && \
    chmod -R 755 /etc/nginx/conf.d && \
    ([ -d /var/log/nginx ] && chmod -R 777 /var/log/nginx || true) && \
    # メイン設定ファイルのhttpブロック内に一時ディレクトリ設定を追加
    # Alpine Linuxのsedを使用（-i.bakでバックアップを作成）
    sed -i.bak '/^http {/a\
    client_body_temp_path /tmp/nginx/client_temp;\
    proxy_temp_path /tmp/nginx/proxy_temp;\
    fastcgi_temp_path /tmp/nginx/fastcgi_temp;\
    uwsgi_temp_path /tmp/nginx/uwsgi_temp;\
    scgi_temp_path /tmp/nginx/scgi_temp;
' /etc/nginx/nginx.conf && rm -f /etc/nginx/nginx.conf.bak || true

# ポート80を公開
EXPOSE 80

# nginxを起動（pidファイルを/tmpに配置）
CMD ["nginx", "-g", "pid /tmp/nginx.pid; daemon off;"]

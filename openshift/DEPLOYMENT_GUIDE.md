# OpenShift（Red Hat Sandbox）へのデプロイガイド

このガイドでは、予約管理システムをOpenShift（Red Hat Sandbox）にデプロイする手順を説明します。

## 前提条件

1. **Red Hat Sandboxアカウント**
   - [Red Hat Developer Sandbox](https://developers.redhat.com/developer-sandbox) にアクセスしてアカウントを作成
   - OpenShiftクラスターへのアクセス権限を取得

2. **必要なツール**
   - `oc` CLI（OpenShift CLI）
   - `docker` または `podman`
   - `git`

## 手順

### 1. OpenShift CLIのインストールとログイン

```bash
# OpenShift CLIをダウンロード（macOSの場合）
# https://mirror.openshift.com/pub/openshift-v4/clients/ocp/latest/

# OpenShiftにログイン
oc login <your-openshift-cluster-url>
# または、Red Hat Sandboxの場合は
oc login --token=<your-token> --server=<your-server-url>
```

### 2. プロジェクトの作成

```bash
# 新しいプロジェクトを作成
oc new-project booking-management-system

# または既存のプロジェクトを使用
oc project booking-management-system
```

### 3. コンテナイメージのビルドとプッシュ

#### 方法1: OpenShiftのBuildConfigを使用（推奨）

```bash
# BuildConfigとImageStreamを作成
cd openshift
oc apply -f build-config.yaml

# バックエンドイメージをビルド
cd ../backend
oc start-build booking-backend --from-dir=. --follow

# フロントエンドイメージをビルド
cd ../frontend
oc start-build booking-frontend --from-dir=. --follow
```

#### 方法2: 手動でBuildConfigを作成

```bash
# バックエンドディレクトリに移動
cd backend

# OpenShiftの内部レジストリを使用してイメージをビルド
oc new-build --name=booking-backend --strategy=docker --binary=true

# イメージをビルド
oc start-build booking-backend --from-dir=. --follow

# フロントエンドディレクトリに移動
cd ../frontend

# OpenShiftの内部レジストリを使用してイメージをビルド
oc new-build --name=booking-frontend --strategy=docker --binary=true

# イメージをビルド
oc start-build booking-frontend --from-dir=. --follow
```

#### 方法3: 外部レジストリを使用（オプション）

```bash
# バックエンドイメージをビルド
cd backend
docker build -t booking-backend:latest .

# OpenShiftの内部レジストリURLを取得
REGISTRY_URL=$(oc get route default-route -n openshift-image-registry --template='{{ .spec.host }}')

# イメージをタグ付け
docker tag booking-backend:latest ${REGISTRY_URL}/booking-management-system/booking-backend:latest

# レジストリにログイン
docker login -u $(oc whoami) -p $(oc whoami -t) ${REGISTRY_URL}

# イメージをプッシュ
docker push ${REGISTRY_URL}/booking-management-system/booking-backend:latest

# フロントエンドも同様に
cd ../frontend
docker build -t booking-frontend:latest .
docker tag booking-frontend:latest ${REGISTRY_URL}/booking-management-system/booking-frontend:latest
docker push ${REGISTRY_URL}/booking-management-system/booking-frontend:latest
```

### 4. デプロイメントの作成

```bash
# openshiftディレクトリに移動
cd ../openshift

# バックエンドをデプロイ
oc apply -f backend-deployment.yaml

# フロントエンドをデプロイ
oc apply -f frontend-deployment.yaml

# Routeを作成（外部アクセス用）
oc apply -f route.yaml
```

### 5. デプロイメントの確認

```bash
# デプロイメントの状態を確認
oc get deployments

# Podの状態を確認
oc get pods

# サービスの状態を確認
oc get services

# RouteのURLを確認
oc get route booking-frontend
```

### 6. ログの確認

```bash
# バックエンドのログを確認
oc logs -f deployment/booking-backend

# フロントエンドのログを確認
oc logs -f deployment/booking-frontend
```

### 7. アプリケーションへのアクセス

```bash
# RouteのURLを取得
oc get route booking-frontend -o jsonpath='{.spec.host}'

# ブラウザでアクセス
# https://<route-url>
```

## トラブルシューティング

### Podが起動しない場合

```bash
# Podの詳細を確認
oc describe pod <pod-name>

# Podのログを確認
oc logs <pod-name>

# イベントを確認
oc get events --sort-by='.lastTimestamp'
```

### イメージのビルドに失敗する場合

```bash
# ビルドのログを確認
oc logs build/<build-name>

# ビルド設定を確認
oc describe buildconfig <build-name>
```

### ヘルスチェックが失敗する場合

```bash
# バックエンドのヘルスチェックエンドポイントを確認
oc exec <pod-name> -- curl http://localhost:8080/actuator/health

# デプロイメントのヘルスチェック設定を確認
oc describe deployment booking-backend
```

## 注意事項

1. **データベース**: 現在はH2インメモリデータベースを使用しています。本番環境では、PostgreSQLなどの永続化データベースを使用することを推奨します。

2. **リソース制限**: Sandbox環境ではリソース制限があるため、必要に応じてリソース要求と制限を調整してください。

3. **セキュリティ**: 本番環境では、適切なセキュリティ設定（Secrets、ConfigMaps、NetworkPoliciesなど）を実装してください。

## CI/CDパイプラインの設定

Git push時に自動的にデプロイする設定については、`CI_CD_SETUP.md`を参照してください。

## 次のステップ

- データベースの永続化（PostgreSQLなど）
- CI/CDパイプラインの構築（`CI_CD_SETUP.md`を参照）
- モニタリングとロギングの設定
- セキュリティポリシーの実装


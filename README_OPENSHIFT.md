# OpenShift（Red Hat Sandbox）へのデプロイ

このドキュメントでは、予約管理システムをOpenShift（Red Hat Sandbox）にデプロイする方法を説明します。

## クイックスタート

### 1. 前提条件

- Red Hat Sandboxアカウント
- OpenShift CLI (`oc`) がインストールされていること
- OpenShiftクラスターへのアクセス権限

### 2. デプロイ手順

```bash
# OpenShiftにログイン
oc login <your-openshift-cluster-url>

# クイックデプロイスクリプトを実行
cd openshift
./QUICK_DEPLOY.sh
```

### 3. アプリケーションへのアクセス

デプロイ完了後、RouteのURLが表示されます。ブラウザでアクセスしてください。

```bash
# RouteのURLを確認
oc get route booking-frontend
```

## 詳細な手順

詳細なデプロイ手順については、`openshift/DEPLOYMENT_GUIDE.md`を参照してください。

## ファイル構成

- `openshift/`: OpenShiftデプロイメント設定ファイル
  - `backend-deployment.yaml`: バックエンドのDeploymentとService
  - `frontend-deployment.yaml`: フロントエンドのDeploymentとService
  - `route.yaml`: 外部アクセス用のRoute
  - `build-config.yaml`: イメージビルド設定
  - `DEPLOYMENT_GUIDE.md`: 詳細なデプロイ手順
  - `QUICK_DEPLOY.sh`: クイックデプロイスクリプト

## トラブルシューティング

### Podが起動しない

```bash
# Podの状態を確認
oc get pods

# Podの詳細を確認
oc describe pod <pod-name>

# ログを確認
oc logs <pod-name>
```

### イメージのビルドに失敗する

```bash
# ビルドの状態を確認
oc get builds

# ビルドのログを確認
oc logs build/<build-name>
```

### ヘルスチェックが失敗する

```bash
# バックエンドのヘルスチェックを確認
oc exec <pod-name> -- curl http://localhost:8080/actuator/health
```

## 注意事項

- 現在はH2インメモリデータベースを使用しています。本番環境では永続化データベース（PostgreSQLなど）の使用を推奨します。
- Sandbox環境ではリソース制限があるため、必要に応じてリソース要求と制限を調整してください。



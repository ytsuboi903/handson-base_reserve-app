# OpenShiftデプロイメント設定

このディレクトリには、OpenShift（Red Hat Sandbox）へのデプロイに必要な設定ファイルが含まれています。

## ファイル構成

- `backend-deployment.yaml`: バックエンドアプリケーションのDeploymentとService定義
- `frontend-deployment.yaml`: フロントエンドアプリケーションのDeploymentとService定義
- `route.yaml`: 外部アクセス用のRoute定義
- `DEPLOYMENT_GUIDE.md`: デプロイ手順の詳細ガイド

## クイックスタート

```bash
# 1. OpenShiftにログイン
oc login <your-openshift-cluster-url>

# 2. プロジェクトを作成
oc new-project booking-management-system

# 3. イメージをビルド（バックエンド）
cd ../backend
oc new-build --name=booking-backend --strategy=docker --binary=true
oc start-build booking-backend --from-dir=. --follow

# 4. イメージをビルド（フロントエンド）
cd ../frontend
oc new-build --name=booking-frontend --strategy=docker --binary=true
oc start-build booking-frontend --from-dir=. --follow

# 5. デプロイメントを作成
cd ../openshift
oc apply -f backend-deployment.yaml
oc apply -f frontend-deployment.yaml
oc apply -f route.yaml

# 6. RouteのURLを確認
oc get route booking-frontend
```

詳細な手順については、`DEPLOYMENT_GUIDE.md`を参照してください。



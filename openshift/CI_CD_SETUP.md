# CI/CDパイプライン設定ガイド

このガイドでは、Git push時に自動的にOpenShiftにデプロイするCI/CDパイプラインの設定方法を説明します。

## 方法1: GitHub Actionsを使用（推奨）

### 1. OpenShiftの認証トークンを取得

```bash
# OpenShiftにログイン
oc login <your-openshift-cluster-url>

# 認証トークンを取得
oc whoami -t
```

### 2. GitHub Secretsの設定

1. GitHubリポジトリに移動
2. Settings > Secrets and variables > Actions を開く
3. 以下のSecretsを追加:
   - **Name**: `OPENSHIFT_SERVER`
     - **Value**: OpenShiftクラスターのURL（例: `https://api.sandbox.openshift.com:6443`）
   - **Name**: `OPENSHIFT_TOKEN`
     - **Value**: ステップ1で取得した認証トークン

### 3. ワークフローの確認

`.github/workflows/deploy-openshift.yml`が有効になっていることを確認します。

### 4. デプロイのテスト

```bash
# 変更をコミットしてpush
git add .
git commit -m "CI/CD設定を追加"
git push origin main
```

GitHub Actionsのタブで、ワークフローの実行状況を確認できます。

## 方法2: OpenShift Webhookを使用

### 1. Webhookの設定

```bash
# OpenShiftにログイン
oc login <your-openshift-cluster-url>
oc project booking-management

# Webhook設定スクリプトを実行
cd openshift
./webhook-setup.sh
```

このスクリプトは以下を実行します:
- Webhook用のSecretを作成
- BuildConfigを更新してWebhookを有効化
- Webhook URLを表示

### 2. GitHub Webhookの設定

1. 表示されたWebhook URLをコピー
2. GitHubリポジトリのSettings > Webhooks に移動
3. "Add webhook"をクリック
4. 以下を設定:
   - **Payload URL**: バックエンドのGitHub Webhook URL
   - **Content type**: `application/json`
   - **Secret**: 表示されたGitHub Webhook Secret
   - **Events**: "Just the push event"を選択
5. "Add webhook"をクリック
6. フロントエンド用にも同様に設定

### 3. GitHub Actionsの設定（オプション）

Webhookのみを使用する場合は、`.github/workflows/deploy-openshift-webhook.yml`を使用します。

GitHub Secretsを設定:
- `OPENSHIFT_SERVER`
- `OPENSHIFT_TOKEN`

### 4. デプロイのテスト

```bash
# 変更をコミットしてpush
git add .
git commit -m "Webhook設定を追加"
git push origin main
```

## 方法3: ハイブリッド（推奨）

GitHub Actionsでビルドとデプロイを実行し、OpenShift Webhookをバックアップとして使用します。

### セットアップ

1. 方法1の手順を実行（GitHub Actionsの設定）
2. 方法2の手順を実行（OpenShift Webhookの設定）

これにより、以下の利点があります:
- GitHub Actionsで完全な制御が可能
- OpenShift Webhookがバックアップとして機能
- より柔軟なデプロイ戦略

## デプロイフロー

### GitHub Actionsを使用する場合

```
Git Push
  ↓
GitHub Actions トリガー
  ↓
コードチェックアウト
  ↓
バックエンドビルド（Maven）
  ↓
フロントエンドビルド（npm）
  ↓
OpenShift CLIでログイン
  ↓
BuildConfig適用
  ↓
イメージビルド開始
  ↓
デプロイメント適用
  ↓
デプロイ完了
```

### OpenShift Webhookを使用する場合

```
Git Push
  ↓
GitHub Webhook トリガー
  ↓
OpenShift BuildConfig Webhook呼び出し
  ↓
OpenShiftでイメージビルド
  ↓
自動デプロイ（DeploymentConfig使用時）
```

## トラブルシューティング

### GitHub Actionsが失敗する

1. **Secretsの確認**
   ```bash
   # OpenShiftにアクセスできるか確認
   oc login --server=$OPENSHIFT_SERVER --token=$OPENSHIFT_TOKEN
   ```

2. **ログの確認**
   - GitHub Actionsのログを確認
   - エラーメッセージを確認

3. **権限の確認**
   - OpenShiftプロジェクトへのアクセス権限があるか確認
   - BuildConfig、Deployment、Routeを作成する権限があるか確認

### Webhookが動作しない

1. **Webhook URLの確認**
   ```bash
   oc describe bc booking-backend | grep -A 1 "GitHub"
   ```

2. **Secretの確認**
   - GitHubのWebhook設定とOpenShiftのSecretが一致しているか確認

3. **BuildConfigの確認**
   ```bash
   oc get bc booking-backend -o yaml
   ```

### デプロイが遅い

- イメージビルドに時間がかかる場合があります
- ビルドログを確認: `oc logs build/<build-name>`
- リソース制限を確認: `oc describe pod <pod-name>`

## ベストプラクティス

1. **ブランチ戦略**
   - `main`/`master`ブランチのみ自動デプロイ
   - 他のブランチは手動デプロイまたはプレビュー環境

2. **テストの実行**
   - デプロイ前にテストを実行（`.github/workflows/deploy-openshift.yml`に追加可能）

3. **ロールバック**
   ```bash
   # 前のバージョンにロールバック
   oc rollout undo deployment/booking-backend
   oc rollout undo deployment/booking-frontend
   ```

4. **通知設定**
   - Slackやメールでのデプロイ通知を追加可能

## 次のステップ

- テストの自動実行を追加
- ステージング環境の設定
- プロダクション環境への自動デプロイ（承認フロー付き）
- モニタリングとアラートの設定



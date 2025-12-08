# GitHub Actions ワークフロー

このディレクトリには、CI/CDパイプラインのワークフロー定義が含まれています。

## ワークフロー

### 1. deploy-openshift.yml
GitHub Actionsを使用して、コードをビルドし、OpenShiftにデプロイします。

**特徴:**
- コードをチェックアウト
- バックエンドとフロントエンドをビルド
- OpenShift CLIを使用してデプロイ
- デプロイメントの状態を確認

**必要なSecrets:**
- `OPENSHIFT_SERVER`: OpenShiftクラスターのURL
- `OPENSHIFT_TOKEN`: OpenShiftの認証トークン

### 2. deploy-openshift-webhook.yml
OpenShiftのWebhookを使用してビルドをトリガーします。

**特徴:**
- OpenShiftのBuildConfigのWebhookを呼び出し
- より軽量なワークフロー
- OpenShift側でビルドを実行

**必要なSecrets:**
- `OPENSHIFT_SERVER`: OpenShiftクラスターのURL
- `OPENSHIFT_TOKEN`: OpenShiftの認証トークン

## セットアップ手順

### 方法1: GitHub Actionsを使用（推奨）

1. GitHubリポジトリのSettings > Secrets and variables > Actionsに移動
2. 以下のSecretsを追加:
   - `OPENSHIFT_SERVER`: OpenShiftクラスターのURL（例: `https://api.sandbox.openshift.com:6443`）
   - `OPENSHIFT_TOKEN`: OpenShiftの認証トークン（`oc whoami -t`で取得）

3. `deploy-openshift.yml`が有効になっていることを確認

### 方法2: OpenShift Webhookを使用

1. OpenShiftでWebhookを設定:
   ```bash
   cd openshift
   ./webhook-setup.sh
   ```

2. 表示されたWebhook URLをGitHubリポジトリのSettings > Webhooksに追加:
   - Payload URL: 表示されたWebhook URL
   - Content type: `application/json`
   - Secret: 表示されたSecret
   - Events: `Just the push event`

3. `deploy-openshift-webhook.yml`が有効になっていることを確認

## 使用方法

### 自動デプロイ

`main`または`master`ブランチにpushすると、自動的にデプロイが開始されます。

### 手動デプロイ

GitHubリポジトリのActionsタブから、手動でワークフローを実行できます。

## トラブルシューティング

### デプロイが失敗する

1. Secretsが正しく設定されているか確認
2. OpenShiftクラスターへのアクセス権限を確認
3. GitHub Actionsのログを確認

### Webhookが動作しない

1. Webhook URLが正しいか確認
2. Secretが一致しているか確認
3. OpenShiftのBuildConfigにWebhookが設定されているか確認



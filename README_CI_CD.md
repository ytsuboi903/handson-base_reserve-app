# CI/CDパイプライン - クイックスタート

Git push時に自動的にOpenShiftにデプロイする設定方法です。

## 最も簡単な方法: GitHub Actions

### 1. OpenShiftの認証情報を取得

```bash
# OpenShiftにログイン
oc login <your-openshift-cluster-url>

# 認証トークンを取得
oc whoami -t
```

### 2. GitHub Secretsを設定

1. GitHubリポジトリの **Settings** > **Secrets and variables** > **Actions** に移動
2. **New repository secret** をクリック
3. 以下の2つのSecretを追加:

   **Secret 1:**
   - Name: `OPENSHIFT_SERVER`
   - Value: OpenShiftクラスターのURL（例: `https://api.sandbox.openshift.com:6443`）

   **Secret 2:**
   - Name: `OPENSHIFT_TOKEN`
   - Value: ステップ1で取得した認証トークン

### 3. デプロイのテスト

```bash
# 変更をコミットしてpush
git add .
git commit -m "CI/CD設定を追加"
git push origin main
```

GitHubリポジトリの **Actions** タブで、ワークフローの実行状況を確認できます。

## 詳細な設定方法

詳細な設定方法や、OpenShift Webhookを使用する方法については、以下を参照してください:

- `openshift/CI_CD_SETUP.md`: 詳細な設定ガイド
- `.github/workflows/README.md`: ワークフローの説明

## トラブルシューティング

### GitHub Actionsが失敗する

1. Secretsが正しく設定されているか確認
2. OpenShiftクラスターへのアクセス権限を確認
3. GitHub Actionsのログを確認

詳細は `openshift/CI_CD_SETUP.md` のトラブルシューティングセクションを参照してください。



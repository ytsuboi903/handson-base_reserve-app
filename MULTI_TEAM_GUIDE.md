# 複数チーム同時実施ガイド

このガイドでは、複数のチームが同時にハンズオンを実施する方法を説明します。

## 概要

各チームは独立したOpenShiftプロジェクトで作業するため、コード変更を共有する必要はありません。ブランチ名からチーム識別子を自動抽出し、各チーム専用のOpenShiftプロジェクトを作成します。

## ブランチ命名規則

### 推奨パターン

- **`team1-sandbox`**: チーム1の作業用ブランチ
- **`team2-sandbox`**: チーム2の作業用ブランチ
- **`team3-sandbox`**: チーム3の作業用ブランチ
- **`team1`**: チーム1のシンプルなブランチ名（`-sandbox`なしでも可）
- **`sandbox`**: デフォルトチーム用（既存の動作を維持）

### ブランチ名からプロジェクト名への変換

- `team1-sandbox` → `booking-team1`
- `team2` → `booking-team2`
- `sandbox` → `booking-default`
- `main` → `booking-default`

## セットアップ手順

### 1. チームごとにブランチを作成

```bash
# チーム1用ブランチを作成
git checkout -b team1-sandbox

# チーム2用ブランチを作成
git checkout -b team2-sandbox

# チーム3用ブランチを作成
git checkout -b team3-sandbox
```

### 2. ブランチをリモートにプッシュ

```bash
# チーム1
git push origin team1-sandbox

# チーム2
git push origin team2-sandbox

# チーム3
git push origin team3-sandbox
```

### 3. GitHub Actionsが自動的にデプロイ

各ブランチにpushすると、GitHub Actionsが自動的に以下を実行します：

1. ブランチ名からチーム識別子を抽出
2. 対応するOpenShiftプロジェクトを作成（存在しない場合）
3. そのプロジェクトにアプリケーションをデプロイ

## OpenShiftプロジェクトの確認

### プロジェクト一覧を確認

```bash
oc get projects | grep booking
```

出力例：
```
booking-default
booking-team1
booking-team2
booking-team3
```

### 各チームのアプリケーションURLを確認

```bash
# チーム1のURL
oc project booking-team1
oc get route booking-frontend -o jsonpath='{.spec.host}'

# チーム2のURL
oc project booking-team2
oc get route booking-frontend -o jsonpath='{.spec.host}'
```

## チーム識別子の抽出ロジック

ワークフローは以下のロジックでチーム識別子を抽出します：

1. **`sandbox`または`main`**: `default`として扱う
2. **`team[数字]`パターン**: `team1`、`team2`などとして抽出
3. **その他のブランチ名**: 最初のハイフンまでの部分を抽出（小文字に変換）

### 例

| ブランチ名 | チームID | プロジェクト名 |
|-----------|---------|---------------|
| `sandbox` | `default` | `booking-default` |
| `team1-sandbox` | `team1` | `booking-team1` |
| `team2` | `team2` | `booking-team2` |
| `team-abc` | `team` | `booking-team` |
| `my-branch` | `my` | `booking-my` |

## 注意事項

### プロジェクト名の制約

- OpenShiftプロジェクト名は最大63文字
- 小文字、数字、ハイフンのみ使用可能
- 自動的に小文字に変換されます

### リソース名

各プロジェクト内では、リソース名（Deployment、Service、Routeなど）は同じ名前を使用しますが、プロジェクトが異なるため競合しません：

- `booking-backend` (Deployment)
- `booking-frontend` (Deployment)
- `booking-frontend` (Route)

### イメージ名

イメージは各プロジェクトの内部レジストリに保存されるため、プロジェクトごとに独立しています：

- `image-registry.openshift-image-registry.svc:5000/booking-team1/booking-backend:latest`
- `image-registry.openshift-image-registry.svc:5000/booking-team2/booking-backend:latest`

## トラブルシューティング

### プロジェクトが作成されない

```bash
# 手動でプロジェクトを作成
oc new-project booking-team1

# プロジェクトを選択
oc project booking-team1
```

### デプロイメントが失敗する

```bash
# プロジェクトを確認
oc project booking-team1

# Podの状態を確認
oc get pods

# ログを確認
oc logs -l app=booking-backend
oc logs -l app=booking-frontend
```

### ブランチ名が正しく認識されない

ワークフローのログを確認：

```bash
# GitHub Actionsのログで以下を確認
# "Extract team identifier"ステップの出力
# Team ID と Project Name が正しく表示されているか
```

## ベストプラクティス

1. **ブランチ命名規則を統一**: チームごとに一貫した命名規則を使用
2. **プロジェクトのクリーンアップ**: ハンズオン終了後、不要なプロジェクトを削除
3. **リソース使用量の監視**: 複数チームが同時に作業する場合、OpenShiftクラスターのリソース使用量に注意

## プロジェクトの削除

ハンズオン終了後、不要なプロジェクトを削除：

```bash
# チーム1のプロジェクトを削除
oc delete project booking-team1

# チーム2のプロジェクトを削除
oc delete project booking-team2
```

## 関連ファイル

- `.github/workflows/deploy-openshift.yml`: メインのデプロイワークフロー
- `.github/workflows/deploy-openshift-webhook.yml`: Webhook経由のデプロイワークフロー
- `openshift/*.yaml`: OpenShift設定ファイル（テンプレートとして使用）


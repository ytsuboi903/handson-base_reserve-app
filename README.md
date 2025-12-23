# 予約管理システム (Booking Management System)

## 概要
このプロジェクトは、**Github Copilot + AI による対話的な開発フロー**の実践を目的としたハンズオンの題材です。
実用的な予約管理Webアプリケーションを題材に、変更要求をGithub Copilotを通してAIと対話しながら実装していきます。

## 技術スタック

### バックエンド
- Java 17+
- Spring Boot 3.2+
- Spring Data JPA
- H2 Database（開発用）
- Maven

### フロントエンド
- React 18+
- TypeScript
- Vite
- Axios（API通信）
- React Router（ルーティング）
- date-fns（日付操作）

## 主要機能

### 1. 予約管理
- 予約の作成、編集、削除
- 予約詳細の表示
- 予約ステータス管理（予約中、確定、キャンセル）

### 2. カレンダー表示
- 月次カレンダービュー
- 日次予約リスト
- 空き状況の視覚化

### 3. リソース管理
- 予約可能なリソース（会議室、施設など）の管理
- リソースごとの予約状況表示

### 4. 空き状況確認
- 日付・時間帯別の空き状況検索
- リアルタイムの予約可能枠表示

## プロジェクト構造

```
agentic-ai-handson/
├── backend/                 # Spring Boot アプリケーション
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/booking/
│   │   │   │   ├── model/           # エンティティクラス
│   │   │   │   ├── repository/      # データアクセス層
│   │   │   │   ├── service/         # ビジネスロジック
│   │   │   │   ├── controller/      # REST API
│   │   │   │   └── BookingApplication.java
│   │   │   └── resources/
│   │   │       └── application.properties
│   │   └── test/
│   └── pom.xml
├── frontend/                # React アプリケーション
│   ├── src/
│   │   ├── components/      # Reactコンポーネント
│   │   ├── services/        # API通信
│   │   ├── types/           # TypeScript型定義
│   │   ├── App.tsx
│   │   └── main.tsx
│   ├── package.json
│   └── tsconfig.json
└── README.md
```

## 複数チーム同時実施について

このプロジェクトは、複数のチームが同時にハンズオンを実施できるよう設計されています。各チームは独立したOpenShiftプロジェクトで作業するため、コード変更を共有する必要はありません。

詳細は [MULTI_TEAM_GUIDE.md](./MULTI_TEAM_GUIDE.md) を参照してください。

### クイックスタート（複数チーム）

1. チームごとにブランチを作成:
   ```bash
   git checkout -b team1-sandbox  # チーム1
   git checkout -b team2-sandbox  # チーム2
   ```

2. ブランチをプッシュ:
   ```bash
   git push origin team1-sandbox
   ```

3. GitHub Actionsが自動的に各チーム専用のOpenShiftプロジェクトにデプロイします。

## OpenShiftへのデプロイ

コードを変更してGitHubにpushすると、自動的にOpenShiftにデプロイされます。

### デプロイの流れ（概要）

1. **コードのpush**
   - `sandbox`ブランチや`team*-sandbox`ブランチにpush
   - GitHub Actionsワークフローが自動的にトリガー

2. **ビルド**
   - バックエンド: MavenでJavaアプリケーションをビルド
   - フロントエンド: npmでReactアプリケーションをビルド

3. **OpenShiftプロジェクトの準備**
   - ブランチ名からチーム識別子を抽出
   - 対応するOpenShiftプロジェクトを作成（存在しない場合）
   - 例: `team1-sandbox` → `booking-team1`プロジェクト

4. **OpenShiftリソースの適用**
   - BuildConfig（イメージビルド設定）
   - Deployment（アプリケーション配置）
   - Service（内部通信）
   - Route（外部アクセス）

5. **イメージのビルドとデプロイ**
   - OpenShiftの内部レジストリでコンテナイメージをビルド
   - ビルド完了後、Deploymentが自動的に新しいイメージを使用してアプリケーションを起動

6. **デプロイメントの確認**
   - Podが正常に起動するまで待機
   - ヘルスチェックでアプリケーションの状態を確認

7. **アプリケーションへのアクセス**
   - RouteのURLが自動生成され、ブラウザからアクセス可能

### 必要な設定

デプロイを有効にするには、GitHubリポジトリのSecretsに以下を設定する必要があります：

- `OPENSHIFT_SERVER`: OpenShiftクラスターのURL
- `OPENSHIFT_TOKEN`: OpenShiftの認証トークン

詳細は [.github/workflows/README.md](./.github/workflows/README.md) を参照してください。

## 環境のリセット

ハンズオン中に問題が発生した場合、環境を着手前の段階に戻すことができます。

**詳細は [RESET_ENVIRONMENT.md](./RESET_ENVIRONMENT.md) を参照してください。**

### クイックリセット（スクリプト使用）

```bash
# macOS/Linux
./reset-environment.sh

# Windows (PowerShell)
.\reset-environment.ps1
```

## セットアップ手順

### 前提条件
- Java 17以上
- Node.js 18以上
- Maven 3.6以上

### バックエンドの起動

```bash
cd backend
mvn clean install
mvn spring-boot:run
```

バックエンドは http://localhost:8080 で起動します。

### フロントエンドの起動

```bash
cd frontend
npm install
npm run dev
```

フロントエンドは http://localhost:5173 で起動します。

## API エンドポイント

### 予約（Bookings）
- `GET /api/bookings` - 全予約の取得
- `GET /api/bookings/{id}` - 特定予約の取得
- `POST /api/bookings` - 新規予約作成
- `PUT /api/bookings/{id}` - 予約更新
- `DELETE /api/bookings/{id}` - 予約削除
- `GET /api/bookings/available` - 空き状況検索

### リソース（Resources）
- `GET /api/resources` - 全リソースの取得
- `GET /api/resources/{id}` - 特定リソースの取得
- `POST /api/resources` - 新規リソース作成
- `PUT /api/resources/{id}` - リソース更新
- `DELETE /api/resources/{id}` - リソース削除

## データモデル

### Booking（予約）
```java
- id: Long
- resourceId: Long
- customerName: String
- customerEmail: String
- startTime: LocalDateTime
- endTime: LocalDateTime
- status: BookingStatus (PENDING, CONFIRMED, CANCELLED)
- notes: String
- createdAt: LocalDateTime
- updatedAt: LocalDateTime
```

### Resource（リソース）
```java
- id: Long
- name: String
- description: String
- capacity: Integer
- available: Boolean
- createdAt: LocalDateTime
- updatedAt: LocalDateTime
```


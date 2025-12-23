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

## プロジェクト構造

```
agentic-ai-handson/
├── backend/                 # Spring Boot アプリケーション
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/booking/
│   │   │   │   ├── model/           # エンティティクラス
│   │   │   │   ├── repository/     # データアクセス層
│   │   │   │   ├── service/         # ビジネスロジック
│   │   │   │   ├── controller/     # REST API
│   │   │   │   ├── config/         # 設定クラス（DataInitializer等）
│   │   │   │   └── BookingApplication.java
│   │   │   └── resources/
│   │   │       ├── application.properties
│   │   │       └── application-openshift.properties
│   │   └── test/
│   │       └── java/com/booking/service/  # ユニットテスト
│   ├── Dockerfile
│   └── pom.xml
├── frontend/                # React アプリケーション
│   ├── src/
│   │   ├── components/      # Reactコンポーネント
│   │   ├── services/        # API通信
│   │   ├── types/           # TypeScript型定義
│   │   ├── __tests__/       # ユニットテスト
│   │   ├── utils/           # ユーティリティ関数
│   │   ├── App.tsx
│   │   ├── main.tsx
│   │   └── setupTests.ts
│   ├── Dockerfile
│   ├── nginx.conf           # Nginx設定（ローカル用）
│   ├── nginx-openshift.conf # Nginx設定（OpenShift用）
│   ├── package.json
│   ├── tsconfig.json
│   ├── vite.config.ts
│   └── vitest.config.ts
├── openshift/               # OpenShiftデプロイメント設定
│   ├── backend-deployment.yaml
│   ├── frontend-deployment.yaml
│   ├── build-config.yaml
│   ├── route.yaml
│   └── webhook-setup.sh
├── .github/
│   └── workflows/           # GitHub Actionsワークフロー
│       ├── deploy-openshift.yml
│       └── deploy-openshift-webhook.yml
├── reset-environment.sh     # 環境リセットスクリプト（macOS/Linux）
├── reset-environment.ps1   # 環境リセットスクリプト（Windows）
├── README.md
├── RESET_ENVIRONMENT.md
├── MULTI_TEAM_GUIDE.md
├── HANDSON_DESIGN.md
└── HANDSON_PREPARATION.md
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


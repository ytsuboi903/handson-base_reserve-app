# Backend Development Guide

このディレクトリには、バックエンドサービスの実装が含まれています。

## 📖 コーディング規約

**共通ポリシーを参照**: `/.github/instructions/backend-coding-policy.instructions.md`

このディレクトリでは上記の共通ポリシーに加えて、以下のルールを適用します。
- パッケージは `com.booking` 配下に集約し、レイヤーごとに `controller`/`service`/`repository`/`model`/`config` を分離する
- 起動時の初期データ投入は `config` 配下の `DataInitializer` に限定し、ビジネス処理に混ぜない

## 🏗️ ディレクトリ構造

```plaintext
backend/
├── src/
│   ├── main/
│   │   ├── java/com/booking/
│   │   │   ├── config/        # 初期データ投入・設定
│   │   │   ├── controller/    # REST API
│   │   │   ├── model/         # エンティティ/列挙型
│   │   │   ├── repository/    # JPA リポジトリ
│   │   │   └── service/       # ビジネスロジック
│   │   └── resources/         # application*.properties
│   └── test/java/com/booking/
│       └── service/           # サービス層のテスト
├── pom.xml
└── Dockerfile
```

## 🔗 モジュール間の依存関係

- `controller` → `service` → `repository` → `model` の順で依存する
- `config` は初期データ投入に限り `repository`/`model` を参照する
- 逆方向の依存や循環参照は作らない（`service` が `controller` を参照しない等）

## 🧰 開発ワークフロー

### ビルド
```bash
cd backend
mvn clean install
```

### テスト実行
```bash
cd backend
mvn test
```

### ローカル起動
```bash
cd backend
mvn spring-boot:run
```

## 🔒 バックエンド固有のセキュリティ要件

- 認証・認可の実装指針は共通ポリシーに従う（詳細は上記ポリシー参照）
- データベースアクセスは `repository` 経由に限定し、直接 SQL 叩きは行わない
- 機密情報は環境変数または OpenShift の Secret を利用し、`application*.properties` に埋め込まない

## 📊 データベース

- 開発用 DB は H2（インメモリ）を使用
- スキーマは `spring.jpa.hibernate.ddl-auto=update` により自動更新
- 永続化が必要な環境では PostgreSQL 等を想定（OpenShift 手順参照）
- マイグレーションツール（Flyway/Liquibase）は未導入のため、導入時は運用方針を追加する

## 🚀 デプロイメント

- デプロイ先: OpenShift（本番/ステージング/開発はクラスタ内のプロジェクトで分離）
- CI/CD: `/.github/workflows/deploy-openshift.yml` に OpenShift デプロイ手順がある
- 環境設定: `application-openshift.properties` を利用（必要に応じてプロファイル切替）

## 🧭 関連ドキュメント

- [API仕様書](../README.md)
- [アーキテクチャ設計書](../HANDSON_DESIGN.md)
- [データベーススキーマ](../README.md)

## ⚠️ 重要な注意事項

- H2 はインメモリのため、再起動でデータは消える
- 起動時に `DataInitializer` がサンプルデータを投入するため、検証時の前提に注意
- 開発用 CORS 設定は `application.properties` にある（フロントの `http://localhost:5173` を許可）
- OpenShift 向け設定では H2 コンソールを無効化している

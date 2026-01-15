# Frontend Development Guide

このディレクトリには、フロントエンドアプリケーションの実装が含まれています。

## 📖 コーディング規約

**共通ポリシーを参照**: `/.github/instructions/frontend-coding-policy.instructions.md`

このディレクトリでは上記の共通ポリシーに加えて、以下のルールを適用します。

## 🏗️ ディレクトリ構造

```plaintext
frontend/
├── src/
│   ├── App.tsx              # ルーティングと画面レイアウト
│   ├── main.tsx             # エントリーポイント
│   ├── components/          # 画面/機能単位のコンポーネント
│   ├── services/            # API クライアント（axios）
│   ├── types/               # ドメイン型定義
│   ├── __tests__/           # UI/ロジックのテスト
│   └── utils/               # 共有ユーティリティ
├── public/                  # なし（Viteのpublic相当）
├── index.html               # Vite HTML エントリ
├── vite.config.ts           # Vite 設定
└── vitest.config.ts         # Vitest 設定
```

## 🧩 コンポーネント設計

- ルート単位の画面は `App.tsx` で `react-router-dom` ルーティング
- `components/` に画面/機能コンポーネントを配置（例: 予約一覧、リソース一覧、予約フォーム）
- データ取得は `services/api.ts` 経由で行い、UI はローカル状態で管理

## 🗄️ 状態管理

- 現状は `useState` / `useEffect` 等のローカル状態が中心
- グローバル状態は未導入。必要になった場合は Context/Reducer を検討
- 状態の型は `src/types/` を参照して統一

## 🧰 開発ワークフロー

### 開発サーバー起動
```bash
npm run dev
```

### ビルド
```bash
npm run build
```

### テスト実行
```bash
npm run test
```

## 🎨 スタイリング

- 既存は `App.css` でレイアウト定義（グローバル）
- 新規コンポーネントは共通ポリシーに従い CSS Modules を優先
- 画面単位のスタイルはコンポーネントに閉じて管理

## 🔗 バックエンドとの連携

- API クライアントは `src/services/api.ts` に集約
- 基本のベースURLは `/api`（フロントから相対パスで呼び出し）
- エラー処理は呼び出し元で `try/catch` し、UI表示やリトライを明示

## 🚀 デプロイメント

- デプロイ先は OpenShift（本番/ステージングは手順に準拠）
- マニフェストは `openshift/frontend-deployment.yaml` を参照
- CI/CD は GitHub Actions から OpenShift へデプロイ

## 🧭 関連ドキュメント

- [コンポーネントカタログ](../HANDSON_DESIGN.md)
- [デザインシステム](../HANDSON_DESIGN.md)
- [API仕様書](../README.md)

## ⚠️ 重要な注意事項

- `/api` 以外の直書きURLは避け、`services/api.ts` に集約
- 画面追加時は `App.tsx` のルーティングとナビゲーションを合わせて更新
- テストは `src/__tests__/` に配置し、UIロジックは最低限テストを書く

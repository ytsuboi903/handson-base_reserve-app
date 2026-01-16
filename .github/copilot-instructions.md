# GitHub Copilot Custom Instructions

## 🎯 Overview
- GitHub Copilot/Agentic AI 向けのハンズオン教材として、予約管理Webアプリを提供します。
- Spring Boot バックエンドと React フロントエンドで構成され、ローカル開発と OpenShift デプロイを想定します。

## 🧰 Tech Stack
- Languages: Java 17, TypeScript 5.2
- Runtimes: JDK 17, Node.js 18.x
- Backend: Spring Boot 3.2.x, Spring Data JPA, H2 Database（開発用）
- Frontend: React 18.x, Vite 5.x, React Router 6.x, Axios 1.6.x, date-fns 3.x
- Build/Test: Maven 3.6+, npm（package-lock 使用）, Vitest 1.x, ESLint 8.x
- Deploy/Infra: Docker, Nginx（フロント配信）, OpenShift（oc CLI）, GitHub Actions

## 🗺️ Directory Registry
```plaintext
/
├── .github/                # GitHub ActionsワークフローとCopilot設定
├── backend/                # Spring Boot API（詳細: /README.md）
├── frontend/               # React/Vite UI（詳細: /README.md）
├── openshift/              # OpenShiftデプロイ設定（詳細: /openshift/README.md）
├── tmp/                    # ハンズオン補助ドキュメント（詳細: /tmp/）
├── HANDSON_DESIGN.md       # ハンズオン設計と進行
├── HANDSON_PREPARATION.md  # 事前準備と環境条件
├── README.md               # 全体概要とローカル起動手順
├── README_OPENSHIFT.md     # OpenShiftデプロイ概要
└── RESET_ENVIRONMENT.md    # 環境リセット手順
```

## 🏗️ Architecture Overview
- フロントエンド（React/Vite）がバックエンドの REST API（`/api/*`）を呼び出します。
- バックエンドは Controller → Service → Repository の層構造で、H2 を開発用データベースに利用します。
- OpenShift デプロイ用に `openshift/` にマニフェストと手順がまとまっています。

```mermaid
graph LR
  browser[Browser] --> frontend[Frontend (React/Vite)]
  frontend --> backend[Backend (Spring Boot REST)]
  backend --> db[(H2 Database)]
  gha[GitHub Actions] --> openshift[OpenShift Deploy]
  openshift --> frontend
  openshift --> backend
```

## 🚫 ハンズオン中の禁止事項
- ハンズオン実施中、開発サーバの起動・停止は参加者にコントロールさせるため、`mvn spring-boot:run`, `npm run dev`, `pkill ...` 等のプロセス操作コマンドを実行・提案しないでください。代わりに「バックエンドの開発サーバを再起動して下さい」のようにお願いベースで案内してください。

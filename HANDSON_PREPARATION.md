# Agentic AI Coding ハンズオン - 事前準備資料

## 📋 はじめに

この資料は、ハンズオンセッションを実施する前に準備する必要がある内容をまとめています。
**ファシリテーター向け**の資料です。

---

## 🛠️ 環境セットアップ

### 1. 必要なソフトウェア

#### 必須
- **Java 17以上**
  - 確認方法: `java -version`
  - インストール: [Oracle JDK](https://www.oracle.com/java/technologies/downloads/) または [OpenJDK](https://adoptium.net/)

- **Node.js 18以上**
  - 確認方法: `node -v`
  - インストール: [Node.js公式サイト](https://nodejs.org/)

- **Maven 3.6以上**
  - 確認方法: `mvn -v`
  - インストール: [Maven公式サイト](https://maven.apache.org/)

- **VS Code**
  - インストール: [VS Code公式サイト](https://code.visualstudio.com/)

- **GitHub Copilot**
  - VS Code拡張機能としてインストール
  - アカウント設定が必要（フリー枠または有料ライセンス）

#### 推奨
- **Git**
  - 確認方法: `git --version`
  - インストール: [Git公式サイト](https://git-scm.com/)

---

### 2. VS Code拡張機能のインストール

以下の拡張機能をインストールしてください：

1. **GitHub Copilot**
   - 拡張機能ID: `GitHub.copilot`
   - インストール後、GitHubアカウントでログイン

2. **Java Extension Pack**（推奨）
   - 拡張機能ID: `vscjava.vscode-java-pack`
   - Java開発に必要な拡張機能がまとめてインストールされる

3. **ES7+ React/Redux/React-Native snippets**（推奨）
   - 拡張機能ID: `dsznajder.es7-react-js-snippets`

---

### 3. プロジェクトのセットアップ

#### 3.1 プロジェクトのクローンまたはダウンロード

```bash
# Gitを使用する場合
git clone <repository-url>
cd demo1

# または、ZIPファイルをダウンロードして展開
```

#### 3.2 バックエンドのセットアップ

```bash
cd backend

# 依存関係のインストール
mvn clean install

# アプリケーションの起動確認
mvn spring-boot:run
```

ブラウザで http://localhost:8080/api/resources にアクセスして、JSONが表示されればOKです。

#### 3.3 フロントエンドのセットアップ

```bash
cd frontend

# 依存関係のインストール
npm install

# アプリケーションの起動確認
npm run dev
```

ブラウザで http://localhost:5173 にアクセスして、アプリケーションが表示されればOKです。

---

## 🔍 事前確認事項

### チェックリスト

セッション開始前に、以下を確認してください：

#### 環境
- [ ] Java 17以上がインストールされている
- [ ] Node.js 18以上がインストールされている
- [ ] Maven 3.6以上がインストールされている
- [ ] VS Codeがインストールされている
- [ ] GitHub Copilotがインストール・有効化されている

#### プロジェクト
- [ ] プロジェクトがクローン/ダウンロードされている
- [ ] バックエンドが起動できる
- [ ] フロントエンドが起動できる
- [ ] 既存機能（予約作成、一覧表示など）が動作する

#### 事前準備ファイル
- [ ] `.github/copilot-instructions.md` が存在する

---

## 🆘 トラブルシューティング

### よくある問題

#### 1. バックエンドが起動しない
- Javaのバージョンを確認
- Mavenの依存関係を再インストール: `mvn clean install`
- ポート8080が使用中でないか確認

#### 2. フロントエンドが起動しない
- Node.jsのバージョンを確認
- 依存関係を再インストール: `npm install`
- ポート5173が使用中でないか確認

#### 3. Copilotが動作しない
- アカウントがログインしているか確認
- 拡張機能が有効か確認
- ネットワーク接続を確認

# 環境リセット手順

ハンズオン中に手順を間違えて復旧不能になった場合、この手順に従ってローカル環境を着手前の段階に戻すことができます。

## 概要

この手順では、以下の操作を行います：

1. 実行中のアプリケーションを停止
2. ビルド成果物とキャッシュを削除
3. Gitの状態をリセット（オプション）
4. 依存関係を再インストール
5. アプリケーションを再起動

## 前提条件

- Gitがインストールされていること
- Java 17以上がインストールされていること
- Node.js 18以上がインストールされていること
- Mavenがインストールされていること

## 方法1: 自動スクリプトを使用（推奨）

最も簡単な方法は、提供されているリセットスクリプトを使用することです。

### スクリプトが実施する作業

リセットスクリプトは以下の6つのステップを自動的に実行します：

1. **実行中のアプリケーションを停止**
   - Spring Bootアプリケーション（バックエンド）のプロセスを停止
   - Vite開発サーバー（フロントエンド）のプロセスを停止

2. **ポートの使用状況を確認・クリア**
   - ポート8080（バックエンド）が使用されている場合、プロセスを終了
   - ポート5173（フロントエンド）が使用されている場合、プロセスを終了

3. **バックエンドのクリーンアップ**
   - `mvn clean`を実行してMavenのビルド成果物を削除
   - `target/`ディレクトリを完全に削除

4. **フロントエンドのクリーンアップ**
   - `node_modules/`ディレクトリを削除
   - `dist/`ディレクトリ（ビルド成果物）を削除
   - `.vite/`ディレクトリ（Viteキャッシュ）を削除

5. **Gitの状態をリセット（オプション）**
   - 確認プロンプトを表示し、ユーザーの選択に応じて実行
   - `git reset --hard HEAD`で未コミットの変更を破棄
   - `git clean -fd`で追跡されていないファイルを削除

6. **依存関係を再インストール**
   - バックエンド: `mvn dependency:resolve`でMaven依存関係を再ダウンロード
   - フロントエンド: `npm install`でnpm依存関係を再インストール

### macOS/Linuxの場合

```bash
# スクリプトに実行権限を付与
chmod +x reset-environment.sh

# スクリプトを実行
./reset-environment.sh
```

### Windowsの場合

```powershell
# PowerShellで実行
.\reset-environment.ps1
```

## 方法2: 手動でリセット

スクリプトを使用しない場合は、以下の手順を手動で実行してください。

### ステップ1: 実行中のアプリケーションを停止

#### バックエンドを停止

```bash
# バックエンドが実行中の場合は、Ctrl+Cで停止
# または、プロセスを確認して停止
ps aux | grep "spring-boot:run"
kill <プロセスID>
```

#### フロントエンドを停止

```bash
# フロントエンドが実行中の場合は、Ctrl+Cで停止
# または、プロセスを確認して停止
ps aux | grep "vite"
kill <プロセスID>
```

### ステップ2: ビルド成果物とキャッシュを削除

#### バックエンドのクリーンアップ

```bash
cd backend

# Mavenのビルド成果物を削除
mvn clean

# targetディレクトリを完全に削除（念のため）
rm -rf target/
```

#### フロントエンドのクリーンアップ

```bash
cd frontend

# node_modulesとビルド成果物を削除
rm -rf node_modules/
rm -rf dist/
rm -rf .vite/

# npmキャッシュをクリア（オプション）
npm cache clean --force
```

### ステップ3: Gitの状態をリセット（オプション）

#### オプションA: 未コミットの変更をすべて破棄

```bash
# プロジェクトのルートディレクトリに戻る
cd ..

# すべての未コミットの変更を破棄
git reset --hard HEAD

# 追跡されていないファイルを削除
git clean -fd
```

**注意**: この操作は、すべての未コミットの変更を**完全に削除**します。重要な変更がある場合は、事前にバックアップを取ってください。

#### オプションB: 特定のブランチ/コミットに戻す

```bash
# 現在のブランチを確認
git branch

# 特定のブランチに切り替え（例: sandboxブランチ）
git checkout sandbox

# または、特定のコミットに戻す
git log --oneline  # コミット履歴を確認
git reset --hard <コミットハッシュ>
```

#### オプションC: リモートブランチの最新状態に戻す

```bash
# リモートの最新状態を取得
git fetch origin

# 現在のブランチをリモートの状態にリセット
git reset --hard origin/$(git branch --show-current)
```

### ステップ4: 依存関係を再インストール

#### バックエンド

```bash
cd backend

# Mavenの依存関係を再ダウンロード
mvn dependency:resolve

# または、完全にクリーンビルド
mvn clean install -DskipTests
```

#### フロントエンド

```bash
cd frontend

# package-lock.jsonを削除（オプション、問題がある場合のみ）
# rm package-lock.json

# 依存関係を再インストール
npm install
```

### ステップ5: アプリケーションを再起動

#### バックエンドを起動

```bash
cd backend
mvn spring-boot:run
```

バックエンドは http://localhost:8080 で起動します。

#### フロントエンドを起動（別のターミナルで）

```bash
cd frontend
npm run dev
```

フロントエンドは http://localhost:5173 で起動します。

## データベースのリセット

H2データベースはインメモリデータベース（`jdbc:h2:mem:bookingdb`）を使用しているため、アプリケーションを停止すれば自動的にリセットされます。

アプリケーションを再起動すると、`DataInitializer`が自動的に初期データを投入します：

- 5つのリソース（会議室A、B、C、実験室1、スタジオ）
- 5つのサンプル予約

## トラブルシューティング

### ポートが既に使用されている

```bash
# ポート8080（バックエンド）が使用されている場合
lsof -ti:8080 | xargs kill -9

# ポート5173（フロントエンド）が使用されている場合
lsof -ti:5173 | xargs kill -9
```

### Mavenのビルドエラー

```bash
cd backend

# Mavenの設定を確認
mvn -version

# 依存関係を強制的に再ダウンロード
mvn clean install -U -DskipTests
```

### npmのインストールエラー

```bash
cd frontend

# node_modulesを完全に削除
rm -rf node_modules/
rm -rf package-lock.json

# npmキャッシュをクリア
npm cache clean --force

# 再インストール
npm install
```

### Gitのリセットが失敗する

```bash
# Gitの状態を確認
git status

# 変更を一時的に保存（stash）
git stash

# または、特定のファイルのみをリセット
git checkout -- <ファイル名>
```

## 完全リセット（すべてを最初から）

すべてを完全にリセットしたい場合：

```bash
# 1. 実行中のプロセスを停止
pkill -f "spring-boot:run"
pkill -f "vite"

# 2. プロジェクトディレクトリに移動
cd /path/to/agentic-ai-handson

# 3. すべての変更を破棄
git reset --hard HEAD
git clean -fd

# 4. バックエンドをクリーンアップ
cd backend
rm -rf target/
mvn clean

# 5. フロントエンドをクリーンアップ
cd ../frontend
rm -rf node_modules/ dist/ .vite/
rm -f package-lock.json

# 6. 依存関係を再インストール
cd ../backend
mvn dependency:resolve

cd ../frontend
npm install

# 7. アプリケーションを起動
cd ../backend
mvn spring-boot:run &
cd ../frontend
npm run dev
```

## 注意事項

1. **重要な変更のバックアップ**: Gitのリセットを行う前に、重要な変更がある場合は必ずバックアップを取ってください。

2. **リモートへのpush**: リセット後、リモートにpushする場合は注意してください。特に`git reset --hard`を使用した場合、リモートの履歴と一致しない可能性があります。

3. **他のプロジェクトへの影響**: Mavenのローカルリポジトリをクリアすると、他のプロジェクトにも影響する可能性があります。

4. **データの永続化**: H2インメモリデータベースを使用しているため、アプリケーションを停止するとすべてのデータが失われます。これは正常な動作です。

## 次のステップ

環境をリセットした後：

1. アプリケーションが正常に起動することを確認
2. ブラウザで http://localhost:5173 にアクセスして動作確認
3. 初期データ（リソースと予約）が表示されることを確認
4. ハンズオンの手順を最初から再開


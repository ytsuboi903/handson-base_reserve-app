#!/bin/bash

# 環境リセットスクリプト
# ハンズオン中に問題が発生した場合、このスクリプトで環境をリセットできます

set -e  # エラーが発生したら停止

# 色の定義
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# プロジェクトのルートディレクトリを取得
SCRIPT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
cd "$SCRIPT_DIR"

echo -e "${GREEN}========================================${NC}"
echo -e "${GREEN}環境リセットスクリプト${NC}"
echo -e "${GREEN}========================================${NC}"
echo ""

# 確認プロンプト
read -p "この操作は未コミットの変更をすべて削除します。続行しますか？ (y/N): " -n 1 -r
echo
if [[ ! $REPLY =~ ^[Yy]$ ]]; then
    echo -e "${YELLOW}リセットをキャンセルしました。${NC}"
    exit 1
fi

# ステップ1: 実行中のアプリケーションを停止
echo -e "${YELLOW}[1/6] 実行中のアプリケーションを停止中...${NC}"
pkill -f "spring-boot:run" 2>/dev/null || true
pkill -f "vite" 2>/dev/null || true
sleep 2
echo -e "${GREEN}✓ アプリケーションを停止しました${NC}"

# ステップ2: ポートが使用されていないか確認
echo -e "${YELLOW}[2/6] ポートの使用状況を確認中...${NC}"
if lsof -ti:8080 > /dev/null 2>&1; then
    echo -e "${YELLOW}ポート8080が使用されています。プロセスを終了します...${NC}"
    lsof -ti:8080 | xargs kill -9 2>/dev/null || true
fi
if lsof -ti:5173 > /dev/null 2>&1; then
    echo -e "${YELLOW}ポート5173が使用されています。プロセスを終了します...${NC}"
    lsof -ti:5173 | xargs kill -9 2>/dev/null || true
fi
echo -e "${GREEN}✓ ポートをクリアしました${NC}"

# ステップ3: バックエンドのクリーンアップ
echo -e "${YELLOW}[3/6] バックエンドをクリーンアップ中...${NC}"
cd backend
if [ -f "pom.xml" ]; then
    mvn clean > /dev/null 2>&1 || true
fi
rm -rf target/
echo -e "${GREEN}✓ バックエンドをクリーンアップしました${NC}"

# ステップ4: フロントエンドのクリーンアップ
echo -e "${YELLOW}[4/6] フロントエンドをクリーンアップ中...${NC}"
cd ../frontend
rm -rf node_modules/
rm -rf dist/
rm -rf .vite/
echo -e "${GREEN}✓ フロントエンドをクリーンアップしました${NC}"

# ステップ5: Gitの状態をリセット（オプション）
cd ..
echo -e "${YELLOW}[5/6] Gitの状態をリセット中...${NC}"
read -p "Gitの未コミット変更をすべて破棄しますか？ (y/N): " -n 1 -r
echo
if [[ $REPLY =~ ^[Yy]$ ]]; then
    git reset --hard HEAD
    git clean -fd
    echo -e "${GREEN}✓ Gitの状態をリセットしました${NC}"
else
    echo -e "${YELLOW}Gitのリセットをスキップしました${NC}"
fi

# ステップ6: 依存関係を再インストール
echo -e "${YELLOW}[6/6] 依存関係を再インストール中...${NC}"

# バックエンド
echo -e "  ${YELLOW}バックエンドの依存関係をインストール中...${NC}"
cd backend
mvn dependency:resolve > /dev/null 2>&1 || {
    echo -e "${RED}バックエンドの依存関係のインストールに失敗しました${NC}"
    exit 1
}
echo -e "  ${GREEN}✓ バックエンドの依存関係をインストールしました${NC}"

# フロントエンド
echo -e "  ${YELLOW}フロントエンドの依存関係をインストール中...${NC}"
cd ../frontend
npm install > /dev/null 2>&1 || {
    echo -e "${RED}フロントエンドの依存関係のインストールに失敗しました${NC}"
    exit 1
}
echo -e "  ${GREEN}✓ フロントエンドの依存関係をインストールしました${NC}"

cd ..

echo ""
echo -e "${GREEN}========================================${NC}"
echo -e "${GREEN}環境リセットが完了しました！${NC}"
echo -e "${GREEN}========================================${NC}"
echo ""
echo "次のコマンドでアプリケーションを起動できます："
echo ""
echo "  バックエンド:"
echo "    cd backend && mvn spring-boot:run"
echo ""
echo "  フロントエンド（別のターミナルで）:"
echo "    cd frontend && npm run dev"
echo ""


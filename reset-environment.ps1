# 環境リセットスクリプト (PowerShell版)
# ハンズオン中に問題が発生した場合、このスクリプトで環境をリセットできます

$ErrorActionPreference = 'Stop'

# プロジェクトのルートディレクトリを取得
$ScriptDir = Split-Path -Parent $MyInvocation.MyCommand.Path
Set-Location $ScriptDir

Write-Host '========================================' -ForegroundColor Green
Write-Host '環境リセットスクリプト' -ForegroundColor Green
Write-Host '========================================' -ForegroundColor Green
Write-Host ''

# 確認プロンプト
$confirmation = Read-Host 'この操作は未コミットの変更をすべて削除します。続行しますか？ (y/N)'
if ($confirmation -ne 'y' -and $confirmation -ne 'Y') {
    Write-Host 'リセットをキャンセルしました。' -ForegroundColor Yellow
    exit 1
}

# ステップ1: 実行中のアプリケーションを停止
Write-Host '[1/6] 実行中のアプリケーションを停止中...' -ForegroundColor Yellow
Get-Process | Where-Object { $_.ProcessName -like '*java*' -or $_.ProcessName -like '*node*' } | 
    Where-Object { $_.CommandLine -like '*spring-boot*' -or $_.CommandLine -like '*vite*' } | 
    Stop-Process -Force -ErrorAction SilentlyContinue
Start-Sleep -Seconds 2
Write-Host '✓ アプリケーションを停止しました' -ForegroundColor Green

# ステップ2: ポートが使用されていないか確認
Write-Host '[2/6] ポートの使用状況を確認中...' -ForegroundColor Yellow
$port8080 = Get-NetTCPConnection -LocalPort 8080 -ErrorAction SilentlyContinue
$port5173 = Get-NetTCPConnection -LocalPort 5173 -ErrorAction SilentlyContinue

if ($port8080) {
    Write-Host 'ポート8080が使用されています。プロセスを終了します...' -ForegroundColor Yellow
    $port8080 | ForEach-Object { Stop-Process -Id $_.OwningProcess -Force -ErrorAction SilentlyContinue }
}
if ($port5173) {
    Write-Host 'ポート5173が使用されています。プロセスを終了します...' -ForegroundColor Yellow
    $port5173 | ForEach-Object { Stop-Process -Id $_.OwningProcess -Force -ErrorAction SilentlyContinue }
}
Write-Host '✓ ポートをクリアしました' -ForegroundColor Green

# ステップ3: バックエンドのクリーンアップ
Write-Host '[3/6] バックエンドをクリーンアップ中...' -ForegroundColor Yellow
Set-Location backend
if (Test-Path 'pom.xml') {
    mvn clean 2>&1 | Out-Null
}
if (Test-Path 'target') {
    Remove-Item -Recurse -Force target
}
Write-Host '✓ バックエンドをクリーンアップしました' -ForegroundColor Green

# ステップ4: フロントエンドのクリーンアップ
Write-Host '[4/6] フロントエンドをクリーンアップ中...' -ForegroundColor Yellow
Set-Location ../frontend
if (Test-Path 'node_modules') {
    Remove-Item -Recurse -Force node_modules
}
if (Test-Path 'dist') {
    Remove-Item -Recurse -Force dist
}
if (Test-Path '.vite') {
    Remove-Item -Recurse -Force .vite
}
Write-Host '✓ フロントエンドをクリーンアップしました' -ForegroundColor Green

# ステップ5: Gitの状態をリセット（オプション）
Set-Location ..
Write-Host '[5/6] Gitの状態をリセット中...' -ForegroundColor Yellow
$gitReset = Read-Host 'Gitの未コミット変更をすべて破棄しますか？ (y/N)'
if ($gitReset -eq 'y' -or $gitReset -eq 'Y') {
    git reset --hard HEAD
    git clean -fd
    Write-Host '✓ Gitの状態をリセットしました' -ForegroundColor Green
} else {
    Write-Host 'Gitのリセットをスキップしました' -ForegroundColor Yellow
}

# ステップ6: 依存関係を再インストール
Write-Host '[6/6] 依存関係を再インストール中...' -ForegroundColor Yellow

# バックエンド
Write-Host '  バックエンドの依存関係をインストール中...' -ForegroundColor Yellow
Set-Location backend
try {
    mvn dependency:resolve 2>&1 | Out-Null
    Write-Host '  ✓ バックエンドの依存関係をインストールしました' -ForegroundColor Green
} catch {
    Write-Host '  バックエンドの依存関係のインストールに失敗しました' -ForegroundColor Red
    exit 1
}

# フロントエンド
Write-Host '  フロントエンドの依存関係をインストール中...' -ForegroundColor Yellow
Set-Location ../frontend
try {
    npm install 2>&1 | Out-Null
    Write-Host '  ✓ フロントエンドの依存関係をインストールしました' -ForegroundColor Green
} catch {
    Write-Host '  フロントエンドの依存関係のインストールに失敗しました' -ForegroundColor Red
    exit 1
}

Set-Location ..

Write-Host ''
Write-Host '========================================' -ForegroundColor Green
Write-Host '環境リセットが完了しました！' -ForegroundColor Green
Write-Host '========================================' -ForegroundColor Green
Write-Host ''
Write-Host '次のコマンドでアプリケーションを起動できます：'
Write-Host ''
Write-Host '  バックエンド:'
Write-Host '    cd backend; mvn spring-boot:run'
Write-Host ''
Write-Host '  フロントエンド（別のターミナルで）:'
Write-Host '    cd frontend; npm run dev'
Write-Host ''


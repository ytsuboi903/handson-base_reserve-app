# Environment reset script (PowerShell)
# Use this script to reset the environment

$ErrorActionPreference = 'Stop'

# Get project root directory
$ScriptDir = Split-Path -Parent $MyInvocation.MyCommand.Path
Set-Location $ScriptDir

Write-Host '========================================' -ForegroundColor Green
Write-Host 'Environment Reset Script' -ForegroundColor Green
Write-Host '========================================' -ForegroundColor Green
Write-Host ''

# Confirmation prompt
$confirmation = Read-Host 'This will delete all uncommitted changes. Continue? (y/N)'
if ($confirmation -ne 'y' -and $confirmation -ne 'Y') {
    Write-Host 'Reset canceled.' -ForegroundColor Yellow
    exit 1
}

# Step 1: Stop running applications
Write-Host '[1/6] Stopping running applications...' -ForegroundColor Yellow
Get-Process | Where-Object { $_.ProcessName -like '*java*' -or $_.ProcessName -like '*node*' } | 
    Where-Object { $_.CommandLine -like '*spring-boot*' -or $_.CommandLine -like '*vite*' } | 
    Stop-Process -Force -ErrorAction SilentlyContinue
Start-Sleep -Seconds 2
Write-Host 'OK: Applications stopped' -ForegroundColor Green

# Step 2: Check ports
Write-Host '[2/6] Checking port usage...' -ForegroundColor Yellow
$port8080 = Get-NetTCPConnection -LocalPort 8080 -ErrorAction SilentlyContinue
$port5173 = Get-NetTCPConnection -LocalPort 5173 -ErrorAction SilentlyContinue

if ($port8080) {
    Write-Host 'Port 8080 in use. Stopping process...' -ForegroundColor Yellow
    $port8080 | ForEach-Object { Stop-Process -Id $_.OwningProcess -Force -ErrorAction SilentlyContinue }
}
if ($port5173) {
    Write-Host 'Port 5173 in use. Stopping process...' -ForegroundColor Yellow
    $port5173 | ForEach-Object { Stop-Process -Id $_.OwningProcess -Force -ErrorAction SilentlyContinue }
}
Write-Host 'OK: Ports cleared' -ForegroundColor Green

# Step 3: Clean backend
Write-Host '[3/6] Cleaning backend...' -ForegroundColor Yellow
Set-Location backend
if (Test-Path 'pom.xml') {
    mvn clean 2>&1 | Out-Null
}
if (Test-Path 'target') {
    Remove-Item -Recurse -Force target
}
Write-Host 'OK: Backend cleaned' -ForegroundColor Green

# Step 4: Clean frontend
Write-Host '[4/6] Cleaning frontend...' -ForegroundColor Yellow
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
Write-Host 'OK: Frontend cleaned' -ForegroundColor Green

# Step 5: Reset git state (optional)
Set-Location ..
Write-Host '[5/6] Resetting git state...' -ForegroundColor Yellow
$gitReset = Read-Host 'Discard all uncommitted git changes? (y/N)'
if ($gitReset -eq 'y' -or $gitReset -eq 'Y') {
    git reset --hard HEAD
    git clean -fd
    Write-Host 'OK: Git state reset' -ForegroundColor Green
} else {
    Write-Host 'Skipped git reset' -ForegroundColor Yellow
}

# Step 6: Reinstall dependencies
Write-Host '[6/6] Reinstalling dependencies...' -ForegroundColor Yellow

# Backend
Write-Host '  Installing backend dependencies...' -ForegroundColor Yellow
Set-Location backend
try {
    mvn dependency:resolve 2>&1 | Out-Null
    Write-Host '  OK: Backend dependencies installed' -ForegroundColor Green
} catch {
    Write-Host '  ERROR: Backend dependency install failed' -ForegroundColor Red
    exit 1
}

# Frontend
Write-Host '  Installing frontend dependencies...' -ForegroundColor Yellow
Set-Location ../frontend
try {
    npm install 2>&1 | Out-Null
    Write-Host '  OK: Frontend dependencies installed' -ForegroundColor Green
} catch {
    Write-Host '  ERROR: Frontend dependency install failed' -ForegroundColor Red
    exit 1
}

Set-Location ..

Write-Host ''
Write-Host '========================================' -ForegroundColor Green
Write-Host 'Environment reset complete!' -ForegroundColor Green
Write-Host '========================================' -ForegroundColor Green
Write-Host ''
Write-Host 'You can start the apps with:'
Write-Host ''
Write-Host '  Backend:'
Write-Host '    cd backend; mvn spring-boot:run'
Write-Host ''
Write-Host '  Frontend (in another terminal):'
Write-Host '    cd frontend; npm run dev'
Write-Host ''


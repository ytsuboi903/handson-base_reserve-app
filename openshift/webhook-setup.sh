#!/bin/bash

# OpenShift Webhook設定スクリプト
# このスクリプトは、GitHub WebhookをOpenShiftのBuildConfigに設定します

set -e

PROJECT_NAME="booking-management-system"
GITHUB_SECRET="${GITHUB_WEBHOOK_SECRET:-$(openssl rand -hex 16)}"
GENERIC_SECRET="${GENERIC_WEBHOOK_SECRET:-$(openssl rand -hex 16)}"

echo "========================================="
echo "OpenShift Webhook設定"
echo "========================================="

# プロジェクトの確認
if ! oc get project ${PROJECT_NAME} > /dev/null 2>&1; then
    echo "エラー: プロジェクト '${PROJECT_NAME}' が存在しません"
    echo "まずプロジェクトを作成してください: oc new-project ${PROJECT_NAME}"
    exit 1
fi

oc project ${PROJECT_NAME}

# Secretの作成
echo ""
echo "1. Webhook Secretを作成..."
oc create secret generic github-webhook-secret --from-literal=WebHookSecretKey=${GITHUB_SECRET} --dry-run=client -o yaml | oc apply -f -
oc create secret generic generic-webhook-secret --from-literal=WebHookSecretKey=${GENERIC_SECRET} --dry-run=client -o yaml | oc apply -f -

# BuildConfigを更新
echo ""
echo "2. BuildConfigを更新..."
oc apply -f build-config.yaml

# Webhook URLを取得
echo ""
echo "3. Webhook URLを取得..."
echo ""
echo "========================================="
echo "GitHub Webhook URL:"
echo "========================================="

BACKEND_GITHUB_WEBHOOK=$(oc describe bc booking-backend | grep -A 1 "GitHub" | grep "URL" | awk '{print $NF}' || echo "未設定")
FRONTEND_GITHUB_WEBHOOK=$(oc describe bc booking-frontend | grep -A 1 "GitHub" | grep "URL" | awk '{print $NF}' || echo "未設定")

echo "バックエンド: ${BACKEND_GITHUB_WEBHOOK}"
echo "フロントエンド: ${FRONTEND_GITHUB_WEBHOOK}"

echo ""
echo "========================================="
echo "Generic Webhook URL:"
echo "========================================="

BACKEND_GENERIC_WEBHOOK=$(oc describe bc booking-backend | grep -A 1 "Generic" | grep "URL" | awk '{print $NF}' || echo "未設定")
FRONTEND_GENERIC_WEBHOOK=$(oc describe bc booking-frontend | grep -A 1 "Generic" | grep "URL" | awk '{print $NF}' || echo "未設定")

echo "バックエンド: ${BACKEND_GENERIC_WEBHOOK}"
echo "フロントエンド: ${FRONTEND_GENERIC_WEBHOOK}"

echo ""
echo "========================================="
echo "Secret情報:"
echo "========================================="
echo "GitHub Webhook Secret: ${GITHUB_SECRET}"
echo "Generic Webhook Secret: ${GENERIC_SECRET}"
echo ""
echo "これらのSecretをGitHubリポジトリのSettings > Webhooksで設定してください"
echo ""



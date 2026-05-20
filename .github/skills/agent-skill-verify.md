# AgentSkills 動作確認スキル

このスキルは、AgentSkills（サブエージェント）の動作確認用の軽量スキルです。
ローカルで実行できるシェルスクリプトを同梱しており、実行すると標準出力に動作確認メッセージを出します。

目的:
- AgentSkills が期待通りに実行されることを簡潔に検証するため。

内容:
- `tools/agent-skill-verify.sh`: 実行スクリプト（bash）

使い方:
1. リポジトリルートで以下いずれかを実行してください。
   - `bash tools/agent-skill-verify.sh`
   - `./tools/agent-skill-verify.sh`（実行権限を付与した場合）

期待される出力例:

```
AgentSkills 動作確認: OK
日時: 2026-05-20T12:34:56Z
作業ディレクトリ: /Users/you/workspace/repository/agentic-ai-handson
```

このファイルは単純な動作確認を目的としています。必要であれば、追加の検証ロジックを実装します。

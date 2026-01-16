# Frontend Coding Policy

このファイルは、フロントエンド開発における共通コーディング規約の単一情報源（SSOT）です。  
各モジュールの `AGENTS.md` から参照されます。

## 🧰 技術スタック
- Languages: TypeScript 5.2
- Runtime/Tooling: Node.js 18.x, Vite 5.x
- Frameworks/Libraries: React 18.x, React Router 6.x, Axios 1.6.x, date-fns 3.x
- Build/Test/Lint: npm（package-lock 使用）, Vitest 1.x, ESLint 8.x

## 📏 命名規則
- Components: PascalCase（`UserCard`）
- Files/Directories: components と hooks は `PascalCase` / `camelCase`、その他は `kebab-case`
- Variables/Functions: `camelCase`
- Constants: `UPPER_SNAKE_CASE`

例:
```
// ✅ Good
components/UserCard.tsx
hooks/useBookingForm.ts
const MAX_RETRY = 3;
function fetchBookings() {}

// ❌ Bad
components/usercard.tsx
hooks/UseBookingForm.ts
const maxRetry = 3;
function FetchBookings() {}
```

## 🏗️ コンポーネント設計
- React Function Components + Hooks を基本とする
- 1ファイル1コンポーネント（小さく分割）
- Props で入力、コールバックで出力（副作用は呼び出し側へ）

Props/State:
```
// ✅ Good
type UserCardProps = {
  name: string;
  onSelect: (name: string) => void;
};

export function UserCard({ name, onSelect }: UserCardProps) {
  const [isOpen, setIsOpen] = useState(false);
  return (
    <button onClick={() => onSelect(name)} aria-expanded={isOpen}>
      {name}
    </button>
  );
}

// ❌ Bad
export function UserCard(props: any) {
  const [state, setState] = useState({});
  return <div onClick={() => props.onSelect(props.name)}>{props.name}</div>;
}
```

Event handling:
```
// ✅ Good
const handleSubmit = (event: React.FormEvent<HTMLFormElement>) => {
  event.preventDefault();
  onSubmit(formData);
};

// ❌ Bad
const handleSubmit = (event: any) => onSubmit(event.target.value);
```

## 🎨 スタイリング
- CSS Modules を基本とする（`*.module.css`）
- クラス名は `camelCase`、コンポーネント単位で閉じる

```
// ✅ Good
import styles from "./UserCard.module.css";
export function UserCard() {
  return <div className={styles.card}>...</div>;
}

/* UserCard.module.css */
.card { padding: 12px; }

// ❌ Bad
export function UserCard() {
  return <div className="card">...</div>;
}
```

## 🗄️ 状態管理
- まずは `useState` / `useReducer` / Context を優先
- グローバル状態は最小化し、スコープを明確にする

Store 定義例（Context + Reducer）:
```
// ✅ Good
type State = { count: number };
type Action = { type: "increment" } | { type: "decrement" };

const initialState: State = { count: 0 };

function reducer(state: State, action: Action): State {
  switch (action.type) {
    case "increment":
      return { count: state.count + 1 };
    case "decrement":
      return { count: state.count - 1 };
    default:
      return state;
  }
}
```

Store 利用例:
```
// ✅ Good
const CounterContext = createContext<State | null>(null);
const CounterDispatchContext = createContext<React.Dispatch<Action> | null>(null);

export function CounterProvider({ children }: { children: React.ReactNode }) {
  const [state, dispatch] = useReducer(reducer, initialState);
  return (
    <CounterContext.Provider value={state}>
      <CounterDispatchContext.Provider value={dispatch}>
        {children}
      </CounterDispatchContext.Provider>
    </CounterContext.Provider>
  );
}
```

## 🧪 テスト
- テスト基盤: Vitest 1.x
- 重要な UI ロジックはコンポーネントテスト必須
- API 連携や画面遷移は統合テスト（可能な範囲）

Component test:
```
// ✅ Good
import { render, screen } from "@testing-library/react";
import { describe, it, expect } from "vitest";
import { UserCard } from "./UserCard";

describe("UserCard", () => {
  it("renders name", () => {
    render(<UserCard name="Alice" onSelect={() => {}} />);
    expect(screen.getByText("Alice")).toBeInTheDocument();
  });
});
```

Integration test:
```
// ✅ Good
import { describe, it, expect, vi } from "vitest";
import { fetchBookings } from "./api";

describe("fetchBookings", () => {
  it("returns data from API", async () => {
    const mock = vi.fn().mockResolvedValue([{ id: "1" }]);
    const data = await fetchBookings(mock);
    expect(data).toHaveLength(1);
  });
});
```

## 🔒 セキュリティ
- XSS 防止: `dangerouslySetInnerHTML` は原則禁止
- 認証トークン: LocalStorage へ保存しない（必要なら HttpOnly Cookie）
- 機密情報: UI へ露出しない、ログ出力しない

```
// ❌ Bad
return <div dangerouslySetInnerHTML={{ __html: userInput }} />;

// ✅ Good
return <div>{userInput}</div>;
```

```
// ❌ Bad
localStorage.setItem("token", token);

// ✅ Good
// Use HttpOnly cookie handled by server
```

## 📊 パフォーマンス
- ルート単位で `lazy` + `Suspense` を使い分割
- 計算コストは `useMemo`、再生成コールバックは `useCallback`
- 無駄な再レンダーを避ける（依存配列を正しく）

```
// ✅ Good
const LazyPage = lazy(() => import("./Page"));

// ❌ Bad
const LazyPage = lazy(() => import("./Page")); // Used in a non-Suspense tree
```

## 🚫 禁止事項
- `any` を無理由で使用しない（型安全性が失われる）
- 巨大コンポーネントを作らない（分割し保守性を上げる）
- `useEffect` に依存配列なしを乱用しない（再レンダーの副作用）
- `dangerouslySetInnerHTML` を使わない（XSS リスク）
- グローバル CSS を無制限に追加しない（衝突・保守困難）
- 重い計算をレンダー中に実行しない（パフォーマンス劣化）

## 📚 参考リソース
- React 公式ドキュメント: https://react.dev/

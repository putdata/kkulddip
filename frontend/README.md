## 요구사항

- Bun 설치: [https://bun.sh/](https://bun.sh/)
- Node.js (concurrently 실행용)

---

## 폴더 구조

```
frontend/
├─ apps/
│  ├─ user/       # User 앱
│  └─ owner/      # Owner 앱
├─ packages/
│  └─ common/     # 공통 라이브러리
```

---

## 스크립트

| 명령어                    | 설명                             |
| ------------------------- | -------------------------------- |
| `bun run dev`             | user, owner 개발 서버 동시 실행  |
| `bun run user`            | user 앱 개발 서버 단독 실행      |
| `bun run owner`           | owner 앱 개발 서버 단독 실행     |
| `bun run storybook`       | user, owner Storybook 동시 실행  |
| `bun run storybook:user`  | user Storybook 단독 실행         |
| `bun run storybook:owner` | owner Storybook 단독 실행        |
| `bun run build`           | common 먼저 빌드 후 모든 앱 빌드 |

---

## 사용법

1. 의존성 설치

```bash
bun install
```

2. 개발 서버 실행 (동시 실행 예시)

```bash
bun run dev
```

3. Storybook 실행

```bash
bun run storybook
```

4. 빌드

```bash
bun run build
```

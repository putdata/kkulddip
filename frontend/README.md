# Frontend Monorepo

React + TypeScript 기반의 모노레포 프로젝트입니다.

## 📁 프로젝트 구조

```
frontend/
├── apps/
│   ├── user/     # 사용자 앱
│   └── owner/    # 관리자 앱
├── packages/
│   └── common/   # 공통 컴포넌트/유틸리티
└── package.json
```

## 🚀 시작하기

### 설치

```bash
bun install
```

### 개발 서버 실행

```bash
# 두 앱 동시 실행
bun run dev

# 개별 앱 실행
bun run user    # 사용자 앱
bun run owner   # 사업자 앱
```

## 📚 Storybook

```bash
# 두 앱 Storybook 동시 실행
bun run storybook

# 개별 Storybook 실행
bun run storybook:user   # http://localhost:6006
bun run storybook:owner  # http://localhost:6007
```

## 🛠️ 개발 도구

### 코드 품질

```bash
# ESLint 검사
bun run lint

# Prettier 포맷팅
bun run format

# 포맷팅 검사 (CI용)
bun run format:check
```

### 빌드

```bash
# 전체 빌드 (common → user → owner 순서)
bun run build

# 개별 빌드
bun run build:common
bun run build:user
bun run build:owner
```

## 📝 개발 환경 요구사항

- **Bun** 1.2+
- **VS Code** (권장)

# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Development Commands

### Installation and Setup

```bash
bun install
```

### Development Servers

```bash
# Run both apps simultaneously
bun run dev

# Run individual apps
bun run user    # User app on default port
bun run owner   # Owner app on default port
```

### Code Quality and Linting

```bash
# Run ESLint checks
bun run lint

# Format code with Prettier
bun run format

# Check formatting (for CI)
bun run format:check
```

### Building

```bash
# Build all packages and apps (common → user → owner)
bun run build

# Build individual components
bun run build:common   # Build shared package first
bun run build:user     # Build user app
bun run build:owner    # Build owner app
```

### Storybook

```bash
# Run both Storybooks simultaneously
bun run storybook

# Run individual Storybooks
bun run storybook:user   # http://localhost:6006
bun run storybook:owner  # http://localhost:6007
```

### Testing

Individual app test commands:

```bash
cd apps/user && bun test    # User app tests
cd apps/owner && bun test   # Owner app tests (if available)
```

## Architecture Overview

This is a **TypeScript React monorepo** using **Bun** as the runtime and package manager. The project follows a multi-app architecture with shared components.

### Monorepo Structure

- **`apps/user/`** - Customer-facing React application with mobile-first responsive design
- **`apps/owner/`** - Business owner/admin dashboard application
- **`packages/common/`** - Shared utilities, hooks, and components used across apps

### Key Technologies

- **Runtime**: Bun 1.2+
- **Framework**: React 19.1 with React Router 7.7
- **Build Tool**: Vite 7.0 with TypeScript project references
- **Styling**: Tailwind CSS 4.0 with shadcn/ui components
- **State Management**: Zustand 5.0
- **Data Fetching**: TanStack Query 5.83 with Axios
- **Error Handling**: React Error Boundary and Sentry
- **Development**: Storybook for component development

### TypeScript Configuration

The project uses TypeScript project references for the monorepo:

- Root `tsconfig.json` defines workspace paths and references
- Path mapping: `common` → `./packages/common/src`
- Strict TypeScript settings with composite builds enabled

## ESLint Configuration

The project uses **ESLint flat config** format (`eslint.config.js`) with strict linting rules to maintain code quality and consistency across the monorepo.

### Configured Plugins

- **`@typescript-eslint`** - TypeScript-specific linting rules
- **`eslint-plugin-react`** - React best practices and patterns
- **`eslint-plugin-react-hooks`** - React Hooks rules (ensures proper hook usage)
- **`eslint-plugin-react-refresh`** - React Fast Refresh compatibility
- **`eslint-plugin-storybook`** - Storybook-specific linting rules
- **`eslint-plugin-import`** - Import/export statement validation
- **`eslint-plugin-no-relative-import-paths`** - Enforces absolute imports over relative paths

### Ignored Files/Directories

ESLint ignores these paths to avoid linting generated or configuration files:

- `**/dist` - Build output directories
- `**/vite.config.ts` - Vite configuration files
- `**/.storybook/**` - Storybook configuration files
- `**/components/ui/**` - shadcn/ui components (external library code)

### Key Linting Rules

**TypeScript Recommended Rules (from `tseslint.configs.recommended`):**

- `@typescript-eslint/no-explicit-any: error` - **Prohibits `any` type usage** - use specific types instead
- `@typescript-eslint/no-unused-vars: error` - Prevents unused variables and imports
- `@typescript-eslint/no-unsafe-assignment: error` - Prevents unsafe assignments
- `@typescript-eslint/no-unsafe-call: error` - Prevents calling values with `any` type
- `@typescript-eslint/no-unsafe-member-access: error` - Prevents accessing properties on `any` types
- `@typescript-eslint/no-unsafe-return: error` - Prevents returning `any` from functions
- `@typescript-eslint/prefer-as-const: error` - Prefers `as const` over literal type assertions
- `@typescript-eslint/ban-ts-comment: error` - Restricts `@ts-ignore`, `@ts-expect-error` usage

**Custom Code Quality Rules:**

- `no-implicit-coercion: error` - Prevents implicit type coercion (use explicit conversions)
- `curly: error` - Requires curly braces for all control statements
- `no-warning-comments: warn` - Warns about TODO/BUG comments in code

**TypeScript Naming Conventions:**

- **Variables**: `camelCase`, `UPPER_CASE`, or `PascalCase` (leading underscore allowed)
- **Functions**: `camelCase` or `PascalCase`
- **Interfaces**: `PascalCase` (e.g., `UserInterface`)
- **Type Aliases**: `PascalCase` (e.g., `UserType`)

**React-Specific Rules:**

- `react-hooks/rules-of-hooks: error` - Enforces proper hook call order and location
- `react-hooks/exhaustive-deps: warn` - Ensures proper dependency arrays in useEffect, useMemo, etc.
- `react-refresh/only-export-components: warn` - Ensures Fast Refresh compatibility
- `@typescript-eslint/no-empty-function: off` - Allows empty functions (common in React patterns)

### Import Standards

- **Absolute imports preferred** over relative imports (enforced by `no-relative-import-paths`)
- Use `@/` alias for local imports within apps
- Use `common` workspace alias for shared package imports

### Code Organization Patterns

**User App Structure** (`apps/user/src/`):

- `components/` - UI components organized by feature
- `pages/` - Route-level page components
- `hooks/` - Custom React hooks
- `services/` - API calls and external integrations
- `store/` - Zustand state management
- `types/` - TypeScript type definitions
- `utils/` - Helper functions
- `constants/` - Application constants

**Routing**: Uses React Router with nested routes and a `MobileLayout` wrapper component.

**UI Components**: Built with Radix UI primitives and Tailwind CSS, following shadcn/ui patterns.

### Shared Package (`packages/common/`)

Contains reusable code exported via `index.ts`. Currently includes custom hooks like `useCounter`.

### Development Workflow

1. Always run `bun run build:common` before building apps if common package changes
2. Use Storybook for component development and testing
3. Follow the established folder structure and naming conventions
4. Import shared utilities from `common` workspace package
5. Use absolute imports with `@/` alias for local files
6. **Run `bun run lint` before committing** to ensure code quality standards
7. **Address all ESLint errors before submitting code** (warnings are acceptable but should be minimized)
8. **Define proper TypeScript types** - avoid `any` usage by creating specific interfaces/types
9. **Clean up unused imports/variables** - remove or prefix with `_` if temporarily needed

### ESLint Best Practices for this Project

- **Avoid `any` type**: Use specific types instead of `any` - define proper interfaces/types

  ```typescript
  // ❌ Avoid
  const data: any = await fetchUser();

  // ✅ Prefer
  interface User {
    id: string;
    name: string;
  }
  const data: User = await fetchUser();
  ```

- **Always use absolute imports**: Import from `@/components/Button` instead of `../components/Button`
- **Follow naming conventions**: Use `PascalCase` for components, `camelCase` for functions/variables
- **Use arrow functions**: Always prefer arrow functions over function declarations for consistency

  ```typescript
  // ❌ Avoid function declarations
  function handleClick() {
    console.log('clicked');
  }

  function MyComponent() {
    return <div>Hello</div>;
  }

  // ✅ Prefer arrow functions
  const handleClick = () => {
    console.log('clicked');
  };

  const MyComponent = () => {
    return <div>Hello</div>;
  };
  ```

- **Proper React Hooks usage**: Always include all dependencies in useEffect dependency arrays
- **Handle unused variables**: Remove unused imports and variables, or prefix with `_` if needed
- **Avoid TypeScript suppressions**: Don't use `@ts-ignore` or `@ts-expect-error` unless absolutely necessary
- **Use `as const` assertions**: For literal types that shouldn't be widened
- **Clean up TODO/BUG comments**: Address or remove warning comments before production

## Common ESLint Error Solutions

### `@typescript-eslint/no-explicit-any` Error

When you get "Unexpected any. Specify a different type" error:

```typescript
// ❌ Error: Unexpected any
const handleData = (data: any) => {
  return data.map((item: any) => item.id);
};

// ✅ Solution: Define specific types
interface DataItem {
  id: string;
  name: string;
}

const handleData = (data: DataItem[]) => {
  return data.map(item => item.id);
};

// ✅ Alternative: Use generic types
const handleData = <T extends { id: string }>(data: T[]) => {
  return data.map(item => item.id);
};
```

### `@typescript-eslint/no-unused-vars` Error

```typescript
// ❌ Error: 'unusedVariable' is assigned a value but never used
const unusedVariable = 'test';
const usedVariable = 'hello';

// ✅ Solution: Remove unused variables
const usedVariable = 'hello';

// ✅ Alternative: Prefix with underscore if temporarily needed
const _unusedVariable = 'test'; // ESLint will ignore
```

### `react-hooks/exhaustive-deps` Warning

```typescript
// ❌ Warning: Hook useEffect has missing dependencies
useEffect(() => {
  fetchData(userId);
}, []); // Missing 'userId' in dependencies

// ✅ Solution: Add all dependencies
useEffect(() => {
  fetchData(userId);
}, [userId]); // Include all used variables
```

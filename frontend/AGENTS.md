# Project Architecture Guide (Angular Frontend)

This document defines the architectural rules, boundaries, and conventions for this codebase. Any automated agent or contributor MUST follow these rules when generating or modifying code.

---

# 1. Scope Boundary (CRITICAL)

This is the **frontend** application. When working on this project:

- **NEVER** modify, create, or delete files inside the `backend/` directory
- **NEVER** modify backend configuration files (`pom.xml`, `build.gradle`, etc.)
- Focus exclusively on the `frontend/` directory and its contents

---

# 2. Framework & Tech Stack

| Aspect | Value |
|---|---|
| Framework | **Angular 22** (standalone components, no NgModules) |
| TypeScript | ~6.0.2 |
| Styling | SCSS (configured in `angular.json`) |
| Routing | `@angular/router` with `withViewTransitions()` |
| Forms | `@angular/forms` (reactive + template-driven) |
| HTTP | `@angular/common/http` |
| Testing | Vitest with jsdom |
| Font | JetBrains Mono (`@fontsource/jetbrains-mono`) |

---

# 3. Architecture Patterns

## Standalone Components
- All components are **standalone** (`standalone: true`)
- No `NgModule` anywhere — fully standalone architecture using `bootstrapApplication(App, appConfig)`

## Component Style
- Components use **inline templates** (no separate `.html` files) and **inline styles** (no separate `.scss` files)
- The only exceptions are `app.html` and `app.scss` for the root app component, and `styles.scss` for global styles
- Use Angular's new **control flow syntax** (`@if`, `@for`, `@switch`)
- State management via **signals** (`signal()`, `computed()`, `input()`, `output()`)
- Dependency injection via `inject()` function (never constructor injection)

## Lazy Loading
- All route pages use `loadComponent()` for lazy loading
- Routes are defined in `src/app/app.routes.ts`

---

# 4. Project Structure

```
src/
├── app/
│   ├── app.routes.ts           # Route definitions (lazy loaded)
│   ├── app.component.ts        # Root component
│   ├── app.html                # Root template
│   ├── app.scss                # Root styles
│   ├── app.config.ts           # App bootstrap config
│   ├── components/
│   │   ├── layout/             # TerminalTitlebar, TerminalStatusbar
│   │   ├── shared/             # TerminalButton, TerminalCard, TerminalInput
│   │   └── notification/       # NotificationComponent
│   ├── pages/
│   │   ├── auth/               # LoginComponent, RegisterComponent
│   │   ├── cart/               # CartComponent
│   │   ├── checkout/           # CheckoutComponent
│   │   ├── chatbot/            # ChatbotComponent
│   │   ├── home/               # HomeComponent
│   │   ├── not-found/          # NotFoundComponent
│   │   └── products/           # ProductsComponent, ProductDetailComponent
│   ├── models/                 # TypeScript interfaces (Product, Cart, User, etc.)
│   └── services/               # AuthService, CartService, ProductService, etc.
├── environments/               # environment.ts — API URL config
└── styles.scss                 # Global styles & CSS variables
```

---

# 5. Component Conventions

Every component MUST follow this pattern:

```typescript
import { Component, inject, signal } from '@angular/core';

@Component({
  selector: 'app-component-name',
  standalone: true,
  imports: [...],
  template: `...`,
  styles: `...`,
})
export class ComponentName {
  readonly someService = inject(SomeService);
  readonly mySignal = signal(initialValue);
}
```

Rules:
- `inject()` for DI, never constructor injection
- Signals for reactive state
- Inline template and styles (no external files)
- Selector prefix: `app-`

---

# 6. Routing Conventions

- All routes use `loadComponent` for lazy loading
- Protected routes use `canActivate: [authGuard]`
- Auth guard redirects unauthenticated users to `/login`
- Routes are defined in `src/app/app.routes.ts`

---

# 7. Services Conventions

- Services use `@Injectable({ providedIn: 'root' })`
- Services use signals for state management
- Services are re-exported from `src/app/services/index.ts` (barrel file)
- Models are re-exported from `src/app/models/index.ts` (barrel file)

---

# 8. Theme & Styling

The app has a **terminal/hacker aesthetic** with CSS custom properties:

```css
--bg-primary: #0a0e14
--bg-secondary: #131720
--bg-tertiary: #1a1f2e
--bg-hover: #1e2538
--text-primary: #b3b3b3
--text-bright: #e6e6e6
--text-muted: #6e6e6e
--accent-green: #00ff41
--accent-red: #ff3355
--accent-amber: #ffb000
--accent-cyan: #00d4ff
--border: #2a2a3a
```

Typography:
- Font: JetBrains Mono (monospace)
- Font is imported via `@fontsource/jetbrains-mono` in `styles.scss`
- UI patterns: terminal-style prompts (`>`), command-like labels (`$ cat cart`), ASCII art

---

# 9. API Integration

- API base URL: configured in `src/environments/environment.ts` (`apiUrl`)
- HTTP client from `@angular/common/http`
- Auth interceptor adds token to requests
- Current product data is mocked in `ProductService` (no real API calls)

---

# 10. Code Style Rules

1. Use `inject()` for DI — never constructor injection
2. Use signals (`signal`, `computed`) over plain variables
3. Control flow: use `@if`, `@for`, `@switch` (not `*ngIf`, `*ngFor`)
4. Outputs: use `output()` function (not `@Output()` decorator)
5. Inputs: use `input()` function (not `@Input()` decorator)
6. Prefer inline templates and styles
7. Follow the terminal/hacker aesthetic for all new UI
8. No NgModules — everything must be standalone
9. Never leave unused imports
10. Export new models/services from their respective barrel (`index.ts`) files

---

# 11. Agent Instructions (CRITICAL)

1. Only modify files inside the `frontend/` directory — never touch `backend/`
2. Respect the standalone component architecture — never create or modify NgModules
3. Use inline templates and styles for new components
4. Follow the terminal/hacker aesthetic consistently
5. Use signal-based state management
6. Lazy-load all page components via `loadComponent()` in routes
7. Re-export new models and services from their barrel (`index.ts`) files
8. Prefer the existing shared components (`TerminalButton`, `TerminalInput`, `TerminalCard`) when building UI

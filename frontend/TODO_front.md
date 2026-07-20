# Frontend TODO — Audit Findings

## HIGH — Bugs & Broken Functionality

- [ ] **#1** Product detail not reactive to route changes — `product = signal(...)` only reads `productId` at construction time. Navigating between products shows stale data. (`product-detail.component.ts`)
- [ ] **#2** Chatbot `isTypingFlag` is a plain boolean, not a signal — template calls `isTyping()` but Angular can't track the dependency. UI only updates coincidentally when `isLoading()` (a signal) also changes. (`chatbot.component.ts:280`)
- [ ] **#3** Chatbot typewriter `setInterval` never cleared on destroy — if user navigates away mid-animation, interval keeps mutating signals on a dead component. (`chatbot.component.ts:422`)
- [ ] **#4** Auth interceptor: queued requests hang forever on refresh failure — `refreshTokenSubject.next(null)` is filtered out, so queued requests with `take(1)` never receive a value. (`auth.interceptor.ts:44-57`)
- [ ] **#5** `auth.register()` never stores tokens — user appears logged in client-side but has no tokens. Page refresh silently logs them out. (`auth.service.ts:62-68`)
- [ ] **#6** Cart `addToCart`, `removeFromCart`, `clearCart` have no error handlers — failed HTTP requests are silently swallowed. Users see success notification but cart state is inconsistent. (`cart.service.ts`)
- [ ] **#7** Checkout: no form validation at all — orders can be "placed" with completely empty shipping/payment fields. (`checkout.component.ts`)
- [ ] **#8** Checkout: order never sent to backend — `placeOrder()` just clears cart and shows notification, no HTTP call. (`checkout.component.ts`)
- [ ] **#9** `auth.refreshToken()` throws synchronously — should use `throwError(() => new Error(...))` for Observable convention consistency. (`auth.service.ts:93`)

## MEDIUM — UX & Architecture Issues

- [ ] **#10** No guest guard — logged-in users can navigate to `/login` and `/register`. (`app.routes.ts`)
- [ ] **#11** Nav links visible to unauthenticated users — protected pages shown in nav before login. (`app.html`)
- [ ] **#12** Checkout button is a dead end — shows "coming soon" notification instead of navigating to `/checkout`. (`cart.component.ts`)
- [ ] **#13** Cart quantity can decrement to 0 — silently removes the item, no confirmation. (`cart.component.ts`)
- [ ] **#14** No loading/error states on products or cart pages — API failure shows empty state with misleading "0 items". (`products.component.ts`, `cart.component.ts`)
- [ ] **#15** No `takeUntilDestroyed()` on subscriptions in chatbot, login, and register components. (Multiple files)
- [ ] **#16** `getStars()` called in template — allocates new array every render cycle, should be a `computed()`. (`product-detail.component.ts`, `TerminalCardComponent`)
- [ ] **#17** Status check runs only once — if API goes down after load, UI stays "online". (`status.service.ts`)
- [ ] **#18** No production environment file — builds point to `localhost:8080`. (`environment.ts`)
- [ ] **#19** No route `title` properties — browser tab always shows same title. (`app.routes.ts`)
- [ ] **#20** `tsconfig.json` missing `strict: true` — hides type safety issues. (`tsconfig.json`)
- [ ] **#21** Product detail: no button disable during add-to-cart — double-click sends duplicate requests. (`product-detail.component.ts`)
- [ ] **#22** `syncQuantity()` silently catches errors — quantity changes lost with no user feedback. (`cart.service.ts`)

## DISCOVERED VIA TESTS

- [x] **#D1** Cart allows decrementing below qty=1 — clicking `-` on item with qty=1 sends qty=0, which triggers `syncQuantity` to DELETE the item. No guard in `cart.component.ts` `updateQty()`. (`cart.component.ts:24`) — **FIXED**

## LOW — Code Quality & Polish

- [ ] **#23** `Cart` and `CartItem` interfaces are dead code (never used). (`cart.model.ts`, `cart-item.model.ts`)
- [ ] **#24** Search predicate duplicated across `ProductService`, `ProductsComponent`, and `SearchService`. (Multiple files)
- [ ] **#25** Inconsistent import paths (barrel vs direct). (`home.component.ts`, `chatbot.component.ts`)
- [ ] **#26** `authGuard` and `authInterceptor` not exported from services barrel. (`services/index.ts`)
- [ ] **#27** Hardcoded `rgba(0,255,65,...)` should use CSS variable. (`app.scss`, `styles.scss`)
- [ ] **#28** No `autocomplete` attributes on login/register inputs. (`login.component.ts`, `register.component.ts`)
- [ ] **#29** Duplicated session-storage logic in `login()` and `refreshToken()` — should extract `setSession()` helper. (`auth.service.ts`)
- [ ] **#30** `logout()` fire-and-forget HTTP call with no error handler. (`auth.service.ts`)
- [ ] **#31** Guest credentials hardcoded in source code. (`login.component.ts`)
- [ ] **#32** Card input in checkout uses plain `type="text"`. (`checkout.component.ts`)
- [ ] **#33** No Firefox scrollbar styles (only `-webkit-scrollbar`). (`styles.scss`)
- [ ] **#34** No `email` format validation on login/register forms. (`login.component.ts`, `register.component.ts`)
- [ ] **#35** `scrollEffect` in chatbot creates unbounded `setTimeout` calls. (`chatbot.component.ts`)
- [ ] **#36** `isFocused` in `TerminalInputComponent` is a plain boolean, not a signal. (`terminal-input.component.ts`)
- [ ] **#37** Hardcoded "connected" status in statusbar. (`terminal-statusbar.component.ts`)

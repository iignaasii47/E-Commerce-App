import { Component, inject } from '@angular/core';
import { Router, NavigationEnd } from '@angular/router';
import { toSignal } from '@angular/core/rxjs-interop';
import { filter, map } from 'rxjs';
import { CartService } from '../../../services';

@Component({
  selector: 'app-terminal-statusbar',
  standalone: true,
  template: `
    <div class="statusbar">
      <span class="statusbar__left">
        <span class="status-dot status-dot--connected"></span>
        connected
      </span>
      <span class="statusbar__breadcrumb">{{ currentRoute() }}</span>
      <span class="statusbar__right">
        <span class="statusbar__item">
          <span class="status-label">cart:</span>{{ cart.itemCount() }} items (\${{ cart.total().toFixed(2) }})
        </span>
      </span>
    </div>
  `,
  styles: `
    .statusbar {
      display: flex;
      align-items: center;
      height: 28px;
      background: var(--bg-tertiary);
      border-top: 1px solid var(--border);
      padding: 0 12px;
      font-size: 11px;
      color: var(--text-muted);
      user-select: none;
      flex-shrink: 0;
      gap: 16px;
    }

    .statusbar__left {
      display: flex;
      align-items: center;
      gap: 6px;
      color: var(--accent-green);
      white-space: nowrap;
    }

    .status-dot {
      width: 6px;
      height: 6px;
      border-radius: 50%;
    }

    .status-dot--connected {
      background: var(--accent-green);
      box-shadow: 0 0 4px var(--accent-green);
    }

    .statusbar__breadcrumb {
      flex: 1;
      text-align: center;
      color: var(--text-muted);
      overflow: hidden;
      text-overflow: ellipsis;
      white-space: nowrap;
    }

    .statusbar__right {
      display: flex;
      gap: 16px;
      white-space: nowrap;
    }

    .statusbar__item {
      color: var(--text-primary);
    }

    .status-label {
      color: var(--text-muted);
      margin-right: 4px;
    }
  `,
})
export class TerminalStatusbarComponent {
  readonly cart = inject(CartService);

  private readonly router = inject(Router);

  readonly currentRoute = toSignal(
    this.router.events.pipe(
      filter((e) => e instanceof NavigationEnd),
      map(() => {
        const url = this.router.url;
        return url === '/' ? '~/' : url;
      }),
    ),
    { initialValue: '~/' },
  );
}

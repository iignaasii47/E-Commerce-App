import { Component, inject } from '@angular/core';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../../../services';

@Component({
  selector: 'app-terminal-titlebar',
  standalone: true,
  imports: [RouterLink],
  template: `
    <div class="titlebar">
      <div class="titlebar__dots">
        <span class="dot dot--red"></span>
        <span class="dot dot--yellow"></span>
        <span class="dot dot--green"></span>
      </div>
      <span class="titlebar__title">term-shop — zsh</span>
      <div class="titlebar__right">
        <span class="titlebar__user">{{ auth.username() }}&#64;term-shop</span>
        @if (auth.isLoggedIn()) {
          <a class="titlebar__action" (click)="onLogout()">[logout]</a>
        } @else {
          <a class="titlebar__action" routerLink="/login">[login]</a>
        }
      </div>
    </div>
  `,
  styles: `
    .titlebar {
      display: flex;
      align-items: center;
      height: 36px;
      background: var(--bg-tertiary);
      border-bottom: 1px solid var(--border);
      padding: 0 12px;
      user-select: none;
      flex-shrink: 0;
    }

    .titlebar__dots {
      display: flex;
      gap: 6px;
      margin-right: 16px;
    }

    .dot {
      width: 10px;
      height: 10px;
      border-radius: 50%;
    }

    .dot--red { background: var(--accent-red); }
    .dot--yellow { background: var(--accent-amber); }
    .dot--green { background: var(--accent-green); }

    .titlebar__title {
      flex: 1;
      text-align: center;
      font-size: 12px;
      color: var(--text-muted);
    }

    .titlebar__right {
      display: flex;
      align-items: center;
      gap: 10px;
      margin-left: auto;
    }

    .titlebar__user {
      font-size: 11px;
      color: var(--text-muted);
    }

    .titlebar__action {
      font-size: 11px;
      color: var(--accent-cyan);
      cursor: pointer;

      &:hover {
        color: var(--accent-green);
      }
    }
  `,
})
export class TerminalTitlebarComponent {
  readonly auth = inject(AuthService);
  private readonly router = inject(Router);

  onLogout(): void {
    this.auth.logout();
    this.router.navigate(['/login']);
  }
}

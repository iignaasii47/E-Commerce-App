import { Component, inject } from '@angular/core';
import { AuthService } from '../../../services';

@Component({
  selector: 'app-terminal-titlebar',
  standalone: true,
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
      margin-left: auto;
    }

    .titlebar__user {
      font-size: 11px;
      color: var(--text-muted);
    }
  `,
})
export class TerminalTitlebarComponent {
  readonly auth = inject(AuthService);
}

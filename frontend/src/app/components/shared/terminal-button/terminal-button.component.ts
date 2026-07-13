import { Component, input } from '@angular/core';

@Component({
  selector: 'app-terminal-button',
  standalone: true,
  template: `
    <button
      class="t-btn"
      [class]="'t-btn t-btn--' + variant()"
      [disabled]="disabled()"
      [type]="type()">
      <span class="t-btn__prefix">$</span>
      <ng-content />
    </button>
  `,
  styles: `
    .t-btn {
      display: inline-flex;
      align-items: center;
      gap: 6px;
      background: transparent;
      border: 1px solid var(--border);
      color: var(--text-primary);
      padding: 6px 14px;
      cursor: pointer;
      transition: all 0.15s ease;
      font-size: 12px;

      &:hover:not(:disabled) {
        background: var(--bg-hover);
        border-color: var(--text-muted);
        color: var(--text-bright);
      }

      &:active:not(:disabled) {
        transform: translateY(1px);
      }

      &:disabled {
        opacity: 0.4;
        cursor: not-allowed;
      }

      &:focus-visible {
        outline: 1px solid var(--accent-green);
        outline-offset: 1px;
      }
    }

    .t-btn__prefix {
      color: var(--accent-green);
      font-weight: 400;
    }

    .t-btn--primary {
      border-color: var(--accent-green);
      color: var(--accent-green);

      &:hover:not(:disabled) {
        background: rgba(0, 255, 65, 0.1);
      }
    }

    .t-btn--danger {
      border-color: var(--accent-red);
      color: var(--accent-red);

      &:hover:not(:disabled) {
        background: rgba(255, 95, 86, 0.1);
      }
    }

    .t-btn--ghost {
      border-color: transparent;

      &:hover:not(:disabled) {
        border-color: var(--border);
      }
    }
  `,
})
export class TerminalButtonComponent {
  variant = input<'default' | 'primary' | 'danger' | 'ghost'>('default');
  disabled = input(false);
  type = input<'button' | 'submit' | 'reset'>('button');
}

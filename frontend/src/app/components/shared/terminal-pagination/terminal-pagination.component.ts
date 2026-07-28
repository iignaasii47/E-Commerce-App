import { Component, input, output } from '@angular/core';

@Component({
  selector: 'app-terminal-pagination',
  standalone: true,
  template: `
    <div class="pagination">
      <button
        class="page-btn"
        [class.disabled]="currentPage() === 0"
        [disabled]="currentPage() === 0"
        (click)="onPrev()">
        $ prev
      </button>

      <span class="page-info">
        page {{ currentPage() + 1 }} of {{ totalPages() || 1 }}
      </span>

      <button
        class="page-btn"
        [class.disabled]="currentPage() >= totalPages() - 1"
        [disabled]="currentPage() >= totalPages() - 1"
        (click)="onNext()">
        $ next
      </button>
    </div>
  `,
  styles: `
    .pagination {
      display: flex;
      align-items: center;
      justify-content: center;
      gap: 16px;
      padding: 16px 0;
      border-top: 1px solid var(--border);
      margin-top: 16px;
    }

    .page-btn {
      background: transparent;
      border: 1px solid var(--border);
      color: var(--text-muted);
      padding: 5px 14px;
      cursor: pointer;
      font-size: 11px;
      font-family: var(--font-mono);
      transition: all 0.15s;

      &:hover:not(:disabled) {
        border-color: var(--accent-green);
        color: var(--accent-green);
        background: rgba(0, 255, 65, 0.05);
      }

      &.disabled,
      &:disabled {
        opacity: 0.3;
        cursor: not-allowed;
      }
    }

    .page-info {
      font-size: 11px;
      color: var(--text-muted);
      font-family: var(--font-mono);
    }
  `,
})
export class TerminalPaginationComponent {
  currentPage = input.required<number>();
  totalPages = input.required<number>();
  pageChange = output<number>();

  onPrev(): void {
    if (this.currentPage() > 0) {
      this.pageChange.emit(this.currentPage() - 1);
    }
  }

  onNext(): void {
    if (this.currentPage() < this.totalPages() - 1) {
      this.pageChange.emit(this.currentPage() + 1);
    }
  }
}

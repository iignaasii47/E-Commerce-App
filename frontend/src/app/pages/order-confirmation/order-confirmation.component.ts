import { Component, inject, signal, OnInit } from '@angular/core';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { OrderService, NotificationService } from '../../services';
import { Order } from '../../models';
import { TerminalButtonComponent } from '../../components/shared/terminal-button/terminal-button.component';

@Component({
  selector: 'app-order-confirmation',
  standalone: true,
  imports: [RouterLink, TerminalButtonComponent],
  template: `
    <div class="page-container">
      <h1 class="page-title">order --confirm</h1>
      <p class="page-subtitle">order placed successfully</p>

      @if (loading()) {
        <div class="loading-state">
          <p>$ loading order details...</p>
        </div>
      } @else if (order()) {
        <div class="confirmation-layout">
          <div class="order-details">
            <div class="detail-section">
              <p class="section-label">// order info</p>
              <div class="detail-grid">
                <div class="detail-row">
                  <span class="detail-label">order id:</span>
                  <span class="detail-value">#{{ order()!.id }}</span>
                </div>
                <div class="detail-row">
                  <span class="detail-label">status:</span>
                  <span class="detail-value status">{{ order()!.status }}</span>
                </div>
                <div class="detail-row">
                  <span class="detail-label">date:</span>
                  <span class="detail-value">{{ formatDate(order()!.createdAt) }}</span>
                </div>
              </div>
            </div>

            <div class="detail-section">
              <p class="section-label">// items</p>
              <div class="items-list">
                @for (item of order()!.items; track item.id) {
                  <div class="item-row">
                    <span class="item-name">{{ item.quantity }}x {{ item.productName }}</span>
                    <span class="item-price">\${{ item.subtotal.toFixed(2) }}</span>
                  </div>
                }
              </div>
              <hr class="section-divider">
              <div class="total-row">
                <span>total:</span>
                <span>\${{ order()!.total.toFixed(2) }}</span>
              </div>
            </div>

            <div class="detail-section">
              <p class="section-label">// shipping</p>
              <div class="detail-grid">
                <div class="detail-row">
                  <span class="detail-label">address:</span>
                  <span class="detail-value">{{ order()!.shippingAddress }}</span>
                </div>
                <div class="detail-row">
                  <span class="detail-label">city:</span>
                  <span class="detail-value">{{ order()!.shippingCity }}</span>
                </div>
                <div class="detail-row">
                  <span class="detail-label">zip:</span>
                  <span class="detail-value">{{ order()!.shippingZip }}</span>
                </div>
              </div>
            </div>
          </div>

          <div class="confirmation-actions">
            <a routerLink="/products">
              <app-terminal-button>continue-shopping</app-terminal-button>
            </a>
          </div>
        </div>
      } @else if (error()) {
        <div class="error-state">
          <p>$ {{ error() }}</p>
          <a routerLink="/products" class="back-link">cd products/</a>
        </div>
      }
    </div>
  `,
  styles: `
    .confirmation-layout {
      display: flex;
      flex-direction: column;
      gap: 24px;
    }

    .order-details {
      display: flex;
      flex-direction: column;
      gap: 20px;
    }

    .detail-section {
      padding: 16px;
      border: 1px solid var(--border);
      background: var(--bg-secondary);
    }

    .detail-grid {
      display: flex;
      flex-direction: column;
      gap: 8px;
    }

    .detail-row {
      display: flex;
      gap: 12px;
      font-size: 12px;
    }

    .detail-label {
      color: var(--text-muted);
      min-width: 80px;
    }

    .detail-value {
      color: var(--text-bright);
    }

    .status {
      color: var(--accent-green);
      font-weight: 600;
    }

    .items-list {
      display: flex;
      flex-direction: column;
      gap: 6px;
    }

    .item-row {
      display: flex;
      justify-content: space-between;
      font-size: 12px;
      color: var(--text-primary);
    }

    .item-price {
      color: var(--accent-amber);
    }

    .total-row {
      display: flex;
      justify-content: space-between;
      font-size: 14px;
      font-weight: 700;
      color: var(--accent-amber);
    }

    .confirmation-actions {
      display: flex;
      justify-content: center;
    }

    .loading-state,
    .error-state {
      padding: 40px;
      color: var(--text-muted);
    }

    .error-state {
      text-align: center;
    }

    .back-link {
      color: var(--text-muted);
      font-size: 12px;

      &:hover {
        color: var(--accent-cyan);
      }
    }
  `,
})
export class OrderConfirmationComponent implements OnInit {
  private readonly route = inject(ActivatedRoute);
  private readonly orderService = inject(OrderService);
  private readonly notifications = inject(NotificationService);

  readonly order = signal<Order | null>(null);
  readonly loading = signal(true);
  readonly error = signal('');

  ngOnInit(): void {
    const id = Number(this.route.snapshot.paramMap.get('id'));
    if (!id) {
      this.loading.set(false);
      this.error.set('invalid order id');
      return;
    }

    this.orderService.getOrder(id).subscribe({
      next: (order) => {
        this.order.set(order);
        this.loading.set(false);
      },
      error: (err) => {
        this.loading.set(false);
        this.error.set(err.error?.message ?? 'order not found');
        this.notifications.error('failed to load order details');
      },
    });
  }

  formatDate(dateStr: string): string {
    return new Date(dateStr).toLocaleString();
  }
}

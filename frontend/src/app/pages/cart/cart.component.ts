import { Component, inject } from '@angular/core';
import { RouterLink } from '@angular/router';
import { CartService, NotificationService } from '../../services';
import { TerminalButtonComponent } from '../../components/shared/terminal-button/terminal-button.component';

@Component({
  selector: 'app-cart',
  standalone: true,
  imports: [RouterLink, TerminalButtonComponent],
  template: `
    <div class="page-container">
      <h1 class="page-title">cat cart.json</h1>
      <p class="page-subtitle">{{ cart.itemCount() }} items in cart</p>

      @if (cart.items().length > 0) {
        <div class="cart-list">
          @for (item of cart.items(); track item.id) {
            <div class="cart-item">
              <div class="cart-item__info">
                <span class="cart-item__id">#{{ item.productId }}</span>
                <span class="cart-item__name">{{ item.productName }}</span>
              </div>
              <div class="cart-item__controls">
                <button class="qty-btn" (click)="updateQty(item, item.quantity - 1)">-</button>
                <span class="qty-val">{{ item.quantity }}</span>
                <button class="qty-btn" (click)="updateQty(item, item.quantity + 1)">+</button>
              </div>
              <span class="cart-item__price">\${{ (item.unitPrice * item.quantity).toFixed(2) }}</span>
              <button class="remove-btn" (click)="remove(item)">
                [rm]
              </button>
            </div>
          }
        </div>

        <div class="cart-summary">
          <div class="summary-row">
            <span>subtotal:</span>
            <span>\${{ cart.total().toFixed(2) }}</span>
          </div>
          <div class="summary-row">
            <span>shipping:</span>
            <span class="free-shipping">free</span>
          </div>
          <hr class="section-divider">
          <div class="summary-row summary-row--total">
            <span>total:</span>
            <span>\${{ cart.total().toFixed(2) }}</span>
          </div>
        </div>

        <div class="cart-actions">
          <a routerLink="/products" class="continue-link">&lt; cd products/</a>
          <app-terminal-button variant="primary" (click)="checkout()">
            checkout
          </app-terminal-button>
        </div>
      } @else {
        <div class="empty-cart">
          <pre class="empty-ascii">
  _____
 / ___ \\
| |   | |  Your cart is empty
| |___| |  Add some items first
  \\_____/   $ cd products/
          </pre>
        </div>
      }
    </div>
  `,
  styles: `
    .cart-list {
      display: flex;
      flex-direction: column;
      gap: 2px;
      margin-bottom: 16px;
    }

    .cart-item {
      display: grid;
      grid-template-columns: 1fr auto auto auto;
      gap: 16px;
      align-items: center;
      padding: 10px 12px;
      background: var(--bg-secondary);
      border: 1px solid var(--border);

      &:hover {
        border-color: var(--text-muted);
      }
    }

    .cart-item__info {
      display: flex;
      align-items: center;
      gap: 10px;
      min-width: 0;
    }

    .cart-item__id {
      color: var(--text-muted);
      font-size: 11px;
      flex-shrink: 0;
    }

    .cart-item__name {
      color: var(--text-bright);
      font-size: 12px;
      overflow: hidden;
      text-overflow: ellipsis;
      white-space: nowrap;
    }

    .cart-item__controls {
      display: flex;
      align-items: center;
      gap: 8px;
    }

    .qty-btn {
      width: 24px;
      height: 24px;
      display: flex;
      align-items: center;
      justify-content: center;
      background: transparent;
      border: 1px solid var(--border);
      color: var(--text-primary);
      cursor: pointer;
      font-family: var(--font-mono);
      font-size: 12px;

      &:hover {
        border-color: var(--accent-green);
        color: var(--accent-green);
      }
    }

    .qty-val {
      min-width: 20px;
      text-align: center;
      font-weight: 600;
      color: var(--text-bright);
    }

    .cart-item__price {
      font-weight: 600;
      color: var(--accent-amber);
      font-size: 13px;
      min-width: 80px;
      text-align: right;
    }

    .remove-btn {
      background: transparent;
      border: none;
      color: var(--accent-red);
      cursor: pointer;
      font-family: var(--font-mono);
      font-size: 11px;
      padding: 4px;

      &:hover {
        text-decoration: underline;
      }
    }

    .cart-summary {
      padding: 14px;
      border: 1px solid var(--border);
      background: var(--bg-secondary);
      margin-bottom: 16px;
    }

    .summary-row {
      display: flex;
      justify-content: space-between;
      font-size: 12px;
      color: var(--text-muted);
      padding: 4px 0;

      &--total {
        font-size: 14px;
        font-weight: 700;
        color: var(--accent-amber);
      }
    }

    .free-shipping {
      color: var(--accent-green);
    }

    .cart-actions {
      display: flex;
      justify-content: space-between;
      align-items: center;
    }

    .continue-link {
      color: var(--text-muted);
      font-size: 12px;

      &:hover {
        color: var(--accent-cyan);
      }
    }

    .empty-cart {
      text-align: center;
      padding: 40px;
    }

    .empty-ascii {
      color: var(--text-muted);
      font-size: 12px;
      line-height: 1.5;
      text-align: left;
      display: inline-block;
    }
  `,
})
export class CartComponent {
  readonly cart = inject(CartService);
  private readonly notifications = inject(NotificationService);

  updateQty(item: { id: number; productId: number; quantity: number; productName: string }, qty: number): void {
    this.cart.updateQuantity(item.id, item.productId, qty);
  }

  remove(item: { id: number; productName: string }): void {
    this.cart.removeFromCart(item.id);
    this.notifications.info(`${item.productName} removed from cart`);
  }

  checkout(): void {
    this.notifications.info('checkout flow coming soon...');
  }
}

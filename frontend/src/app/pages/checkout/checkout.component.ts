import { Component, inject, signal } from '@angular/core';
import { Router, RouterLink } from '@angular/router';
import { CartService, AuthService, NotificationService, OrderService } from '../../services';
import { TerminalInputComponent } from '../../components/shared/terminal-input/terminal-input.component';
import { TerminalButtonComponent } from '../../components/shared/terminal-button/terminal-button.component';

@Component({
  selector: 'app-checkout',
  standalone: true,
  imports: [RouterLink, TerminalInputComponent, TerminalButtonComponent],
  template: `
    <div class="page-container">
      <h1 class="page-title">checkout --process</h1>
      <p class="page-subtitle">complete your order</p>

      @if (cart.items().length > 0) {
        <div class="checkout-layout">
          <div class="checkout-form">
            <div class="form-section">
              <p class="section-label">// shipping info</p>
              <div class="form-fields">
                <app-terminal-input
                  label="address"
                  placeholder="123 Terminal St"
                  [value]="address()"
                  (valueChange)="address.set($event)" />
                @if (errors()['address']) {
                  <span class="field-error">{{ errors()['address'] }}</span>
                }
                <app-terminal-input
                  label="city"
                  placeholder="San Francisco"
                  [value]="city()"
                  (valueChange)="city.set($event)" />
                @if (errors()['city']) {
                  <span class="field-error">{{ errors()['city'] }}</span>
                }
                <app-terminal-input
                  label="zip"
                  placeholder="94102"
                  [value]="zip()"
                  (valueChange)="zip.set($event)" />
                @if (errors()['zip']) {
                  <span class="field-error">{{ errors()['zip'] }}</span>
                }
              </div>
            </div>

            <div class="form-section">
              <p class="section-label">// payment (mock)</p>
              <div class="form-fields">
                <app-terminal-input
                  label="card"
                  placeholder="**** **** **** 4242"
                  [value]="card()"
                  (valueChange)="card.set($event)" />
                <app-terminal-input
                  label="exp"
                  placeholder="12/28"
                  [value]="exp()"
                  (valueChange)="exp.set($event)" />
              </div>
            </div>
          </div>

          <div class="checkout-summary">
            <p class="section-label">// order summary</p>
            <div class="summary-items">
              @for (item of cart.items(); track item.id) {
                <div class="summary-item">
                  <span>{{ item.quantity }}x {{ item.productName }}</span>
                  <span>\${{ (item.unitPrice * item.quantity).toFixed(2) }}</span>
                </div>
              }
            </div>
            <hr class="section-divider">
            <div class="summary-total">
              <span>total:</span>
              <span>\${{ cart.total().toFixed(2) }}</span>
            </div>

            <div class="checkout-actions">
              <app-terminal-button (click)="placeOrder()" [disabled]="placing()">
                {{ placing() ? 'processing...' : 'place-order' }}
              </app-terminal-button>
            </div>
          </div>
        </div>
      } @else {
        <div class="empty-state">
          <p>$ checkout: cart is empty</p>
          <a routerLink="/products" class="back-link">cd products/</a>
        </div>
      }
    </div>
  `,
  styles: `
    .checkout-layout {
      display: grid;
      grid-template-columns: 1fr 320px;
      gap: 24px;
    }

    .form-section {
      margin-bottom: 20px;
    }

    .form-fields {
      display: flex;
      flex-direction: column;
      gap: 8px;
    }

    .field-error {
      color: var(--accent-red);
      font-size: 11px;
      padding-left: 4px;
    }

    .checkout-summary {
      padding: 16px;
      border: 1px solid var(--border);
      background: var(--bg-secondary);
      align-self: start;
    }

    .summary-items {
      display: flex;
      flex-direction: column;
      gap: 6px;
    }

    .summary-item {
      display: flex;
      justify-content: space-between;
      font-size: 12px;
      color: var(--text-primary);
    }

    .summary-total {
      display: flex;
      justify-content: space-between;
      font-size: 14px;
      font-weight: 700;
      color: var(--accent-amber);
    }

    .checkout-actions {
      margin-top: 16px;
    }

    .empty-state {
      padding: 40px;
      color: var(--text-muted);
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
export class CheckoutComponent {
  readonly cart = inject(CartService);
  private readonly auth = inject(AuthService);
  private readonly notifications = inject(NotificationService);
  private readonly orderService = inject(OrderService);
  private readonly router = inject(Router);

  readonly address = signal('');
  readonly city = signal('');
  readonly zip = signal('');
  readonly card = signal('');
  readonly exp = signal('');
  readonly placing = signal(false);
  readonly errors = signal<Record<string, string>>({});

  placeOrder(): void {
    if (!this.auth.isLoggedIn()) {
      this.notifications.error('please login to place an order');
      return;
    }

    const validationErrors: Record<string, string> = {};
    if (!this.address().trim()) {
      validationErrors['address'] = 'address is required';
    }
    if (!this.city().trim()) {
      validationErrors['city'] = 'city is required';
    }
    if (!this.zip().trim()) {
      validationErrors['zip'] = 'zip is required';
    }

    if (Object.keys(validationErrors).length > 0) {
      this.errors.set(validationErrors);
      return;
    }

    this.errors.set({});
    this.placing.set(true);

    this.orderService
      .createOrder({
        shippingAddress: this.address().trim(),
        shippingCity: this.city().trim(),
        shippingZip: this.zip().trim(),
      })
      .subscribe({
        next: (order) => {
          this.cart.loadCart();
          this.placing.set(false);
          this.router.navigate(['/order', order.id]);
        },
        error: (err) => {
          this.placing.set(false);
          const message = err.error?.message ?? 'failed to place order';
          this.notifications.error(message);
        },
      });
  }
}

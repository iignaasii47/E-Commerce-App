import { Component, inject, signal } from '@angular/core';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { HttpErrorResponse } from '@angular/common/http';
import { toSignal } from '@angular/core/rxjs-interop';
import { map } from 'rxjs';
import { ProductService, CartService, NotificationService } from '../../../services';
import { TerminalButtonComponent } from '../../../components/shared/terminal-button/terminal-button.component';
import { getStars } from '../../../utils';
import { Product } from '../../../models';

@Component({
  selector: 'app-product-detail',
  standalone: true,
  imports: [RouterLink, TerminalButtonComponent],
  template: `
    <div class="page-container">
      @if (loading()) {
        <div class="loading">
          <p>$ loading product...</p>
        </div>
      } @else if (product(); as p) {
        <a routerLink="/products" class="back-link">&lt; cd ..</a>

        <div class="detail-layout">
          <div class="detail-main">
            <div class="product-header">
              <span class="product-id">#{{ p.id }}</span>
              <span class="product-category">{{ p.category }}</span>
              <span class="product-stock" [class.low]="p.stock < 10">
                [{{ p.stock > 0 ? 'in stock: ' + p.stock : 'out of stock' }}]
              </span>
            </div>

            <h1 class="product-name">{{ p.name }}</h1>

            <div class="product-meta">
              <span class="product-rating">
                @for (i of getStars(p.rating); track i) {
                  <span class="star filled">*</span>
                }
                {{ p.rating }}/5.0
              </span>
            </div>

            <div class="product-description">
              <p class="desc-label">// description</p>
              <p>{{ p.description }}</p>
            </div>
          </div>

          <div class="detail-sidebar">
            <div class="price-box">
              <span class="price-label">price:</span>
              <span class="price-value">\${{ p.price.toFixed(2) }}</span>
            </div>

            <div class="quantity-control">
              <span class="qty-label">qty:</span>
              <button class="qty-btn" (click)="decrementQty()">-</button>
              <span class="qty-value">{{ quantity() }}</span>
              <button class="qty-btn" (click)="incrementQty()">+</button>
            </div>

            <app-terminal-button
              variant="primary"
              [disabled]="p.stock === 0"
              (click)="addToCart(p)">
              add-to-cart
            </app-terminal-button>

            <div class="subtotal">
              <span>subtotal:</span>
              <span class="subtotal-value">
                \${{ (p.price * quantity()).toFixed(2) }}
              </span>
            </div>
          </div>
        </div>
      } @else {
        <div class="not-found">
          <p>$ error: could not load product</p>
          <a routerLink="/products" class="back-link">cd products/</a>
        </div>
      }
    </div>
  `,
  styles: `
    .detail-layout {
      display: grid;
      grid-template-columns: 1fr 300px;
      gap: 24px;
    }

    .product-header {
      display: flex;
      align-items: center;
      gap: 10px;
      font-size: 12px;
      margin-bottom: 8px;
    }

    .product-id {
      color: var(--text-muted);
    }

    .product-category {
      color: var(--accent-cyan);
      text-transform: uppercase;
    }

    .product-stock {
      margin-left: auto;
      color: var(--accent-green);
      font-size: 11px;

      &.low {
        color: var(--accent-amber);
      }
    }

    .product-name {
      font-size: 20px;
      font-weight: 700;
      color: var(--text-bright);
      margin-bottom: 8px;
    }

    .product-meta {
      font-size: 12px;
      color: var(--text-muted);
      margin-bottom: 20px;
    }

    .star {
      color: var(--text-muted);
      &.filled { color: var(--accent-amber); }
    }

    .desc-label {
      font-size: 11px;
      color: var(--text-muted);
      margin-bottom: 6px;
    }

    .product-description {
      font-size: 13px;
      line-height: 1.7;
      color: var(--text-primary);
      padding: 12px;
      border: 1px solid var(--border);
      background: var(--bg-secondary);
    }

    .detail-sidebar {
      display: flex;
      flex-direction: column;
      gap: 14px;
      padding: 16px;
      border: 1px solid var(--border);
      background: var(--bg-secondary);
      align-self: start;
    }

    .price-box {
      display: flex;
      justify-content: space-between;
      align-items: center;
    }

    .price-label {
      color: var(--text-muted);
      font-size: 12px;
    }

    .price-value {
      font-size: 22px;
      font-weight: 700;
      color: var(--accent-amber);
    }

    .quantity-control {
      display: flex;
      align-items: center;
      gap: 10px;
      font-size: 13px;
    }

    .qty-label {
      color: var(--text-muted);
      font-size: 12px;
    }

    .qty-btn {
      width: 28px;
      height: 28px;
      display: flex;
      align-items: center;
      justify-content: center;
      background: transparent;
      border: 1px solid var(--border);
      color: var(--text-primary);
      cursor: pointer;
      font-family: var(--font-mono);
      font-size: 14px;

      &:hover {
        border-color: var(--accent-green);
        color: var(--accent-green);
      }
    }

    .qty-value {
      min-width: 24px;
      text-align: center;
      font-weight: 600;
      color: var(--text-bright);
    }

    .subtotal {
      display: flex;
      justify-content: space-between;
      padding-top: 10px;
      border-top: 1px solid var(--border);
      font-size: 12px;
      color: var(--text-muted);
    }

    .subtotal-value {
      color: var(--accent-amber);
      font-weight: 600;
    }

    .not-found {
      padding: 40px;
      text-align: center;
      color: var(--text-muted);
    }

    .loading {
      padding: 40px;
      text-align: center;
      color: var(--text-muted);
    }
  `,
})
export class ProductDetailComponent {
  private readonly router = inject(Router);

  private readonly productService = inject(ProductService);
  private readonly cartService = inject(CartService);
  private readonly notifications = inject(NotificationService);
  private readonly route = inject(ActivatedRoute);

  private readonly productId = toSignal(
    this.route.paramMap.pipe(map((params) => Number(params.get('id')))),
    { initialValue: 0 },
  );

  product = signal<Product | undefined>(undefined);

  loading = signal(true);

  quantity = signal(1);

  constructor() {
    const id = this.productId();
    if (id) {
      this.productService.getProduct(id).subscribe({
        next: (product) => {
          this.product.set(product);
          this.loading.set(false);
        },
        error: (err: HttpErrorResponse) => {
          this.loading.set(false);
          if (err.status === 404) {
            this.router.navigate(['/not-found']);
          } else {
            this.notifications.error('failed to load product');
            this.router.navigate(['/products']);
          }
        },
      });
    }
  }

  getStars = getStars;

  incrementQty(): void {
    const p = this.product();
    if (p && this.quantity() < p.stock) {
      this.quantity.update((q) => q + 1);
    }
  }

  decrementQty(): void {
    if (this.quantity() > 1) {
      this.quantity.update((q) => q - 1);
    }
  }

  addToCart(product: Product): void {
    this.cartService.addToCart(product.id, this.quantity());
    this.notifications.success(`${product.name} added to cart`);
  }
}

import { Component, input } from '@angular/core';
import { RouterLink } from '@angular/router';
import { Product } from '../../../models';

@Component({
  selector: 'app-terminal-card',
  standalone: true,
  imports: [RouterLink],
  template: `
    <a class="t-card" [routerLink]="['/products', product().id]">
      <div class="t-card__image-wrap">
        <div class="t-card__image-frame">
          @if (product().image) {
            <img [src]="product().image" [alt]="product().name" class="t-card__img" />
          } @else {
            <span class="t-card__img-placeholder">*</span>
          }
        </div>
      </div>
      <div class="t-card__info">
        <div class="t-card__header">
          <span class="t-card__id">#{{ product().id }}</span>
          <span class="t-card__category">{{ product().category }}</span>
          <span class="t-card__stock" [class.low]="product().stock < 10">
            stock:{{ product().stock }}
          </span>
        </div>
        <div class="t-card__body">
          <h3 class="t-card__name">{{ product().name }}</h3>
          <p class="t-card__desc">{{ product().description }}</p>
        </div>
        <div class="t-card__footer">
          <span class="t-card__price">\${{ product().price.toFixed(2) }}</span>
          <span class="t-card__rating">
            @for (star of getStars(); track star) {
              <span class="star filled">*</span>
            }@empty {
              <span class="star">-</span>
            }
            {{ product().rating }}
          </span>
        </div>
      </div>
    </a>
  `,
  styles: `
    .t-card {
      display: flex;
      gap: 12px;
      border: 1px solid var(--border);
      background: var(--bg-secondary);
      padding: 12px 14px;
      cursor: pointer;
      transition: all 0.15s ease;
      text-decoration: none;
      color: inherit;

      &:hover {
        border-color: var(--accent-green);
        background: var(--bg-hover);

        .t-card__name {
          color: var(--accent-green);
        }
      }
    }

    .t-card__image-wrap {
      flex-shrink: 0;
    }

    .t-card__image-frame {
      width: 70px;
      height: 70px;
      border: 1px solid var(--border);
      display: flex;
      align-items: center;
      justify-content: center;
      overflow: hidden;
      background: var(--bg-primary);
    }

    .t-card__img {
      width: 100%;
      height: 100%;
      object-fit: cover;
    }

    .t-card__img-placeholder {
      color: var(--text-muted);
      font-size: 18px;
    }

    .t-card__info {
      flex: 1;
      min-width: 0;
    }

    .t-card__header {
      display: flex;
      align-items: center;
      gap: 10px;
      font-size: 11px;
      margin-bottom: 8px;
    }

    .t-card__id {
      color: var(--text-muted);
    }

    .t-card__category {
      color: var(--accent-cyan);
      text-transform: uppercase;
      letter-spacing: 0.5px;
    }

    .t-card__stock {
      margin-left: auto;
      color: var(--accent-green);

      &.low {
        color: var(--accent-amber);
      }
    }

    .t-card__name {
      font-size: 13px;
      font-weight: 600;
      color: var(--text-bright);
      margin-bottom: 4px;
      transition: color 0.15s;
    }

    .t-card__desc {
      font-size: 11px;
      color: var(--text-muted);
      line-height: 1.5;
      display: -webkit-box;
      -webkit-line-clamp: 2;
      -webkit-box-orient: vertical;
      overflow: hidden;
    }

    .t-card__footer {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-top: 10px;
      padding-top: 8px;
      border-top: 1px solid var(--border);
    }

    .t-card__price {
      font-size: 14px;
      font-weight: 700;
      color: var(--accent-amber);
    }

    .t-card__rating {
      font-size: 11px;
      color: var(--text-muted);
    }

    .star {
      color: var(--text-muted);
      &.filled {
        color: var(--accent-amber);
      }
    }
  `,
})
export class TerminalCardComponent {
  product = input.required<Product>();

  getStars(): number[] {
    return Array.from({ length: Math.floor(this.product().rating) }, (_, i) => i);
  }
}

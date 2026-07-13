import { Component, inject, signal, computed } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { toSignal } from '@angular/core/rxjs-interop';
import { map } from 'rxjs';
import { ProductService } from '../../services';
import { TerminalCardComponent } from '../../components/shared/terminal-card/terminal-card.component';
import { TerminalInputComponent } from '../../components/shared/terminal-input/terminal-input.component';

@Component({
  selector: 'app-products',
  standalone: true,
  imports: [TerminalCardComponent, TerminalInputComponent],
  template: `
    <div class="page-container">
      <h1 class="page-title">ls products/</h1>
      <p class="page-subtitle">
        {{ filteredProducts().length }} items found
        @if (searchQuery()) {
          <span> — searching: "{{ searchQuery() }}"</span>
        }
      </p>

      <div class="filters">
        <div class="search-bar">
          <app-terminal-input
            placeholder="search products..."
            [value]="searchQuery()"
            (valueChange)="onSearch($event)" />
        </div>
        <div class="category-filters">
          <button
            class="filter-tag"
            [class.active]="selectedCategory() === ''"
            (click)="selectCategory('')">
            all
          </button>
          @for (cat of categories(); track cat) {
            <button
              class="filter-tag"
              [class.active]="selectedCategory() === cat"
              (click)="selectCategory(cat)">
              {{ cat }}
            </button>
          }
        </div>
      </div>

      <div class="product-grid">
        @for (product of filteredProducts(); track product.id) {
          <app-terminal-card [product]="product" />
        } @empty {
          <div class="empty-state">
            <p>$ ls: no matching products found</p>
            <p class="empty-hint">try adjusting your search or filter criteria</p>
          </div>
        }
      </div>
    </div>
  `,
  styles: `
    .filters {
      margin-bottom: 16px;
      display: flex;
      flex-direction: column;
      gap: 10px;
    }

    .search-bar {
      max-width: 400px;
    }

    .category-filters {
      display: flex;
      flex-wrap: wrap;
      gap: 6px;
    }

    .filter-tag {
      background: transparent;
      border: 1px solid var(--border);
      color: var(--text-muted);
      padding: 3px 10px;
      font-size: 11px;
      cursor: pointer;
      transition: all 0.15s;
      font-family: var(--font-mono);

      &:hover {
        border-color: var(--text-muted);
        color: var(--text-primary);
      }

      &.active {
        border-color: var(--accent-green);
        color: var(--accent-green);
        background: rgba(0, 255, 65, 0.05);
      }
    }

    .product-grid {
      display: grid;
      grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
      gap: 10px;
    }

    .empty-state {
      grid-column: 1 / -1;
      padding: 40px;
      text-align: center;
      color: var(--text-muted);
      border: 1px dashed var(--border);
    }

    .empty-hint {
      font-size: 11px;
      margin-top: 6px;
      color: var(--text-muted);
    }
  `,
})
export class ProductsComponent {
  private readonly productService = inject(ProductService);
  private readonly route = inject(ActivatedRoute);

  readonly searchQuery = signal('');
  readonly selectedCategory = signal('');

  readonly categories = this.productService.categories;

  private readonly queryParam = toSignal(
    this.route.queryParamMap.pipe(map((params) => params.get('q') ?? '')),
    { initialValue: '' },
  );

  readonly filteredProducts = computed(() => {
    let products = this.productService.allProducts();

    const q = this.searchQuery() || this.queryParam();
    if (q) {
      const lower = q.toLowerCase();
      products = products.filter(
        (p) =>
          p.name.toLowerCase().includes(lower) ||
          p.description.toLowerCase().includes(lower) ||
          p.category.toLowerCase().includes(lower),
      );
    }

    const cat = this.selectedCategory();
    if (cat) {
      products = products.filter((p) => p.category === cat);
    }

    return products;
  });

  constructor() {
    const q = this.queryParam();
    if (q) this.searchQuery.set(q);
  }

  onSearch(value: string): void {
    this.searchQuery.set(value);
  }

  selectCategory(category: string): void {
    this.selectedCategory.set(category);
  }
}

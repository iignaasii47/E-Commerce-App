import { Component, inject, signal, OnInit } from '@angular/core';
import { RouterLink, ActivatedRoute } from '@angular/router';
import { toSignal } from '@angular/core/rxjs-interop';
import { map } from 'rxjs';
import { ProductService } from '../../services';
import { TerminalCardComponent } from '../../components/shared/terminal-card/terminal-card.component';
import { TerminalInputComponent } from '../../components/shared/terminal-input/terminal-input.component';
import { TerminalPaginationComponent } from '../../components/shared/terminal-pagination/terminal-pagination.component';

@Component({
  selector: 'app-products',
  standalone: true,
  imports: [RouterLink, TerminalCardComponent, TerminalInputComponent, TerminalPaginationComponent],
  template: `
    <div class="page-container">
      <a routerLink="/" class="back-link">$ cd ~</a>
      <h1 class="page-title">ls products/</h1>
      <p class="page-subtitle">
        {{ productService.total() }} items found
        @if (searchQuery()) {
          <span> — searching: "{{ searchQuery() }}"</span>
        }
        @if (productService.pages() > 1) {
          <span> — page {{ productService.page() + 1 }} of {{ productService.pages() }}</span>
        }
      </p>

      <div class="filters">
        <div class="search-bar">
          <app-terminal-input
            placeholder="search products..."
            [value]="searchQuery()"
            (valueChange)="onSearch($event)" />
        </div>

        <div class="sort-row">
          <span class="sort-label">$ sort by:</span>
          @for (opt of sortOptions; track opt.label) {
            <button
              class="filter-tag"
              [class.active]="productService.currentSort() === opt.field && productService.currentSortDir() === opt.dir"
              (click)="productService.setSort(opt.field, opt.dir)">
              {{ opt.label }}
            </button>
          }
        </div>

        <div class="category-filters">
          <button
            class="filter-tag"
            [class.active]="selectedCategory() === ''"
            (click)="selectCategory('')">
            all
          </button>
          @for (cat of productService.categories(); track cat) {
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
        @for (product of productService.products(); track product.id) {
          <app-terminal-card [product]="product" />
        } @empty {
          <div class="empty-state">
            <p>$ ls: no matching products found</p>
            <p class="empty-hint">try adjusting your search or filter criteria</p>
          </div>
        }
      </div>

      @if (productService.pages() > 1) {
        <app-terminal-pagination
          [currentPage]="productService.page()"
          [totalPages]="productService.pages()"
          (pageChange)="productService.goToPage($event)" />
      }
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

    .sort-row {
      display: flex;
      align-items: center;
      flex-wrap: wrap;
      gap: 6px;
    }

    .sort-label {
      font-size: 11px;
      color: var(--text-muted);
      margin-right: 4px;
      font-family: var(--font-mono);
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
export class ProductsComponent implements OnInit {
  readonly productService = inject(ProductService);
  private readonly route = inject(ActivatedRoute);

  readonly searchQuery = signal('');
  readonly selectedCategory = signal('');

  readonly sortOptions = [
    { label: 'name ↑', field: 'name', dir: 'asc' },
    { label: 'name ↓', field: 'name', dir: 'desc' },
    { label: 'price ↑', field: 'price', dir: 'asc' },
    { label: 'price ↓', field: 'price', dir: 'desc' },
  ];

  private readonly queryParam = toSignal(
    this.route.queryParamMap.pipe(map((params) => params.get('q') ?? '')),
    { initialValue: '' },
  );

  ngOnInit(): void {
    const q = this.queryParam();
    if (q) {
      this.searchQuery.set(q);
      this.productService.search(q);
    }
  }

  onSearch(value: string): void {
    this.searchQuery.set(value);
    this.productService.search(value);
  }

  selectCategory(category: string): void {
    this.selectedCategory.set(category);
    this.productService.filterByCategory(category);
  }
}

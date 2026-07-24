import { Injectable, inject, signal } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Product, PaginatedResponse } from '../models';
import { environment } from '../../environments/environment';

@Injectable({ providedIn: 'root' })
export class ProductService {
  private readonly http = inject(HttpClient);

  private readonly currentPage = signal(0);
  private readonly totalPages = signal(0);
  private readonly totalElements = signal(0);
  private readonly pageSize = signal(10);
  private readonly sortField = signal('name');
  private readonly sortDir = signal('asc');
  private readonly content = signal<Product[]>([]);
  private readonly categoriesCache = signal<string[]>([]);

  readonly products = this.content.asReadonly();
  readonly page = this.currentPage.asReadonly();
  readonly pages = this.totalPages.asReadonly();
  readonly total = this.totalElements.asReadonly();
  readonly size = this.pageSize.asReadonly();
  readonly currentSort = this.sortField.asReadonly();
  readonly currentSortDir = this.sortDir.asReadonly();
  readonly categories = this.categoriesCache.asReadonly();

  private filters: { search?: string; category?: string } = {};

  constructor() {
    this.loadProducts();
    this.loadCategories();
  }

  loadProducts(search?: string, category?: string): void {
    this.filters = { search, category };
    let params = new HttpParams()
      .set('page', this.currentPage())
      .set('size', this.pageSize());

    const sort = `${this.sortField()},${this.sortDir()}`;
    params = params.set('sort', sort);

    if (search) {
      params = params.set('search', search);
    }
    if (category) {
      params = params.set('category', category);
    }

    this.http
      .get<PaginatedResponse<Product>>(`${environment.apiUrl}/api/products`, { params })
      .subscribe({
        next: (response) => {
          this.content.set(response.content);
          this.totalPages.set(response.totalPages);
          this.totalElements.set(response.totalElements);
          this.currentPage.set(response.currentPage);
          this.pageSize.set(response.pageSize);
        },
        error: () => {
          this.content.set([]);
          this.totalPages.set(0);
          this.totalElements.set(0);
        },
      });
  }

  goToPage(page: number): void {
    if (page >= 0 && page < this.totalPages()) {
      this.currentPage.set(page);
      this.loadProducts(this.filters.search, this.filters.category);
    }
  }

  setSort(field: string, dir: string): void {
    this.sortField.set(field);
    this.sortDir.set(dir);
    this.currentPage.set(0);
    this.loadProducts(this.filters.search, this.filters.category);
  }

  search(query: string): void {
    this.currentPage.set(0);
    this.loadProducts(query || undefined, this.filters.category);
  }

  filterByCategory(category: string): void {
    this.currentPage.set(0);
    this.loadProducts(this.filters.search, category || undefined);
  }

  getProduct(id: number): Observable<Product> {
    return this.http.get<Product>(`${environment.apiUrl}/api/products/${id}`);
  }

  getProductById(id: number): Product | undefined {
    return this.products().find((p) => p.id === id);
  }

  private loadCategories(): void {
    this.http
      .get<string[]>(`${environment.apiUrl}/api/products/categories`)
      .subscribe({
        next: (cats) => this.categoriesCache.set(cats),
        error: () => this.categoriesCache.set([]),
      });
  }
}

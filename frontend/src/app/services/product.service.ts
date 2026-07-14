import { Injectable, inject, signal, computed } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Product } from '../models';
import { environment } from '../../environments/environment';

@Injectable({ providedIn: 'root' })
export class ProductService {
  private readonly http = inject(HttpClient);
  private readonly products = signal<Product[]>([]);

  readonly allProducts = this.products.asReadonly();

  readonly categories = computed(() => {
    const cats = new Set(this.products().map((p) => p.category));
    return Array.from(cats);
  });

  constructor() {
    this.loadProducts();
  }

  private loadProducts(): void {
    this.http.get<Product[]>(`${environment.apiUrl}/api/products`).subscribe({
      next: (products) => this.products.set(products),
      error: () => this.products.set([]),
    });
  }

  getProductById(id: number): Product | undefined {
    return this.products().find((p) => p.id === id);
  }

  getByCategory(category: string): Product[] {
    return this.products().filter((p) => p.category === category);
  }

  search(query: string): Product[] {
    const q = query.toLowerCase();
    return this.products().filter(
      (p) =>
        p.name.toLowerCase().includes(q) ||
        p.description.toLowerCase().includes(q) ||
        p.category.toLowerCase().includes(q),
    );
  }
}

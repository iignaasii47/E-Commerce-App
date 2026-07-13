import { Injectable, signal, computed, inject } from '@angular/core';
import { Product } from '../models';
import { ProductService } from './product.service';

@Injectable({ providedIn: 'root' })
export class SearchService {
  private readonly productService = inject(ProductService);

  readonly query = signal('');

  readonly results = computed<Product[]>(() => {
    const q = this.query();
    if (!q.trim()) return [];
    return this.productService.search(q);
  });

  readonly hasQuery = computed(() => this.query().trim().length > 0);

  setQuery(value: string): void {
    this.query.set(value);
  }

  clear(): void {
    this.query.set('');
  }
}

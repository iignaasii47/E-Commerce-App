import { Injectable, inject, signal, computed } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { ApiCartItem } from '../models';
import { environment } from '../../environments/environment';

@Injectable({ providedIn: 'root' })
export class CartService {
  private readonly http = inject(HttpClient);

  private readonly apiUrl = environment.apiUrl + '/api/cart';

  readonly items = signal<ApiCartItem[]>([]);

  readonly total = computed(() =>
    this.items().reduce((sum, item) => sum + item.unitPrice * item.quantity, 0),
  );

  readonly itemCount = computed(() => this.items().reduce((sum, item) => sum + item.quantity, 0));

  private pendingTimeouts = new Map<number, ReturnType<typeof setTimeout>>();

  constructor() {
    this.loadCart();
  }

  loadCart(): void {
    this.http.get<ApiCartItem[]>(this.apiUrl).subscribe({
      next: (items) => this.items.set(items),
      error: () => this.items.set([]),
    });
  }

  addToCart(productId: number, quantity: number): void {
    this.http
      .post<ApiCartItem>(`${this.apiUrl}?productId=${productId}&quantity=${quantity}`, {})
      .subscribe({
        next: (item) => {
          this.items.update((items) => {
            const idx = items.findIndex((i) => i.productId === item.productId);
            if (idx >= 0) {
              const updated = [...items];
              updated[idx] = item;
              return updated;
            }
            return [...items, item];
          });
        },
      });
  }

  removeFromCart(cartItemId: number): void {
    this.http.delete(`${this.apiUrl}/${cartItemId}`).subscribe({
      next: () => {
        this.items.update((items) => items.filter((i) => i.id !== cartItemId));
      },
    });
  }

  updateQuantity(cartItemId: number, productId: number, newQty: number): void {
    this.items.update((items) =>
      items.map((item) =>
        item.id === cartItemId
          ? { ...item, quantity: newQty, subtotal: item.unitPrice * newQty }
          : item,
      ),
    );

    const existing = this.pendingTimeouts.get(cartItemId);
    if (existing) clearTimeout(existing);

    const timeout = setTimeout(() => {
      this.pendingTimeouts.delete(cartItemId);
      this.syncQuantity(cartItemId, productId, newQty);
    }, 1000);

    this.pendingTimeouts.set(cartItemId, timeout);
  }

  clearCart(): void {
    const ids = this.items().map((i) => i.id);
    ids.forEach((id) => {
      this.http.delete(`${this.apiUrl}/${id}`).subscribe();
    });
    this.items.set([]);
  }

  private syncQuantity(cartItemId: number, productId: number, newQty: number): void {
    if (newQty === 0) {
      this.http.delete(`${this.apiUrl}/${cartItemId}`).subscribe({
        next: () => {
          this.items.update((items) => items.filter((i) => i.id !== cartItemId));
        },
      });
    } else {
      this.http.delete(`${this.apiUrl}/${cartItemId}`).subscribe({
        next: () => {
          this.http
            .post<ApiCartItem>(`${this.apiUrl}?productId=${productId}&quantity=${newQty}`, {})
            .subscribe({
              next: (newItem) => {
                this.items.update((items) => [
                  ...items.filter((i) => i.id !== cartItemId),
                  newItem,
                ]);
              },
            });
        },
      });
    }
  }
}

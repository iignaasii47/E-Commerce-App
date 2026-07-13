import { Injectable, signal, computed } from '@angular/core';
import { CartItem, Product } from '../models';

@Injectable({ providedIn: 'root' })
export class CartService {
  readonly items = signal<CartItem[]>([]);

  readonly total = computed(() =>
    this.items().reduce((sum, item) => sum + item.product.price * item.quantity, 0),
  );

  readonly itemCount = computed(() => this.items().reduce((sum, item) => sum + item.quantity, 0));

  addToCart(product: Product, quantity = 1): void {
    const current = this.items();
    const existing = current.find((i) => i.product.id === product.id);

    if (existing) {
      this.items.set(
        current.map((i) =>
          i.product.id === product.id ? { ...i, quantity: i.quantity + quantity } : i,
        ),
      );
    } else {
      this.items.set([...current, { product, quantity }]);
    }
  }

  removeFromCart(productId: number): void {
    this.items.set(this.items().filter((i) => i.product.id !== productId));
  }

  updateQuantity(productId: number, quantity: number): void {
    if (quantity <= 0) {
      this.removeFromCart(productId);
      return;
    }
    this.items.set(
      this.items().map((i) => (i.product.id === productId ? { ...i, quantity } : i)),
    );
  }

  clearCart(): void {
    this.items.set([]);
  }
}

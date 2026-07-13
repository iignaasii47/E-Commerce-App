import { TestBed } from '@angular/core/testing';
import { CartService } from './cart.service';
import { Product } from '../models';

const mockProduct: Product = {
  id: 1,
  name: 'Test Product',
  description: 'A test product',
  price: 29.99,
  category: 'accessories',
  image: 'test.jpg',
  stock: 10,
  rating: 4.5,
};

const mockProduct2: Product = {
  id: 2,
  name: 'Another Product',
  description: 'Another test product',
  price: 49.99,
  category: 'peripherals',
  image: 'test2.jpg',
  stock: 5,
  rating: 3.8,
};

describe('CartService', () => {
  let service: CartService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(CartService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should start with empty cart', () => {
    expect(service.items()).toEqual([]);
    expect(service.total()).toBe(0);
    expect(service.itemCount()).toBe(0);
  });

  it('should add a product to cart', () => {
    service.addToCart(mockProduct);
    expect(service.items().length).toBe(1);
    expect(service.items()[0].product.id).toBe(1);
    expect(service.items()[0].quantity).toBe(1);
    expect(service.total()).toBe(29.99);
    expect(service.itemCount()).toBe(1);
  });

  it('should add product with custom quantity', () => {
    service.addToCart(mockProduct, 3);
    expect(service.items()[0].quantity).toBe(3);
    expect(service.itemCount()).toBe(3);
    expect(service.total()).toBe(29.99 * 3);
  });

  it('should increment quantity when adding existing product', () => {
    service.addToCart(mockProduct);
    service.addToCart(mockProduct);
    expect(service.items().length).toBe(1);
    expect(service.items()[0].quantity).toBe(2);
    expect(service.itemCount()).toBe(2);
    expect(service.total()).toBe(29.99 * 2);
  });

  it('should add multiple different products', () => {
    service.addToCart(mockProduct);
    service.addToCart(mockProduct2);
    expect(service.items().length).toBe(2);
    expect(service.itemCount()).toBe(2);
    expect(service.total()).toBe(29.99 + 49.99);
  });

  it('should remove a product from cart', () => {
    service.addToCart(mockProduct);
    service.addToCart(mockProduct2);
    service.removeFromCart(1);
    expect(service.items().length).toBe(1);
    expect(service.items()[0].product.id).toBe(2);
  });

  it('should update quantity', () => {
    service.addToCart(mockProduct);
    service.updateQuantity(1, 5);
    expect(service.items()[0].quantity).toBe(5);
    expect(service.itemCount()).toBe(5);
  });

  it('should remove item when updating quantity to 0', () => {
    service.addToCart(mockProduct);
    service.updateQuantity(1, 0);
    expect(service.items()).toEqual([]);
  });

  it('should remove item when updating quantity to negative', () => {
    service.addToCart(mockProduct);
    service.updateQuantity(1, -1);
    expect(service.items()).toEqual([]);
  });

  it('should clear cart', () => {
    service.addToCart(mockProduct);
    service.addToCart(mockProduct2);
    service.clearCart();
    expect(service.items()).toEqual([]);
    expect(service.total()).toBe(0);
    expect(service.itemCount()).toBe(0);
  });

  it('total should compute correctly with multiple items', () => {
    service.addToCart(mockProduct, 2);
    service.addToCart(mockProduct2, 3);
    const expectedTotal = 29.99 * 2 + 49.99 * 3;
    expect(Math.abs(service.total() - expectedTotal)).toBeLessThan(0.01);
  });
});

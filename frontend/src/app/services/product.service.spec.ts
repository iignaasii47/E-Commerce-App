import { TestBed } from '@angular/core/testing';
import { ProductService } from './product.service';

describe('ProductService', () => {
  let service: ProductService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(ProductService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should return 10 mock products', () => {
    expect(service.allProducts().length).toBe(10);
  });

  it('should compute unique categories', () => {
    const categories = service.categories();
    expect(categories).toContain('peripherals');
    expect(categories).toContain('displays');
    expect(categories).toContain('audio');
    expect(categories).toContain('accessories');
    expect(categories).toContain('storage');
    expect(categories.length).toBe(5);
  });

  it('should get product by id', () => {
    const product = service.getProductById(1);
    expect(product).toBeDefined();
    expect(product!.name).toBe('Mechanical Keyboard MK-750');
  });

  it('should return undefined for non-existent id', () => {
    expect(service.getProductById(999)).toBeUndefined();
  });

  it('should get products by category', () => {
    const peripherals = service.getByCategory('peripherals');
    expect(peripherals.length).toBe(2);
    expect(peripherals.every((p) => p.category === 'peripherals')).toBe(true);
  });

  it('should return empty array for non-existent category', () => {
    expect(service.getByCategory('nonexistent')).toEqual([]);
  });

  it('should search products by name (case insensitive)', () => {
    const results = service.search('keyboard');
    expect(results.length).toBe(1);
    expect(results[0].id).toBe(1);
  });

  it('should search products by description', () => {
    const results = service.search('curved');
    expect(results.length).toBe(1);
    expect(results[0].id).toBe(2);
  });

  it('should search products by category', () => {
    const results = service.search('audio');
    expect(results.length).toBe(1);
    expect(results[0].id).toBe(5);
  });

  it('should return multiple results for broad search', () => {
    const results = service.search('usb');
    expect(results.length).toBeGreaterThanOrEqual(2);
  });

  it('should return empty for unmatched search', () => {
    expect(service.search('zzzzz')).toEqual([]);
  });

  it('search should be case insensitive', () => {
    const lower = service.search('keyboard');
    const upper = service.search('KEYBOARD');
    const mixed = service.search('KeyBoard');
    expect(lower.length).toBe(upper.length);
    expect(upper.length).toBe(mixed.length);
  });
});

import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { ProductService } from './product.service';
import { environment } from '../../environments/environment';
import { Product } from '../models';

const MOCK_PRODUCTS: Product[] = [
  {
    id: 1, name: 'Mechanical Keyboard MK-750',
    description: 'Hot-swappable mechanical keyboard with RGB backlighting.',
    price: 149.99, category: 'peripherals',
    image: 'https://placehold.co/400x300/0a0e14/00ff41?text=MK-750',
    stock: 23, rating: 4.7,
  },
  {
    id: 2, name: 'Ultrawide Monitor 34"',
    description: '34-inch curved ultrawide QHD monitor.',
    price: 599.99, category: 'displays',
    image: 'https://placehold.co/400x300/0a0e14/7dd3fc?text=UW-34',
    stock: 8, rating: 4.9,
  },
  {
    id: 3, name: 'Wireless Mouse Pro',
    description: 'Ergonomic wireless mouse with 25K DPI sensor.',
    price: 79.99, category: 'peripherals',
    image: 'https://placehold.co/400x300/0a0e14/ffb000?text=WM-PRO',
    stock: 45, rating: 4.5,
  },
  {
    id: 4, name: 'USB-C Hub 7-in-1',
    description: 'USB-C hub with HDMI 4K, 3x USB-A 3.0.',
    price: 39.99, category: 'accessories',
    image: 'https://placehold.co/400x300/0a0e14/ff6bcb?text=USB-C',
    stock: 120, rating: 4.3,
  },
  {
    id: 5, name: 'Noise-Cancelling Headphones',
    description: 'Over-ear ANC headphones with 40-hour battery.',
    price: 249.99, category: 'audio',
    image: 'https://placehold.co/400x300/0a0e14/e6e6e9?text=ANC-40',
    stock: 15, rating: 4.8,
  },
];

describe('ProductService', () => {
  let service: ProductService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()],
    });
    httpMock = TestBed.inject(HttpTestingController);
    service = TestBed.inject(ProductService);
    const req = httpMock.expectOne(environment.apiUrl + '/api/products');
    expect(req.request.method).toBe('GET');
    req.flush(MOCK_PRODUCTS);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should return products loaded from API', () => {
    expect(service.allProducts().length).toBe(5);
  });

  it('should compute unique categories', () => {
    const categories = service.categories();
    expect(categories).toContain('peripherals');
    expect(categories).toContain('displays');
    expect(categories).toContain('audio');
    expect(categories).toContain('accessories');
    expect(categories.length).toBe(4);
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
    expect(results.length).toBeGreaterThanOrEqual(1);
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

  it('should handle API error gracefully', () => {
    TestBed.resetTestingModule();
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()],
    });
    httpMock = TestBed.inject(HttpTestingController);
    service = TestBed.inject(ProductService);
    const req = httpMock.expectOne(environment.apiUrl + '/api/products');
    req.error(new ProgressEvent('Network error'));

    expect(service.allProducts().length).toBe(0);
  });
});

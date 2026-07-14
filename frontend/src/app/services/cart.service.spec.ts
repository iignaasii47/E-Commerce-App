import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { CartService } from './cart.service';
import { environment } from '../../environments/environment';

describe('CartService', () => {
  let service: CartService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()],
    });
    httpMock = TestBed.inject(HttpTestingController);
    service = TestBed.inject(CartService);
    httpMock.expectOne(environment.apiUrl + '/api/cart').flush([]);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should start with empty cart', () => {
    expect(service.items()).toEqual([]);
    expect(service.total()).toBe(0);
    expect(service.itemCount()).toBe(0);
  });

  it('addToCart should POST and update items', () => {
    const apiItem = { id: 1, productId: 1, productName: 'Test', unitPrice: 29.99, quantity: 2, subtotal: 59.98 };

    service.addToCart(1, 2);
    const req = httpMock.expectOne(environment.apiUrl + '/api/cart?productId=1&quantity=2');
    expect(req.request.method).toBe('POST');
    req.flush(apiItem);

    expect(service.items().length).toBe(1);
    expect(service.items()[0].productId).toBe(1);
    expect(service.items()[0].quantity).toBe(2);
    expect(service.total()).toBe(59.98);
    expect(service.itemCount()).toBe(2);
  });

  it('addToCart should replace existing item for same product', () => {
    const item1 = { id: 1, productId: 1, productName: 'Test', unitPrice: 10, quantity: 1, subtotal: 10 };
    const item2 = { id: 2, productId: 1, productName: 'Test', unitPrice: 10, quantity: 3, subtotal: 30 };

    service.addToCart(1, 1);
    httpMock.expectOne(environment.apiUrl + '/api/cart?productId=1&quantity=1').flush(item1);

    service.addToCart(1, 3);
    httpMock.expectOne(environment.apiUrl + '/api/cart?productId=1&quantity=3').flush(item2);

    expect(service.items().length).toBe(1);
    expect(service.items()[0].quantity).toBe(3);
    expect(service.total()).toBe(30);
  });

  it('removeFromCart should DELETE and remove item', () => {
    service.items.set([{ id: 5, productId: 1, productName: 'X', unitPrice: 10, quantity: 1, subtotal: 10 }]);

    service.removeFromCart(5);
    httpMock.expectOne(environment.apiUrl + '/api/cart/5').flush({});

    expect(service.items()).toEqual([]);
  });

  it('updateQuantity should update locally immediately', () => {
    service.items.set([{ id: 5, productId: 1, productName: 'X', unitPrice: 10, quantity: 2, subtotal: 20 }]);

    service.updateQuantity(5, 1, 3);
    expect(service.items()[0].quantity).toBe(3);
  });

  it('updateQuantity with 0 should set quantity to 0 locally', () => {
    service.items.set([{ id: 5, productId: 1, productName: 'X', unitPrice: 10, quantity: 1, subtotal: 10 }]);

    service.updateQuantity(5, 1, 0);
    expect(service.items().length).toBe(1);
    expect(service.items()[0].quantity).toBe(0);
  });

  it('clearCart should DELETE all items', () => {
    service.items.set([
      { id: 1, productId: 1, productName: 'A', unitPrice: 10, quantity: 1, subtotal: 10 },
      { id: 2, productId: 2, productName: 'B', unitPrice: 20, quantity: 1, subtotal: 20 },
    ]);

    service.clearCart();
    httpMock.expectOne(environment.apiUrl + '/api/cart/1').flush({});
    httpMock.expectOne(environment.apiUrl + '/api/cart/2').flush({});

    expect(service.items()).toEqual([]);
    expect(service.total()).toBe(0);
    expect(service.itemCount()).toBe(0);
  });

  it('loadCart should fetch from API', () => {
    const items = [
      { id: 1, productId: 1, productName: 'A', unitPrice: 10, quantity: 2, subtotal: 20 },
    ];

    service.loadCart();
    httpMock.expectOne(environment.apiUrl + '/api/cart').flush(items);

    expect(service.items().length).toBe(1);
  });

  it('loadCart should handle API error gracefully', () => {
    service.loadCart();
    httpMock.expectOne(environment.apiUrl + '/api/cart').error(new ProgressEvent('error'));

    expect(service.items()).toEqual([]);
  });
});

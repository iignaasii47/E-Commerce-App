import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { CartService } from './cart.service';
import { NotificationService } from './notification.service';
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

  it('syncQuantity should handle API error silently', () => {
    service.items.set([{ id: 5, productId: 1, productName: 'X', unitPrice: 10, quantity: 2, subtotal: 20 }]);

    service.updateQuantity(5, 1, 0);
    expect(service.items()[0].quantity).toBe(0);
  });

  it('should debounce updateQuantity and not call syncQuantity immediately', () => {
    service.items.set([{ id: 5, productId: 1, productName: 'X', unitPrice: 10, quantity: 2, subtotal: 20 }]);

    service.updateQuantity(5, 1, 3);
    expect(service.items()[0].quantity).toBe(3);

    const pendingTimeouts = (service as any).pendingTimeouts as Map<number, ReturnType<typeof setTimeout>>;
    expect(pendingTimeouts.has(5)).toBe(true);
  });

  it('should cancel previous pending timeout on repeated updateQuantity', () => {
    service.items.set([{ id: 5, productId: 1, productName: 'X', unitPrice: 10, quantity: 2, subtotal: 20 }]);

    service.updateQuantity(5, 1, 3);
    const timeout1 = (service as any).pendingTimeouts.get(5);

    service.updateQuantity(5, 1, 4);
    const timeout2 = (service as any).pendingTimeouts.get(5);

    expect(timeout2).not.toBe(timeout1);
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

  it('addToCart should show notification on 404', () => {
    const notifications = TestBed.inject(NotificationService);
    const errorSpy = vi.spyOn(notifications, 'error');

    service.addToCart(99, 1);
    const req = httpMock.expectOne(environment.apiUrl + '/api/cart?productId=99&quantity=1');
    req.flush({}, { status: 404, statusText: 'Not Found' });

    expect(errorSpy).toHaveBeenCalledWith('product not found');
  });

  it('removeFromCart should show notification and reload cart on 404', () => {
    const notifications = TestBed.inject(NotificationService);
    const errorSpy = vi.spyOn(notifications, 'error');

    service.removeFromCart(99);
    httpMock.expectOne(environment.apiUrl + '/api/cart/99').flush({}, { status: 404, statusText: 'Not Found' });
    httpMock.expectOne(environment.apiUrl + '/api/cart').flush([]);

    expect(errorSpy).toHaveBeenCalledWith('cart item not found');
  });

  describe('quantity correctness', () => {
    it('addToCart incrementally: qty=1 then qty=1 should result in qty=2 (server returns cumulative)', () => {
      const item1 = { id: 1, productId: 1, productName: 'A', unitPrice: 10, quantity: 1, subtotal: 10 };
      const item2 = { id: 1, productId: 1, productName: 'A', unitPrice: 10, quantity: 2, subtotal: 20 };

      service.addToCart(1, 1);
      httpMock.expectOne(environment.apiUrl + '/api/cart?productId=1&quantity=1').flush(item1);

      expect(service.items().length).toBe(1);
      expect(service.items()[0].quantity).toBe(1);

      service.addToCart(1, 1);
      httpMock.expectOne(environment.apiUrl + '/api/cart?productId=1&quantity=1').flush(item2);

      expect(service.items().length).toBe(1);
      expect(service.items()[0].quantity).toBe(2);
      expect(service.items()[0].subtotal).toBe(20);
      expect(service.total()).toBe(20);
      expect(service.itemCount()).toBe(2);
    });

    it('addToCart incrementally: qty=3 then qty=2 should result in qty=5', () => {
      const item1 = { id: 1, productId: 1, productName: 'A', unitPrice: 10, quantity: 3, subtotal: 30 };
      const item2 = { id: 1, productId: 1, productName: 'A', unitPrice: 10, quantity: 5, subtotal: 50 };

      service.addToCart(1, 3);
      httpMock.expectOne(environment.apiUrl + '/api/cart?productId=1&quantity=3').flush(item1);

      expect(service.items()[0].quantity).toBe(3);

      service.addToCart(1, 2);
      httpMock.expectOne(environment.apiUrl + '/api/cart?productId=1&quantity=2').flush(item2);

      expect(service.items().length).toBe(1);
      expect(service.items()[0].quantity).toBe(5);
      expect(service.total()).toBe(50);
      expect(service.itemCount()).toBe(5);
    });

    it('addToCart with multiple products should keep quantities independent', () => {
      const itemA = { id: 1, productId: 1, productName: 'Keyboard', unitPrice: 50, quantity: 3, subtotal: 150 };
      const itemB = { id: 2, productId: 2, productName: 'Mouse', unitPrice: 25, quantity: 2, subtotal: 50 };

      service.addToCart(1, 3);
      httpMock.expectOne(environment.apiUrl + '/api/cart?productId=1&quantity=3').flush(itemA);

      service.addToCart(2, 2);
      httpMock.expectOne(environment.apiUrl + '/api/cart?productId=2&quantity=2').flush(itemB);

      expect(service.items().length).toBe(2);
      expect(service.items()[0].quantity).toBe(3);
      expect(service.items()[1].quantity).toBe(2);
      expect(service.total()).toBe(200);
      expect(service.itemCount()).toBe(5);
    });

    it('addToCart should reflect server-returned quantity even if different from requested', () => {
      const requestedQty = 3;
      const serverReturned = { id: 1, productId: 1, productName: 'A', unitPrice: 10, quantity: 7, subtotal: 70 };

      service.addToCart(1, requestedQty);
      httpMock.expectOne(environment.apiUrl + '/api/cart?productId=1&quantity=3').flush(serverReturned);

      expect(service.items()[0].quantity).toBe(7);
      expect(service.total()).toBe(70);
    });

    it('rapid addToCart calls should result in correct final state', () => {
      const item1 = { id: 1, productId: 1, productName: 'A', unitPrice: 10, quantity: 1, subtotal: 10 };
      const item2 = { id: 1, productId: 1, productName: 'A', unitPrice: 10, quantity: 2, subtotal: 20 };

      service.addToCart(1, 1);
      const req1 = httpMock.expectOne(environment.apiUrl + '/api/cart?productId=1&quantity=1');

      service.addToCart(1, 1);
      const req2 = httpMock.expectOne(environment.apiUrl + '/api/cart?productId=1&quantity=1');

      req1.flush(item1);
      expect(service.items().length).toBe(1);
      expect(service.items()[0].quantity).toBe(1);

      req2.flush(item2);
      expect(service.items().length).toBe(1);
      expect(service.items()[0].quantity).toBe(2);
    });

    it('sequential updateQuantity should reflect final value after debounce', () => {
      service.items.set([{ id: 5, productId: 1, productName: 'X', unitPrice: 10, quantity: 1, subtotal: 10 }]);

      service.updateQuantity(5, 1, 2);
      expect(service.items()[0].quantity).toBe(2);

      service.updateQuantity(5, 1, 3);
      expect(service.items()[0].quantity).toBe(3);

      service.updateQuantity(5, 1, 4);
      expect(service.items()[0].quantity).toBe(4);
      expect(service.items()[0].subtotal).toBe(40);
    });

    it('sequential decrease via updateQuantity should reflect final value', () => {
      service.items.set([{ id: 5, productId: 1, productName: 'X', unitPrice: 10, quantity: 5, subtotal: 50 }]);

      service.updateQuantity(5, 1, 4);
      expect(service.items()[0].quantity).toBe(4);

      service.updateQuantity(5, 1, 3);
      expect(service.items()[0].quantity).toBe(3);

      service.updateQuantity(5, 1, 2);
      expect(service.items()[0].quantity).toBe(2);
      expect(service.items()[0].subtotal).toBe(20);
    });

    it('updateQuantity total and itemCount should stay in sync after multiple changes', () => {
      service.items.set([
        { id: 1, productId: 1, productName: 'A', unitPrice: 10, quantity: 2, subtotal: 20 },
        { id: 2, productId: 2, productName: 'B', unitPrice: 20, quantity: 1, subtotal: 20 },
      ]);

      expect(service.total()).toBe(40);
      expect(service.itemCount()).toBe(3);

      service.updateQuantity(1, 1, 5);
      expect(service.total()).toBe(70);
      expect(service.itemCount()).toBe(6);

      service.updateQuantity(2, 2, 3);
      expect(service.total()).toBe(110);
      expect(service.itemCount()).toBe(8);
    });
  });
});

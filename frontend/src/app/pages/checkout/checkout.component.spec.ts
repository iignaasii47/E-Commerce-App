import { TestBed } from '@angular/core/testing';
import { provideRouter, Router } from '@angular/router';
import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { CheckoutComponent } from './checkout.component';
import { CartService, AuthService, NotificationService } from '../../services';
import { environment } from '../../../environments/environment';

const apiItem = () => ({
  id: 1, productId: 1, productName: 'Test Item', unitPrice: 29.99, quantity: 1, subtotal: 29.99,
});

describe('CheckoutComponent', () => {
  async function setup() {
    await TestBed.configureTestingModule({
      imports: [CheckoutComponent],
      providers: [provideRouter([]), provideHttpClient(), provideHttpClientTesting()],
    }).compileComponents();
    const httpMock = TestBed.inject(HttpTestingController);
    const fixture = TestBed.createComponent(CheckoutComponent);
    httpMock.expectOne(environment.apiUrl + '/api/cart').flush([]);
    const component = fixture.componentInstance;
    fixture.detectChanges();
    return { fixture, component, httpMock };
  }

  it('should create', async () => {
    const { component } = await setup();
    expect(component).toBeTruthy();
  });

  it('should show empty state when cart is empty', async () => {
    const { fixture } = await setup();
    expect(fixture.nativeElement.textContent).toContain('cart is empty');
    expect(fixture.nativeElement.querySelector('.checkout-layout')).toBeFalsy();
  });

  it('should show checkout form when cart has items', async () => {
    const { fixture } = await setup();
    const cart = TestBed.inject(CartService);
    cart.items.set([apiItem()]);
    fixture.detectChanges();
    expect(fixture.nativeElement.querySelector('.checkout-layout')).toBeTruthy();
    expect(fixture.nativeElement.textContent).toContain('Test Item');
    expect(fixture.nativeElement.textContent).toContain('$29.99');
  });

  it('should show order summary', async () => {
    const { fixture } = await setup();
    const cart = TestBed.inject(CartService);
    cart.items.set([apiItem()]);
    fixture.detectChanges();
    expect(fixture.nativeElement.textContent).toContain('order summary');
    expect(fixture.nativeElement.textContent).toContain('total:');
  });

  it('should have shipping address fields', async () => {
    const { fixture } = await setup();
    const cart = TestBed.inject(CartService);
    cart.items.set([apiItem()]);
    fixture.detectChanges();
    expect(fixture.nativeElement.textContent).toContain('shipping info');
    expect(fixture.nativeElement.textContent).toContain('address');
    expect(fixture.nativeElement.textContent).toContain('city');
    expect(fixture.nativeElement.textContent).toContain('zip');
  });

  it('should have payment fields', async () => {
    const { fixture } = await setup();
    const cart = TestBed.inject(CartService);
    cart.items.set([apiItem()]);
    fixture.detectChanges();
    expect(fixture.nativeElement.textContent).toContain('payment (mock)');
    expect(fixture.nativeElement.textContent).toContain('card');
    expect(fixture.nativeElement.textContent).toContain('exp');
  });

  it('should show error when placing order without login', async () => {
    const { fixture } = await setup();
    const cart = TestBed.inject(CartService);
    cart.items.set([apiItem()]);
    const auth = TestBed.inject(AuthService);
    auth.logout();
    const notifications = TestBed.inject(NotificationService);
    const errorSpy = vi.spyOn(notifications, 'error');
    fixture.detectChanges();

    fixture.componentInstance.placeOrder();
    expect(errorSpy).toHaveBeenCalledWith('please login to place an order');
  });

  it('should place order successfully when logged in', async () => {
    const { fixture, httpMock } = await setup();
    const cart = TestBed.inject(CartService);
    cart.items.set([apiItem()]);
    const auth = TestBed.inject(AuthService);
    (auth as any).currentUser.set({ id: 1, username: 'user', email: 'user@test.com' });
    const loadCartSpy = vi.spyOn(cart, 'loadCart');
    const router = TestBed.inject(Router);
    vi.spyOn(router, 'navigate').mockResolvedValue(true);
    fixture.componentInstance.address.set('123 Main St');
    fixture.componentInstance.city.set('Springfield');
    fixture.componentInstance.zip.set('12345');
    fixture.detectChanges();

    fixture.componentInstance.placeOrder();
    httpMock.expectOne(environment.apiUrl + '/api/orders').flush({ id: 1, status: 'CONFIRMED' });
    httpMock.expectOne(environment.apiUrl + '/api/cart').flush([]);
    expect(loadCartSpy).toHaveBeenCalled();
  });

  it('should show back link when cart is empty', async () => {
    const { fixture } = await setup();
    const link = fixture.nativeElement.querySelector('.back-link') as HTMLElement;
    expect(link).toBeTruthy();
    expect(link.textContent).toContain('cd products/');
  });

  it('should have place-order button when cart has items', async () => {
    const { fixture } = await setup();
    const cart = TestBed.inject(CartService);
    cart.items.set([apiItem()]);
    fixture.detectChanges();
    const btn = fixture.nativeElement.querySelector('app-terminal-button') as HTMLElement;
    expect(btn).toBeTruthy();
    expect(btn.textContent).toContain('place-order');
  });

  it('should update signals through template input events', async () => {
    const { fixture } = await setup();
    const cart = TestBed.inject(CartService);
    cart.items.set([apiItem()]);
    fixture.detectChanges();
    await fixture.whenStable();

    const inputs = fixture.nativeElement.querySelectorAll('.t-input__field') as NodeListOf<HTMLInputElement>;
    inputs[0].value = '123 Main St';
    inputs[0].dispatchEvent(new Event('input'));
    inputs[1].value = 'SF';
    inputs[1].dispatchEvent(new Event('input'));
    inputs[2].value = '94102';
    inputs[2].dispatchEvent(new Event('input'));
    inputs[3].value = '4111';
    inputs[3].dispatchEvent(new Event('input'));
    inputs[4].value = '12/28';
    inputs[4].dispatchEvent(new Event('input'));
    fixture.detectChanges();

    expect(fixture.componentInstance.address()).toBe('123 Main St');
    expect(fixture.componentInstance.city()).toBe('SF');
    expect(fixture.componentInstance.zip()).toBe('94102');
    expect(fixture.componentInstance.card()).toBe('4111');
    expect(fixture.componentInstance.exp()).toBe('12/28');
  });

  it('should call placeOrder when button is clicked', async () => {
    const { fixture } = await setup();
    const cart = TestBed.inject(CartService);
    cart.items.set([apiItem()]);
    const notifications = TestBed.inject(NotificationService);
    const infoSpy = vi.spyOn(notifications, 'success');
    const auth = TestBed.inject(AuthService);
    (auth as any).currentUser.set({ id: 1, username: 'user', email: 'user@test.com' });
    fixture.detectChanges();
    await fixture.whenStable();

    const placeOrderFn = vi.spyOn(fixture.componentInstance, 'placeOrder');
    const btn = fixture.nativeElement.querySelector('app-terminal-button') as HTMLElement;
    btn.click();
    expect(placeOrderFn).toHaveBeenCalled();
  });
});

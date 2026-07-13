import { TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';
import { CheckoutComponent } from './checkout.component';
import { CartService, AuthService, NotificationService } from '../../services';

describe('CheckoutComponent', () => {
  async function setup() {
    await TestBed.configureTestingModule({
      imports: [CheckoutComponent],
      providers: [provideRouter([])],
    }).compileComponents();
    const fixture = TestBed.createComponent(CheckoutComponent);
    const component = fixture.componentInstance;
    fixture.detectChanges();
    return { fixture, component };
  }

  const mockProduct = () => ({
    id: 1, name: 'Test Item', description: '', price: 29.99,
    category: 'accessories', image: '', stock: 10, rating: 4,
  });

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
    cart.addToCart(mockProduct());
    fixture.detectChanges();
    expect(fixture.nativeElement.querySelector('.checkout-layout')).toBeTruthy();
    expect(fixture.nativeElement.textContent).toContain('Test Item');
    expect(fixture.nativeElement.textContent).toContain('$29.99');
  });

  it('should show order summary', async () => {
    const { fixture } = await setup();
    const cart = TestBed.inject(CartService);
    cart.addToCart(mockProduct());
    fixture.detectChanges();
    expect(fixture.nativeElement.textContent).toContain('order summary');
    expect(fixture.nativeElement.textContent).toContain('total:');
  });

  it('should have shipping address fields', async () => {
    const { fixture } = await setup();
    const cart = TestBed.inject(CartService);
    cart.addToCart(mockProduct());
    fixture.detectChanges();
    expect(fixture.nativeElement.textContent).toContain('shipping info');
    expect(fixture.nativeElement.textContent).toContain('address');
    expect(fixture.nativeElement.textContent).toContain('city');
    expect(fixture.nativeElement.textContent).toContain('zip');
  });

  it('should have payment fields', async () => {
    const { fixture } = await setup();
    const cart = TestBed.inject(CartService);
    cart.addToCart(mockProduct());
    fixture.detectChanges();
    expect(fixture.nativeElement.textContent).toContain('payment (mock)');
    expect(fixture.nativeElement.textContent).toContain('card');
    expect(fixture.nativeElement.textContent).toContain('exp');
  });

  it('should show error when placing order without login', async () => {
    const { fixture } = await setup();
    const cart = TestBed.inject(CartService);
    cart.addToCart(mockProduct());
    const auth = TestBed.inject(AuthService);
    auth.logout();
    const notifications = TestBed.inject(NotificationService);
    const errorSpy = vi.spyOn(notifications, 'error');
    fixture.detectChanges();

    fixture.componentInstance.placeOrder();
    expect(errorSpy).toHaveBeenCalledWith('please login to place an order');
  });

  it('should place order successfully when logged in', async () => {
    const { fixture } = await setup();
    const cart = TestBed.inject(CartService);
    cart.addToCart(mockProduct());
    const auth = TestBed.inject(AuthService);
    (auth as any).currentUser.set({ id: 1, username: 'user', email: 'user@test.com', token: 't' });
    const notifications = TestBed.inject(NotificationService);
    const successSpy = vi.spyOn(notifications, 'success');
    const clearSpy = vi.spyOn(cart, 'clearCart');
    fixture.detectChanges();

    fixture.componentInstance.placeOrder();
    expect(clearSpy).toHaveBeenCalled();
    expect(successSpy).toHaveBeenCalledWith('order placed successfully! (mock)');
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
    cart.addToCart(mockProduct());
    fixture.detectChanges();
    const btn = fixture.nativeElement.querySelector('app-terminal-button') as HTMLElement;
    expect(btn).toBeTruthy();
    expect(btn.textContent).toContain('place-order');
  });

  it('should update signals through template input events', async () => {
    const { fixture } = await setup();
    const cart = TestBed.inject(CartService);
    cart.addToCart(mockProduct());
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
    cart.addToCart(mockProduct());
    const notifications = TestBed.inject(NotificationService);
    const infoSpy = vi.spyOn(notifications, 'success');
    const auth = TestBed.inject(AuthService);
    (auth as any).currentUser.set({ id: 1, username: 'user', email: 'user@test.com', token: 't' });
    fixture.detectChanges();
    await fixture.whenStable();

    const placeOrderFn = vi.spyOn(fixture.componentInstance, 'placeOrder');
    const btn = fixture.nativeElement.querySelector('app-terminal-button') as HTMLElement;
    btn.click();
    expect(placeOrderFn).toHaveBeenCalled();
  });
});

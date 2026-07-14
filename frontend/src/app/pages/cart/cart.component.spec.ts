import { TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';
import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { CartComponent } from './cart.component';
import { CartService, NotificationService } from '../../services';
import { environment } from '../../../environments/environment';

const apiItem = (overrides: Partial<{ id: number; productId: number; productName: string; unitPrice: number; quantity: number; subtotal: number }> = {}) => ({
  id: 1,
  productId: 1,
  productName: 'Test Item',
  unitPrice: 29.99,
  quantity: 1,
  subtotal: 29.99,
  ...overrides,
});

describe('CartComponent', () => {
  async function setup() {
    await TestBed.configureTestingModule({
      imports: [CartComponent],
      providers: [provideRouter([]), provideHttpClient(), provideHttpClientTesting()],
    }).compileComponents();
    const httpMock = TestBed.inject(HttpTestingController);
    const fixture = TestBed.createComponent(CartComponent);
    httpMock.expectOne(environment.apiUrl + '/api/cart').flush([]);
    const component = fixture.componentInstance;
    fixture.detectChanges();
    return { fixture, component, httpMock };
  }

  it('should create', async () => {
    const { component } = await setup();
    expect(component).toBeTruthy();
  });

  it('should show empty cart by default', async () => {
    const { fixture } = await setup();
    expect(fixture.nativeElement.querySelector('.empty-cart')).toBeTruthy();
  });

  it('should render page title', async () => {
    const { fixture } = await setup();
    expect(fixture.nativeElement.querySelector('.page-title')?.textContent).toContain('cat cart.json');
  });

  it('should show 0 items in subtitle when empty', async () => {
    const { fixture } = await setup();
    expect(fixture.nativeElement.textContent).toContain('0 items in cart');
  });

  it('should show cart items when added', async () => {
    const { fixture } = await setup();
    const cart = TestBed.inject(CartService);
    cart.items.set([apiItem()]);
    fixture.detectChanges();
    expect(fixture.nativeElement.querySelector('.empty-cart')).toBeFalsy();
    expect(fixture.nativeElement.querySelector('.cart-list')).toBeTruthy();
    expect(fixture.nativeElement.textContent).toContain('Test Item');
    expect(fixture.nativeElement.textContent).toContain('1 items in cart');
  });

  it('should show subtotal and total', async () => {
    const { fixture } = await setup();
    const cart = TestBed.inject(CartService);
    cart.items.set([apiItem({ unitPrice: 49.99, subtotal: 49.99 })]);
    fixture.detectChanges();
    expect(fixture.nativeElement.textContent).toContain('subtotal:');
    expect(fixture.nativeElement.textContent).toContain('$49.99');
    expect(fixture.nativeElement.textContent).toContain('free');
  });

  it('should show quantity controls', async () => {
    const { fixture } = await setup();
    const cart = TestBed.inject(CartService);
    cart.items.set([apiItem()]);
    fixture.detectChanges();
    const qtyBtns = fixture.nativeElement.querySelectorAll('.qty-btn');
    expect(qtyBtns.length).toBe(2);
    expect(qtyBtns[0].textContent).toBe('-');
    expect(qtyBtns[1].textContent).toBe('+');
  });

  it('should show remove button for each item', async () => {
    const { fixture } = await setup();
    const cart = TestBed.inject(CartService);
    cart.items.set([apiItem()]);
    fixture.detectChanges();
    const removeBtn = fixture.nativeElement.querySelector('.remove-btn') as HTMLElement;
    expect(removeBtn).toBeTruthy();
    expect(removeBtn.textContent).toContain('rm');
  });

  it('should update quantity via - button', async () => {
    const { fixture } = await setup();
    const cart = TestBed.inject(CartService);
    const updateSpy = vi.spyOn(cart, 'updateQuantity');
    cart.items.set([apiItem({ quantity: 3 })]);
    fixture.detectChanges();
    const minusBtn = fixture.nativeElement.querySelectorAll('.qty-btn')[0] as HTMLButtonElement;
    minusBtn.click();
    expect(updateSpy).toHaveBeenCalledWith(1, 1, 2);
  });

  it('should update quantity via + button', async () => {
    const { fixture } = await setup();
    const cart = TestBed.inject(CartService);
    const updateSpy = vi.spyOn(cart, 'updateQuantity');
    cart.items.set([apiItem({ quantity: 1 })]);
    fixture.detectChanges();
    const plusBtn = fixture.nativeElement.querySelectorAll('.qty-btn')[1] as HTMLButtonElement;
    plusBtn.click();
    expect(updateSpy).toHaveBeenCalledWith(1, 1, 2);
  });

  it('should remove item when clicking remove button', async () => {
    const { fixture } = await setup();
    const cart = TestBed.inject(CartService);
    const notifications = TestBed.inject(NotificationService);
    const removeSpy = vi.spyOn(cart, 'removeFromCart');
    const infoSpy = vi.spyOn(notifications, 'info');
    cart.items.set([apiItem()]);
    fixture.detectChanges();
    const removeBtn = fixture.nativeElement.querySelector('.remove-btn') as HTMLButtonElement;
    removeBtn.click();
    expect(removeSpy).toHaveBeenCalledWith(1);
    expect(infoSpy).toHaveBeenCalledWith('Test Item removed from cart');
  });

  it('should show checkout button and continue link when cart has items', async () => {
    const { fixture } = await setup();
    const cart = TestBed.inject(CartService);
    cart.items.set([apiItem()]);
    fixture.detectChanges();
    expect(fixture.nativeElement.querySelector('.continue-link')).toBeTruthy();
    expect(fixture.nativeElement.querySelector('app-terminal-button')).toBeTruthy();
  });

  it('checkout should show info notification', async () => {
    const { fixture } = await setup();
    const notifications = TestBed.inject(NotificationService);
    const infoSpy = vi.spyOn(notifications, 'info');
    fixture.componentInstance.checkout();
    expect(infoSpy).toHaveBeenCalledWith('checkout flow coming soon...');
  });
});

import { TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';
import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { ProductDetailComponent } from './product-detail.component';
import { ProductService, CartService, NotificationService } from '../../../services';
import { environment } from '../../../../environments/environment';

const MOCK_PRODUCTS = [
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
];

describe('ProductDetailComponent', () => {
  async function setup() {
    await TestBed.configureTestingModule({
      imports: [ProductDetailComponent],
      providers: [provideRouter([]), provideHttpClient(), provideHttpClientTesting()],
    }).compileComponents();
    const httpMock = TestBed.inject(HttpTestingController);
    const fixture = TestBed.createComponent(ProductDetailComponent);
    httpMock.expectOne(environment.apiUrl + '/api/products').flush(MOCK_PRODUCTS);
    const component = fixture.componentInstance;
    fixture.detectChanges();
    return { fixture, component, httpMock };
  }

  it('should create', async () => {
    const { component } = await setup();
    expect(component).toBeTruthy();
  });

  it('should show product not found for invalid id', async () => {
    const { fixture } = await setup();
    const notFound = fixture.nativeElement.querySelector('.not-found') as HTMLElement;
    expect(notFound).toBeTruthy();
    expect(notFound.textContent).toContain('product not found');
  });

  it('should display product details when product is found', async () => {
    const { fixture } = await setup();
    const productService = TestBed.inject(ProductService);
    const product = productService.getProductById(1)!;
    fixture.componentInstance.product.set(product);
    fixture.detectChanges();

    expect(fixture.nativeElement.querySelector('.detail-layout')).toBeTruthy();
    expect(fixture.nativeElement.textContent).toContain('Mechanical Keyboard MK-750');
    expect(fixture.nativeElement.textContent).toContain('#1');
    expect(fixture.nativeElement.textContent).toContain('peripherals');
    expect(fixture.nativeElement.textContent).toContain('$149.99');
  });

  it('should show stock count', async () => {
    const { fixture } = await setup();
    const productService = TestBed.inject(ProductService);
    const product = productService.getProductById(1)!;
    fixture.componentInstance.product.set(product);
    fixture.detectChanges();

    expect(fixture.nativeElement.textContent).toContain('in stock: 23');
  });

  it('should show low stock warning', async () => {
    const { fixture } = await setup();
    const productService = TestBed.inject(ProductService);
    const product = productService.getProductById(2)!;
    fixture.componentInstance.product.set(product);
    fixture.detectChanges();

    expect(fixture.nativeElement.textContent).toContain('in stock: 8');
    const stockEl = fixture.nativeElement.querySelector('.product-stock') as HTMLElement;
    expect(stockEl.classList.contains('low')).toBe(true);
  });

  it('should show out of stock for stock = 0', async () => {
    const { fixture } = await setup();
    const outOfStock = {
      id: 99, name: 'Sold Out', description: '', price: 10, category: 'test',
      image: '', stock: 0, rating: 0,
    };
    fixture.componentInstance.product.set(outOfStock);
    fixture.detectChanges();

    expect(fixture.nativeElement.textContent).toContain('out of stock');
    const btn = fixture.nativeElement.querySelector('.t-btn') as HTMLButtonElement;
    expect(btn.disabled).toBe(true);
  });

  it('should display rating stars', async () => {
    const { fixture } = await setup();
    const productService = TestBed.inject(ProductService);
    fixture.componentInstance.product.set(productService.getProductById(1)!);
    fixture.detectChanges();

    const stars = fixture.nativeElement.querySelectorAll('.star.filled');
    expect(stars.length).toBe(4);
    expect(fixture.nativeElement.textContent).toContain('4.7/5.0');
  });

  it('should have back link', async () => {
    const { fixture } = await setup();
    const productService = TestBed.inject(ProductService);
    fixture.componentInstance.product.set(productService.getProductById(1)!);
    fixture.detectChanges();

    const link = fixture.nativeElement.querySelector('.back-link') as HTMLElement;
    expect(link).toBeTruthy();
    expect(link.textContent).toContain('cd ..');
  });

  it('should start with quantity 1', async () => {
    const { component } = await setup();
    expect(component.quantity()).toBe(1);
  });

  it('should increment quantity', async () => {
    const { component } = await setup();
    const productService = TestBed.inject(ProductService);
    component.product.set(productService.getProductById(1)!);
    component.incrementQty();
    expect(component.quantity()).toBe(2);
  });

  it('should not increment beyond stock', async () => {
    const { component } = await setup();
    const productService = TestBed.inject(ProductService);
    component.product.set(productService.getProductById(2)!);
    component.quantity.set(8);
    component.incrementQty();
    expect(component.quantity()).toBe(8);
  });

  it('should decrement quantity', async () => {
    const { component } = await setup();
    component.quantity.set(3);
    component.decrementQty();
    expect(component.quantity()).toBe(2);
  });

  it('should not decrement below 1', async () => {
    const { component } = await setup();
    component.decrementQty();
    expect(component.quantity()).toBe(1);
  });

  it('should not increment when product is null', async () => {
    const { component } = await setup();
    component.product.set(undefined as any);
    component.quantity.set(1);
    component.incrementQty();
    expect(component.quantity()).toBe(1);
  });

  it('should show subtotal', async () => {
    const { fixture } = await setup();
    const productService = TestBed.inject(ProductService);
    const product = productService.getProductById(1)!;
    fixture.componentInstance.product.set(product);
    fixture.detectChanges();

    expect(fixture.nativeElement.textContent).toContain('$149.99');
  });

  it('should add to cart and show notification', async () => {
    const { fixture } = await setup();
    const cartService = TestBed.inject(CartService);
    const notifications = TestBed.inject(NotificationService);
    const addSpy = vi.spyOn(cartService, 'addToCart');
    const successSpy = vi.spyOn(notifications, 'success');
    const productService = TestBed.inject(ProductService);
    const product = productService.getProductById(1)!;
    fixture.componentInstance.product.set(product);
    fixture.componentInstance.quantity.set(2);
    fixture.detectChanges();

    fixture.componentInstance.addToCart(product);
    expect(addSpy).toHaveBeenCalledWith(1, 2);
    expect(successSpy).toHaveBeenCalledWith('Mechanical Keyboard MK-750 added to cart');
  });

  it('getStars should return correct array', async () => {
    const { component } = await setup();
    expect(component.getStars(4.7)).toEqual([0, 1, 2, 3]);
    expect(component.getStars(0)).toEqual([]);
    expect(component.getStars(5)).toEqual([0, 1, 2, 3, 4]);
  });

  it('should show add-to-cart button', async () => {
    const { fixture } = await setup();
    const productService = TestBed.inject(ProductService);
    fixture.componentInstance.product.set(productService.getProductById(1)!);
    fixture.detectChanges();

    expect(fixture.nativeElement.textContent).toContain('add-to-cart');
  });
});

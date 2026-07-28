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
    httpMock.expectOne((r) => r.url === environment.apiUrl + '/api/products').flush({
      content: MOCK_PRODUCTS, totalElements: 2, totalPages: 1, currentPage: 0, pageSize: 10,
    });
    httpMock.expectOne(environment.apiUrl + '/api/products/categories').flush(['peripherals', 'displays']);
    httpMock.expectOne(environment.apiUrl + '/api/cart').flush([]);
    const component = fixture.componentInstance;
    component.loading.set(false);
    fixture.detectChanges();
    return { fixture, component, httpMock };
  }

  it('should create', async () => {
    const { component } = await setup();
    expect(component).toBeTruthy();
  });

  it('should show loading state initially', async () => {
    const { fixture, component } = await setup();
    component.loading.set(true);
    fixture.detectChanges();
    const loading = fixture.nativeElement.querySelector('.loading') as HTMLElement;
    expect(loading).toBeTruthy();
    expect(loading.textContent).toContain('loading product');
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

  describe('quantity selector integration', () => {
    it('should pass qty=3 to addToCart after three increments', async () => {
      const { component, fixture } = await setup();
      const cartService = TestBed.inject(CartService);
      const addSpy = vi.spyOn(cartService, 'addToCart');
      const productService = TestBed.inject(ProductService);
      const product = productService.getProductById(1)!;
      component.product.set(product);
      fixture.detectChanges();

      component.incrementQty();
      component.incrementQty();

      component.addToCart(product);
      expect(addSpy).toHaveBeenCalledWith(1, 3);
    });

    it('should pass cumulative qty=5 after five increments', async () => {
      const { component, fixture } = await setup();
      const cartService = TestBed.inject(CartService);
      const addSpy = vi.spyOn(cartService, 'addToCart');
      const productService = TestBed.inject(ProductService);
      const product = productService.getProductById(1)!;
      component.product.set(product);
      fixture.detectChanges();

      for (let i = 0; i < 5; i++) {
        component.incrementQty();
      }

      component.addToCart(product);
      expect(addSpy).toHaveBeenCalledWith(1, 6);
    });

    it('should handle increment/decrement mix and pass correct final qty', async () => {
      const { component, fixture } = await setup();
      const cartService = TestBed.inject(CartService);
      const addSpy = vi.spyOn(cartService, 'addToCart');
      const productService = TestBed.inject(ProductService);
      const product = productService.getProductById(1)!;
      component.product.set(product);
      fixture.detectChanges();

      component.incrementQty();
      component.incrementQty();
      component.incrementQty();
      component.decrementQty();
      component.incrementQty();

      expect(component.quantity()).toBe(4);
      component.addToCart(product);
      expect(addSpy).toHaveBeenCalledWith(1, 4);
    });

    it('should pass correct qty via DOM add-to-cart button click', async () => {
      const { fixture } = await setup();
      const cartService = TestBed.inject(CartService);
      const addSpy = vi.spyOn(cartService, 'addToCart');
      const notifications = TestBed.inject(NotificationService);
      vi.spyOn(notifications, 'success');
      const productService = TestBed.inject(ProductService);
      const product = productService.getProductById(1)!;
      fixture.componentInstance.product.set(product);
      fixture.detectChanges();

      const plusBtn = fixture.nativeElement.querySelectorAll('.qty-btn')[1] as HTMLButtonElement;
      plusBtn.click();
      plusBtn.click();
      fixture.detectChanges();

      const addBtn = fixture.nativeElement.querySelector('.t-btn--primary') as HTMLButtonElement;
      addBtn.click();

      expect(addSpy).toHaveBeenCalledWith(1, 3);
    });

    it('should show updated subtotal in DOM as quantity changes', async () => {
      const { fixture } = await setup();
      const productService = TestBed.inject(ProductService);
      const product = productService.getProductById(1)!;
      fixture.componentInstance.product.set(product);
      fixture.detectChanges();

      expect(fixture.nativeElement.textContent).toContain('$149.99');

      fixture.componentInstance.incrementQty();
      fixture.detectChanges();
      expect(fixture.nativeElement.textContent).toContain('$299.98');

      fixture.componentInstance.incrementQty();
      fixture.detectChanges();
      expect(fixture.nativeElement.textContent).toContain('$449.97');
    });
  });
});

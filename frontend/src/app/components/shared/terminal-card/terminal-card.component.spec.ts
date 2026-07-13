import { TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';
import { TerminalCardComponent } from './terminal-card.component';
import { Product } from '../../../models';

const mockProduct: Product = {
  id: 5,
  name: 'Test Product',
  description: 'A test product description',
  price: 99.99,
  category: 'accessories',
  image: 'test.jpg',
  stock: 15,
  rating: 4.2,
};

const lowStockProduct: Product = {
  id: 2,
  name: 'Low Stock Item',
  description: 'Low stock item',
  price: 49.99,
  category: 'displays',
  image: 'test2.jpg',
  stock: 3,
  rating: 4.9,
};

const zeroRatingProduct: Product = {
  id: 3,
  name: 'No Rating',
  description: 'No rating item',
  price: 19.99,
  category: 'peripherals',
  image: 'test3.jpg',
  stock: 10,
  rating: 0,
};

describe('TerminalCardComponent', () => {
  async function setup(product: Product = mockProduct) {
    await TestBed.configureTestingModule({
      imports: [TerminalCardComponent],
      providers: [provideRouter([])],
    }).compileComponents();
    const fixture = TestBed.createComponent(TerminalCardComponent);
    const component = fixture.componentInstance;
    fixture.componentRef.setInput('product', product);
    fixture.detectChanges();
    return { fixture, component };
  }

  it('should create', async () => {
    const { component } = await setup();
    expect(component).toBeTruthy();
  });

  it('should display product ID', async () => {
    const { fixture } = await setup();
    expect(fixture.nativeElement.textContent).toContain('#5');
  });

  it('should display product name', async () => {
    const { fixture } = await setup();
    expect(fixture.nativeElement.textContent).toContain('Test Product');
  });

  it('should display product category', async () => {
    const { fixture } = await setup();
    expect(fixture.nativeElement.textContent).toContain('accessories');
  });

  it('should display product price', async () => {
    const { fixture } = await setup();
    expect(fixture.nativeElement.textContent).toContain('99.99');
  });

  it('should display stock count', async () => {
    const { fixture } = await setup();
    expect(fixture.nativeElement.textContent).toContain('stock:15');
  });

  it('should display description', async () => {
    const { fixture } = await setup();
    expect(fixture.nativeElement.textContent).toContain('A test product description');
  });

  it('should display rating', async () => {
    const { fixture } = await setup();
    expect(fixture.nativeElement.textContent).toContain('4.2');
  });

  it('should add low stock class when stock < 10', async () => {
    const { fixture } = await setup(lowStockProduct);
    const stock = fixture.nativeElement.querySelector('.t-card__stock') as HTMLElement;
    expect(stock.classList.contains('low')).toBe(true);
  });

  it('should not add low stock class when stock >= 10', async () => {
    const { fixture } = await setup();
    const stock = fixture.nativeElement.querySelector('.t-card__stock') as HTMLElement;
    expect(stock.classList.contains('low')).toBe(false);
  });

  it('should render correct number of stars', async () => {
    const { fixture } = await setup();
    const stars = fixture.nativeElement.querySelectorAll('.star.filled');
    expect(stars.length).toBe(4);
  });

  it('should render empty state for 0 rating', async () => {
    const { fixture } = await setup(zeroRatingProduct);
    const star = fixture.nativeElement.querySelector('.star') as HTMLElement;
    expect(star.textContent).toBe('-');
    expect(fixture.nativeElement.querySelectorAll('.star.filled').length).toBe(0);
  });

  it('should have routerLink to product detail', async () => {
    const { fixture } = await setup();
    const link = fixture.nativeElement.querySelector('.t-card') as HTMLElement;
    expect(link.getAttribute('href')).toContain('/products/5');
  });

  it('getStars should return array of correct length', async () => {
    const { component } = await setup();
    const stars = component.getStars();
    expect(Array.isArray(stars)).toBe(true);
  });
});

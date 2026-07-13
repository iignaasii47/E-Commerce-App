import { TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';
import { ProductsComponent } from './products.component';
import { ProductService } from '../../services';

describe('ProductsComponent', () => {
  async function setup() {
    await TestBed.configureTestingModule({
      imports: [ProductsComponent],
      providers: [provideRouter([])],
    }).compileComponents();
    const fixture = TestBed.createComponent(ProductsComponent);
    const component = fixture.componentInstance;
    fixture.detectChanges();
    return { fixture, component };
  }

  it('should create', async () => {
    const { component } = await setup();
    expect(component).toBeTruthy();
  });

  it('should render product grid', async () => {
    const { fixture } = await setup();
    expect(fixture.nativeElement.querySelector('.product-grid')).toBeTruthy();
  });

  it('should render all 10 products by default', async () => {
    const { fixture } = await setup();
    const cards = fixture.nativeElement.querySelectorAll('.t-card');
    expect(cards.length).toBe(10);
  });

  it('should render category filter buttons', async () => {
    const { fixture } = await setup();
    const filters = fixture.nativeElement.querySelectorAll('.filter-tag');
    expect(filters.length).toBeGreaterThan(1);
    expect(filters[0].textContent?.trim()).toBe('all');
  });

  it('should show correct item count', async () => {
    const { fixture } = await setup();
    expect(fixture.nativeElement.textContent).toContain('10 items found');
  });

  it('should filter products when searching', async () => {
    const { fixture } = await setup();
    fixture.componentInstance.onSearch('keyboard');
    fixture.detectChanges();
    const cards = fixture.nativeElement.querySelectorAll('.t-card');
    expect(cards.length).toBe(1);
    expect(fixture.nativeElement.textContent).toContain('1 items found');
  });

  it('should show search query text when filtering', async () => {
    const { fixture } = await setup();
    fixture.componentInstance.onSearch('mouse');
    fixture.detectChanges();
    expect(fixture.nativeElement.textContent).toContain('searching:');
    expect(fixture.nativeElement.textContent).toContain('mouse');
  });

  it('should filter by category', async () => {
    const { fixture } = await setup();
    fixture.componentInstance.selectCategory('displays');
    fixture.detectChanges();
    const cards = fixture.nativeElement.querySelectorAll('.t-card');
    expect(cards.length).toBe(1);
  });

  it('should show empty state when no products match', async () => {
    const { fixture } = await setup();
    fixture.componentInstance.onSearch('zzzzzz');
    fixture.detectChanges();
    const cards = fixture.nativeElement.querySelectorAll('.t-card');
    expect(cards.length).toBe(0);
    expect(fixture.nativeElement.querySelector('.empty-state')).toBeTruthy();
  });

  it('should combine search and category filter', async () => {
    const { fixture } = await setup();
    fixture.componentInstance.selectCategory('peripherals');
    fixture.componentInstance.onSearch('keyboard');
    fixture.detectChanges();
    const cards = fixture.nativeElement.querySelectorAll('.t-card');
    expect(cards.length).toBe(1);
  });

  it('should highlight active category', async () => {
    const { fixture } = await setup();
    fixture.componentInstance.selectCategory('audio');
    fixture.detectChanges();
    const filters = fixture.nativeElement.querySelectorAll('.filter-tag');
    const active = Array.from(filters).find((f) =>
      (f as HTMLElement).classList.contains('active'),
    ) as HTMLElement | undefined;
    expect(active?.textContent?.trim()).toBe('audio');
  });

  it('should reset search when clearing', async () => {
    const { fixture } = await setup();
    fixture.componentInstance.onSearch('keyboard');
    fixture.detectChanges();
    expect(fixture.nativeElement.querySelectorAll('.t-card').length).toBe(1);
    fixture.componentInstance.onSearch('');
    fixture.detectChanges();
    expect(fixture.nativeElement.querySelectorAll('.t-card').length).toBe(10);
  });

  it('selectCategory with empty string shows all', async () => {
    const { fixture } = await setup();
    fixture.componentInstance.selectCategory('displays');
    fixture.detectChanges();
    expect(fixture.nativeElement.querySelectorAll('.t-card').length).toBe(1);
    fixture.componentInstance.selectCategory('');
    fixture.detectChanges();
    expect(fixture.nativeElement.querySelectorAll('.t-card').length).toBe(10);
  });
});

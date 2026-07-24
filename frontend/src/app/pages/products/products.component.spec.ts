import { TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';
import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { ProductsComponent } from './products.component';
import { environment } from '../../../environments/environment';
import { PaginatedResponse } from '../../models';

const MOCK_PRODUCTS = [
  { id: 1, name: 'Keyboard', description: 'A keyboard', price: 99.99, category: 'peripherals', image: '', stock: 10, rating: 4.5 },
  { id: 2, name: 'Monitor', description: 'A monitor', price: 299.99, category: 'displays', image: '', stock: 5, rating: 4.2 },
];

const PAGINATED_RESPONSE: PaginatedResponse<typeof MOCK_PRODUCTS[0]> = {
  content: MOCK_PRODUCTS,
  totalElements: 2,
  totalPages: 1,
  currentPage: 0,
  pageSize: 10,
};

function mockRequests(httpMock: HttpTestingController, response = PAGINATED_RESPONSE) {
  const productsReq = httpMock.expectOne((r) => r.url === environment.apiUrl + '/api/products');
  productsReq.flush(response);
  const categoriesReq = httpMock.expectOne((r) => r.url === environment.apiUrl + '/api/products/categories');
  categoriesReq.flush(['peripherals', 'displays']);
}

describe('ProductsComponent', () => {
  async function setup() {
    await TestBed.configureTestingModule({
      imports: [ProductsComponent],
      providers: [provideRouter([]), provideHttpClient(), provideHttpClientTesting()],
    }).compileComponents();
    const httpMock = TestBed.inject(HttpTestingController);
    const fixture = TestBed.createComponent(ProductsComponent);
    mockRequests(httpMock);
    fixture.detectChanges();
    await fixture.whenStable();
    return { fixture, fixtureInstance: fixture.componentInstance, httpMock };
  }

  it('should create', async () => {
    const { fixtureInstance } = await setup();
    expect(fixtureInstance).toBeTruthy();
  });

  it('should render product grid', async () => {
    const { fixture } = await setup();
    expect(fixture.nativeElement.querySelector('.product-grid')).toBeTruthy();
  });

  it('should render all products by default', async () => {
    const { fixture } = await setup();
    const cards = fixture.nativeElement.querySelectorAll('.t-card');
    expect(cards.length).toBe(2);
  });

  it('should render category filter buttons', async () => {
    const { fixture } = await setup();
    const filters = fixture.nativeElement.querySelectorAll('.filter-tag');
    expect(filters.length).toBeGreaterThanOrEqual(3);
  });

  it('should show correct item count from paginated response', async () => {
    const { fixture } = await setup();
    expect(fixture.nativeElement.textContent).toContain('2 items found');
  });

  it('should trigger search API call when searching', async () => {
    const { fixture, httpMock } = await setup();
    fixture.componentInstance.onSearch('keyboard');

    const req = httpMock.expectOne((r) => r.params.get('search') === 'keyboard');
    req.flush({ ...PAGINATED_RESPONSE, content: [MOCK_PRODUCTS[0]], totalElements: 1 });
    fixture.detectChanges();

    expect(fixture.nativeElement.textContent).toContain('1 items found');
  });

  it('should show search query text when filtering', async () => {
    const { fixture, httpMock } = await setup();
    fixture.componentInstance.onSearch('mouse');

    const req = httpMock.expectOne((r) => r.params.get('search') === 'mouse');
    req.flush({ ...PAGINATED_RESPONSE, content: [], totalElements: 0 });
    fixture.detectChanges();

    expect(fixture.nativeElement.textContent).toContain('searching:');
    expect(fixture.nativeElement.textContent).toContain('mouse');
  });

  it('should trigger category API when filtering by category', async () => {
    const { fixture, httpMock } = await setup();
    fixture.componentInstance.selectCategory('displays');

    const req = httpMock.expectOne((r) => r.params.get('category') === 'displays');
    req.flush({ ...PAGINATED_RESPONSE, content: [MOCK_PRODUCTS[1]], totalElements: 1 });
    fixture.detectChanges();

    const cards = fixture.nativeElement.querySelectorAll('.t-card');
    expect(cards.length).toBe(1);
  });

  it('should show empty state when no products match', async () => {
    const { fixture, httpMock } = await setup();
    fixture.componentInstance.onSearch('zzzzzz');

    const req = httpMock.expectOne((r) => r.params.get('search') === 'zzzzzz');
    req.flush({ ...PAGINATED_RESPONSE, content: [], totalElements: 0 });
    fixture.detectChanges();

    const cards = fixture.nativeElement.querySelectorAll('.t-card');
    expect(cards.length).toBe(0);
    expect(fixture.nativeElement.querySelector('.empty-state')).toBeTruthy();
  });

  it('should highlight active category', async () => {
    const { fixture, httpMock } = await setup();
    fixture.componentInstance.selectCategory('displays');

    const req = httpMock.expectOne((r) => r.params.get('category') === 'displays');
    req.flush({ ...PAGINATED_RESPONSE, content: [MOCK_PRODUCTS[1]], totalElements: 1 });
    fixture.detectChanges();

    const categorySection = fixture.nativeElement.querySelector('.category-filters');
    const active = Array.from(categorySection.querySelectorAll('.filter-tag')).find((f) =>
      (f as HTMLElement).classList.contains('active'),
    ) as HTMLElement | undefined;
    expect(active?.textContent?.trim()).toBe('displays');
  });

  it('should reset to all when selecting empty category', async () => {
    const { fixture, httpMock } = await setup();
    fixture.componentInstance.selectCategory('displays');

    const req1 = httpMock.expectOne((r) => r.params.get('category') === 'displays');
    req1.flush({ ...PAGINATED_RESPONSE, content: [MOCK_PRODUCTS[1]], totalElements: 1 });
    fixture.detectChanges();

    fixture.componentInstance.selectCategory('');

    const req2 = httpMock.expectOne((r) => !r.params.has('category'));
    req2.flush(PAGINATED_RESPONSE);
    fixture.detectChanges();

    const cards = fixture.nativeElement.querySelectorAll('.t-card');
    expect(cards.length).toBe(2);
  });

  it('should render sort option buttons', async () => {
    const { fixture } = await setup();
    const sortLabel = fixture.nativeElement.querySelector('.sort-label');
    expect(sortLabel).toBeTruthy();
    expect(sortLabel.textContent).toContain('sort by');
  });
});

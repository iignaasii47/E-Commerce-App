import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { ProductService } from './product.service';
import { environment } from '../../environments/environment';
import { Product, PaginatedResponse } from '../models';

const MOCK_PRODUCTS: Product[] = [
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
  {
    id: 3, name: 'Wireless Mouse Pro',
    description: 'Ergonomic wireless mouse with 25K DPI sensor.',
    price: 79.99, category: 'peripherals',
    image: 'https://placehold.co/400x300/0a0e14/ffb000?text=WM-PRO',
    stock: 45, rating: 4.5,
  },
];

const PAGINATED_RESPONSE: PaginatedResponse<Product> = {
  content: MOCK_PRODUCTS,
  totalElements: 3,
  totalPages: 1,
  currentPage: 0,
  pageSize: 10,
};

function mockInitialRequests(httpMock: HttpTestingController) {
  const productsReq = httpMock.expectOne(
    (req) => req.url === environment.apiUrl + '/api/products' && req.method === 'GET',
  );
  productsReq.flush(PAGINATED_RESPONSE);
  const categoriesReq = httpMock.expectOne(
    (req) => req.url === environment.apiUrl + '/api/products/categories' && req.method === 'GET',
  );
  categoriesReq.flush(['peripherals', 'displays']);
}

describe('ProductService', () => {
  let service: ProductService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()],
    });
    httpMock = TestBed.inject(HttpTestingController);
    service = TestBed.inject(ProductService);
    mockInitialRequests(httpMock);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should return products loaded from API', () => {
    expect(service.products().length).toBe(3);
  });

  it('should load categories from API', () => {
    expect(service.categories()).toContain('peripherals');
    expect(service.categories()).toContain('displays');
  });

  it('should return pagination metadata', () => {
    expect(service.total()).toBe(3);
    expect(service.pages()).toBe(1);
    expect(service.page()).toBe(0);
  });

  it('should get product by id from current page', () => {
    const product = service.getProductById(1);
    expect(product).toBeDefined();
    expect(product!.name).toBe('Mechanical Keyboard MK-750');
  });

  it('should return undefined for non-existent id', () => {
    expect(service.getProductById(999)).toBeUndefined();
  });

  it('should call API with search param when search is invoked', () => {
    service.search('keyboard');

    const req = httpMock.expectOne(
      (r) =>
        r.url === environment.apiUrl + '/api/products' &&
        r.params.get('search') === 'keyboard',
    );
    req.flush({ ...PAGINATED_RESPONSE, content: [MOCK_PRODUCTS[0]], totalElements: 1 });
  });

  it('should call API with category param when filtering by category', () => {
    service.filterByCategory('peripherals');

    const req = httpMock.expectOne(
      (r) =>
        r.url === environment.apiUrl + '/api/products' &&
        r.params.get('category') === 'peripherals',
    );
    req.flush({ ...PAGINATED_RESPONSE, content: [MOCK_PRODUCTS[0], MOCK_PRODUCTS[2]], totalElements: 2 });
  });

  it('should change page when goToPage is called', () => {
    service.setSort('price', 'desc');
    const sortReq = httpMock.expectOne((r) => r.params.get('sort') === 'price,desc');
    sortReq.flush({ ...PAGINATED_RESPONSE, totalPages: 3, currentPage: 0 });

    service.goToPage(1);

    const req = httpMock.expectOne(
      (r) => r.url === environment.apiUrl + '/api/products' && r.params.get('page') === '1',
    );
    req.flush({ ...PAGINATED_RESPONSE, currentPage: 1, totalPages: 3 });

    expect(service.page()).toBe(1);
  });

  it('should set sort params when setSort is called', () => {
    service.setSort('price', 'desc');

    const req = httpMock.expectOne(
      (r) =>
        r.url === environment.apiUrl + '/api/products' &&
        r.params.get('sort') === 'price,desc',
    );
    req.flush(PAGINATED_RESPONSE);

    expect(service.currentSort()).toBe('price');
    expect(service.currentSortDir()).toBe('desc');
  });

  it('should reset page to 0 when sort changes', () => {
    service.setSort('price', 'desc');
    let sortReq = httpMock.expectOne((r) => r.params.get('sort') === 'price,desc');
    sortReq.flush({ ...PAGINATED_RESPONSE, totalPages: 3, currentPage: 0 });

    service.goToPage(1);
    const pageReq = httpMock.expectOne(
      (r) => r.params.get('page') === '1',
    );
    pageReq.flush({ ...PAGINATED_RESPONSE, currentPage: 1, totalPages: 3 });

    service.setSort('name', 'desc');

    const req = httpMock.expectOne(
      (r) => r.params.get('page') === '0' && r.params.get('sort') === 'name,desc',
    );
    req.flush(PAGINATED_RESPONSE);

    expect(service.page()).toBe(0);
  });

  it('should handle API error gracefully', () => {
    TestBed.resetTestingModule();
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()],
    });
    httpMock = TestBed.inject(HttpTestingController);
    service = TestBed.inject(ProductService);

    const productsReq = httpMock.expectOne(
      (r) => r.url === environment.apiUrl + '/api/products',
    );
    productsReq.error(new ProgressEvent('Network error'));
    const categoriesReq = httpMock.expectOne(
      (r) => r.url === environment.apiUrl + '/api/products/categories',
    );
    categoriesReq.error(new ProgressEvent('Network error'));

    expect(service.products().length).toBe(0);
    expect(service.categories().length).toBe(0);
  });

  it('getProduct should fetch single product from API', () => {
    let result: Product | undefined;
    service.getProduct(1).subscribe((p) => (result = p));

    const req = httpMock.expectOne(environment.apiUrl + '/api/products/1');
    expect(req.request.method).toBe('GET');
    req.flush(MOCK_PRODUCTS[0]);

    expect(result).toBeDefined();
    expect(result!.name).toBe('Mechanical Keyboard MK-750');
  });

  it('getProduct should propagate HTTP errors', () => {
    let errorStatus: number | undefined;
    service.getProduct(999).subscribe({
      error: (err) => (errorStatus = err.status),
    });

    const req = httpMock.expectOne(environment.apiUrl + '/api/products/999');
    req.flush('', { status: 404, statusText: 'Not Found' });

    expect(errorStatus).toBe(404);
  });
});

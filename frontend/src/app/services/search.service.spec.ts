import { TestBed } from '@angular/core/testing';
import { SearchService } from './search.service';
import { ProductService } from './product.service';

describe('SearchService', () => {
  let service: SearchService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(SearchService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should start with empty query', () => {
    expect(service.query()).toBe('');
  });

  it('should return empty results when query is empty', () => {
    expect(service.results()).toEqual([]);
  });

  it('should return empty results when query is whitespace', () => {
    service.setQuery('   ');
    expect(service.results()).toEqual([]);
  });

  it('should delegate to ProductService.search() with query', () => {
    const productService = TestBed.inject(ProductService);
    const searchSpy = vi.spyOn(productService, 'search');
    service.setQuery('keyboard');
    const results = service.results();
    expect(searchSpy).toHaveBeenCalledWith('keyboard');
    expect(results.length).toBeGreaterThan(0);
  });

  it('should return matching products', () => {
    service.setQuery('keyboard');
    const results = service.results();
    expect(results.every((p) => p.name.toLowerCase().includes('keyboard'))).toBe(true);
  });

  it('hasQuery should be false when query is empty', () => {
    expect(service.hasQuery()).toBe(false);
  });

  it('hasQuery should be true when query is non-empty', () => {
    service.setQuery('monitor');
    expect(service.hasQuery()).toBe(true);
  });

  it('hasQuery should be false when query is whitespace only', () => {
    service.setQuery('   ');
    expect(service.hasQuery()).toBe(false);
  });

  it('clear should reset query to empty', () => {
    service.setQuery('test');
    service.clear();
    expect(service.query()).toBe('');
    expect(service.results()).toEqual([]);
  });

  it('setQuery should update the query signal', () => {
    service.setQuery('mouse');
    expect(service.query()).toBe('mouse');
  });
});

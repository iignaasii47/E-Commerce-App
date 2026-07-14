import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { SearchService } from './search.service';
import { environment } from '../../environments/environment';

describe('SearchService', () => {
  let service: SearchService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()],
    });
    const httpMock = TestBed.inject(HttpTestingController);
    service = TestBed.inject(SearchService);
    httpMock.expectOne(environment.apiUrl + '/api/products').flush([]);
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
    service.setQuery('keyboard');
    const results = service.results();
    expect(results).toEqual([]);
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

import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { StatusService } from './status.service';
import { environment } from '../../environments/environment';

describe('StatusService', () => {
  let service: StatusService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()],
    });
    httpMock = TestBed.inject(HttpTestingController);
    service = TestBed.inject(StatusService);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should be created', () => {
    httpMock.expectOne(environment.apiUrl + '/api/status').flush({
      api: 'UP', database: 'UP', timestamp: '2026-01-01T00:00:00Z',
    });
    expect(service).toBeTruthy();
  });

  it('should call checkStatus on construction with GET request', () => {
    const req = httpMock.expectOne(environment.apiUrl + '/api/status');
    expect(req.request.method).toBe('GET');
    req.flush({ api: 'UP', database: 'UP', timestamp: '2026-01-01T00:00:00Z' });
  });

  it('should start with apiStatus checking before response arrives', () => {
    const req = httpMock.expectOne(environment.apiUrl + '/api/status');
    expect(service.apiStatus()).toBe('checking');
    expect(service.dbStatus()).toBe('unknown');
    req.flush({ api: 'UP', database: 'UP', timestamp: '' });
    expect(service.apiStatus()).toBe('online');
  });

  it('should set apiStatus to online and dbStatus to online when API returns UP', () => {
    httpMock.expectOne(environment.apiUrl + '/api/status').flush({
      api: 'UP', database: 'UP', timestamp: '2026-01-01T00:00:00Z',
    });
    expect(service.apiStatus()).toBe('online');
    expect(service.dbStatus()).toBe('online');
  });

  it('should set apiStatus to offline when API returns non-UP api status', () => {
    httpMock.expectOne(environment.apiUrl + '/api/status').flush({
      api: 'DOWN', database: 'UP', timestamp: '2026-01-01T00:00:00Z',
    });
    expect(service.apiStatus()).toBe('offline');
    expect(service.dbStatus()).toBe('online');
  });

  it('should set dbStatus to offline when database is not UP', () => {
    httpMock.expectOne(environment.apiUrl + '/api/status').flush({
      api: 'UP', database: 'DOWN', timestamp: '2026-01-01T00:00:00Z',
    });
    expect(service.apiStatus()).toBe('online');
    expect(service.dbStatus()).toBe('offline');
  });

  it('should set both statuses to offline on HTTP error', () => {
    httpMock.expectOne(environment.apiUrl + '/api/status').error(new ProgressEvent('Network error'));
    expect(service.apiStatus()).toBe('offline');
    expect(service.dbStatus()).toBe('offline');
  });

  it('should have techStack data', () => {
    httpMock.expectOne(environment.apiUrl + '/api/status').flush({
      api: 'UP', database: 'UP', timestamp: '',
    });
    expect(service.techStack.length).toBeGreaterThan(0);
    expect(service.techStack.some((t) => t.name === 'Angular')).toBe(true);
    expect(service.techStack.some((t) => t.name === 'Spring Boot')).toBe(true);
    expect(service.techStack.some((t) => t.name === 'PostgreSQL')).toBe(true);
  });

  it('checkStatus should make a new GET request to /api/status', () => {
    httpMock.expectOne(environment.apiUrl + '/api/status').flush({
      api: 'UP', database: 'UP', timestamp: '',
    });

    service.checkStatus();

    const req = httpMock.expectOne(environment.apiUrl + '/api/status');
    expect(req.request.method).toBe('GET');
    req.flush({ api: 'DOWN', database: 'DOWN', timestamp: '' });
    expect(service.apiStatus()).toBe('offline');
  });

  it('should reset apiStatus to checking on checkStatus call', () => {
    httpMock.expectOne(environment.apiUrl + '/api/status').flush({
      api: 'UP', database: 'UP', timestamp: '',
    });
    expect(service.apiStatus()).toBe('online');

    service.checkStatus();
    expect(service.apiStatus()).toBe('checking');

    httpMock.expectOne(environment.apiUrl + '/api/status').flush({
      api: 'UP', database: 'UP', timestamp: '',
    });
    expect(service.apiStatus()).toBe('online');
  });
});

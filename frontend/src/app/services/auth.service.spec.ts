import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { AuthService } from './auth.service';
import { environment } from '../../environments/environment';

describe('AuthService', () => {
  let service: AuthService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    localStorage.clear();
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()],
    });
    service = TestBed.inject(AuthService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
    localStorage.clear();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should start with no user logged in', () => {
    expect(service.currentUser()).toBeNull();
    expect(service.isLoggedIn()).toBe(false);
    expect(service.username()).toBe('guest');
  });

  it('login should make HTTP POST and set currentUser with token', () => {
    const mockResponse = {
      id: 1,
      username: 'test',
      email: 'test@example.com',
      createdAt: '2026-01-01T00:00:00',
      token: 'eyJhbGciOiJIUzM4NCJ9.test-token',
    };

    let completed = false;
    service.login('test@example.com', 'password').subscribe(() => {
      completed = true;
    });

    const req = httpMock.expectOne(environment.apiUrl + '/api/users/login');
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual({ email: 'test@example.com', password: 'password' });
    req.flush(mockResponse);

    expect(completed).toBe(true);
    expect(service.isLoggedIn()).toBe(true);
    expect(service.username()).toBe('test');
    expect(service.currentUser()?.email).toBe('test@example.com');
    expect(service.currentUser()?.token).toBe('eyJhbGciOiJIUzM4NCJ9.test-token');
    expect(localStorage.getItem('jwt_token')).toBe('eyJhbGciOiJIUzM4NCJ9.test-token');
  });

  it('logout should clear currentUser and localStorage', () => {
    const mockResponse = { id: 1, username: 'test', email: 'test@example.com', createdAt: '', token: 't' };
    service.login('test@example.com', 'password').subscribe();
    httpMock.expectOne(environment.apiUrl + '/api/users/login').flush(mockResponse);

    expect(service.isLoggedIn()).toBe(true);
    service.logout();

    expect(service.currentUser()).toBeNull();
    expect(service.isLoggedIn()).toBe(false);
    expect(service.username()).toBe('guest');
    expect(localStorage.getItem('jwt_token')).toBeNull();
    expect(localStorage.getItem('user')).toBeNull();
  });

  it('register should make HTTP POST and set currentUser on success', () => {
    const mockResponse = { id: 42, username: 'newuser', email: 'new@test.com', createdAt: '2026-01-01' };

    service.register('newuser', 'new@test.com', 'password123').subscribe((res) => {
      expect(res).toEqual(mockResponse);
    });

    const req = httpMock.expectOne(environment.apiUrl + '/api/users');
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual({ username: 'newuser', email: 'new@test.com', password: 'password123' });
    req.flush(mockResponse);

    expect(service.isLoggedIn()).toBe(true);
    expect(service.username()).toBe('newuser');
    expect(service.currentUser()?.id).toBe(42);
  });

  it('should restore session from localStorage', () => {
    localStorage.setItem('jwt_token', 'restored-token');
    localStorage.setItem('user', JSON.stringify({ id: 7, username: 'saveduser', email: 'saved@test.com' }));

    TestBed.resetTestingModule();
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()],
    });
    service = TestBed.inject(AuthService);
    httpMock = TestBed.inject(HttpTestingController);

    expect(service.isLoggedIn()).toBe(true);
    expect(service.username()).toBe('saveduser');
    expect(service.currentUser()?.id).toBe(7);
    expect(service.currentUser()?.token).toBe('restored-token');
  });

  it('isLoggedIn should react to state changes', () => {
    expect(service.isLoggedIn()).toBe(false);

    const mockResponse = { id: 1, username: 'x', email: 'x@y.com', createdAt: '', token: 't' };
    service.login('x@y.com', 'pwd').subscribe();
    httpMock.expectOne(environment.apiUrl + '/api/users/login').flush(mockResponse);

    expect(service.isLoggedIn()).toBe(true);
    service.logout();
    expect(service.isLoggedIn()).toBe(false);
  });
});

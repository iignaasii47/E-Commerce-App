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

  it('login should store tokens and set currentUser', () => {
    const mockResponse = {
      id: 1,
      username: 'test',
      email: 'test@example.com',
      createdAt: '2026-01-01T00:00:00',
      accessToken: 'access-jwt-token',
      refreshToken: 'refresh-jwt-token',
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
    expect(localStorage.getItem('access_token')).toBe('access-jwt-token');
    expect(localStorage.getItem('refresh_token')).toBe('refresh-jwt-token');
  });

  it('logout should clear currentUser and localStorage', () => {
    const mockResponse = {
      id: 1, username: 'test', email: 'test@example.com', createdAt: '',
      accessToken: 'at', refreshToken: 'rt',
    };
    service.login('test@example.com', 'password').subscribe();
    httpMock.expectOne(environment.apiUrl + '/api/users/login').flush(mockResponse);

    expect(service.isLoggedIn()).toBe(true);
    service.logout();

    const logoutReq = httpMock.expectOne(environment.apiUrl + '/api/auth/logout');
    expect(logoutReq.request.method).toBe('POST');
    expect(logoutReq.request.body).toEqual({ refreshToken: 'rt' });
    logoutReq.flush({});

    expect(service.currentUser()).toBeNull();
    expect(service.isLoggedIn()).toBe(false);
    expect(service.username()).toBe('guest');
    expect(localStorage.getItem('access_token')).toBeNull();
    expect(localStorage.getItem('refresh_token')).toBeNull();
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
    const jwt = 'header.eyJleHAiOjk5OTk5OTk5OTl9.signature';
    localStorage.setItem('access_token', jwt);
    localStorage.setItem(
      'user',
      JSON.stringify({ id: 7, username: 'saveduser', email: 'saved@test.com' }),
    );

    TestBed.resetTestingModule();
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()],
    });
    service = TestBed.inject(AuthService);
    httpMock = TestBed.inject(HttpTestingController);

    expect(service.isLoggedIn()).toBe(true);
    expect(service.username()).toBe('saveduser');
    expect(service.currentUser()?.id).toBe(7);
  });

  it('isLoggedIn should react to state changes', () => {
    expect(service.isLoggedIn()).toBe(false);

    const mockResponse = {
      id: 1, username: 'x', email: 'x@y.com', createdAt: '',
      accessToken: 'at', refreshToken: 'rt',
    };
    service.login('x@y.com', 'pwd').subscribe();
    httpMock.expectOne(environment.apiUrl + '/api/users/login').flush(mockResponse);

    expect(service.isLoggedIn()).toBe(true);
    service.logout();
    httpMock.expectOne(environment.apiUrl + '/api/auth/logout').flush({});
    expect(service.isLoggedIn()).toBe(false);
  });

  it('refreshToken should call refresh endpoint and store new tokens', () => {
    localStorage.setItem('access_token', 'old-access');
    localStorage.setItem('refresh_token', 'old-refresh');

    const mockResponse = {
      id: 2,
      username: 'refreshed',
      email: 'refreshed@test.com',
      createdAt: '2026-01-01T00:00:00',
      accessToken: 'new-access',
      refreshToken: 'new-refresh',
    };

    let completed = false;
    service.refreshToken().subscribe(() => {
      completed = true;
    });

    const req = httpMock.expectOne(environment.apiUrl + '/api/auth/refresh');
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual({ refreshToken: 'old-refresh' });
    req.flush(mockResponse);

    expect(completed).toBe(true);
    expect(localStorage.getItem('access_token')).toBe('new-access');
    expect(localStorage.getItem('refresh_token')).toBe('new-refresh');
    expect(service.currentUser()?.username).toBe('refreshed');
  });
});

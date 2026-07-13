import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { AuthService } from './auth.service';
import { environment } from '../../environments/environment';

describe('AuthService', () => {
  let service: AuthService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()],
    });
    service = TestBed.inject(AuthService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should start with no user logged in', () => {
    expect(service.currentUser()).toBeNull();
    expect(service.isLoggedIn()).toBe(false);
    expect(service.username()).toBe('guest');
  });

  it('login should set currentUser and return true', () => {
    const result = service.login('test@example.com', 'password');
    expect(result).toBe(true);
    expect(service.isLoggedIn()).toBe(true);
    expect(service.username()).toBe('test');
    expect(service.currentUser()?.email).toBe('test@example.com');
  });

  it('login should set a token', () => {
    service.login('a@b.com', 'pwd');
    expect(service.currentUser()?.token).toContain('mock-jwt-token-');
  });

  it('logout should clear currentUser', () => {
    service.login('test@example.com', 'password');
    expect(service.isLoggedIn()).toBe(true);
    service.logout();
    expect(service.currentUser()).toBeNull();
    expect(service.isLoggedIn()).toBe(false);
    expect(service.username()).toBe('guest');
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

  it('username should derive from currentUser email', () => {
    service.login('john.doe@example.com', 'pwd');
    expect(service.username()).toBe('john.doe');
  });

  it('isLoggedIn should react to state changes', () => {
    expect(service.isLoggedIn()).toBe(false);
    service.login('x@y.com', 'pwd');
    expect(service.isLoggedIn()).toBe(true);
    service.logout();
    expect(service.isLoggedIn()).toBe(false);
  });
});

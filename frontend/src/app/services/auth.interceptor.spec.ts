import { TestBed } from '@angular/core/testing';
import { provideRouter, Router } from '@angular/router';
import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { HttpRequest, HttpErrorResponse } from '@angular/common/http';
import { of, throwError, Observable } from 'rxjs';
import { authInterceptor } from './auth.interceptor';
import { AuthService } from './auth.service';

describe('authInterceptor', () => {
  let authService: AuthService;
  let router: Router;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    localStorage.clear();
    TestBed.configureTestingModule({
      providers: [provideRouter([]), provideHttpClient(), provideHttpClientTesting()],
    });
    authService = TestBed.inject(AuthService);
    router = TestBed.inject(Router);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  function runInterceptor(
    req: HttpRequest<unknown>,
    next: (req: HttpRequest<unknown>) => Observable<unknown>,
  ) {
    return TestBed.runInInjectionContext(() => authInterceptor(req, next));
  }

  it('should add Authorization header when token exists', () => {
    localStorage.setItem('access_token', 'my-token');
    const req = new HttpRequest('GET', '/api/test');
    const nextSpy = vi.fn().mockReturnValue(of({ body: {} }));

    runInterceptor(req, nextSpy);

    expect(nextSpy).toHaveBeenCalledOnce();
    const clonedReq = nextSpy.mock.calls[0][0] as HttpRequest<unknown>;
    expect(clonedReq.headers.get('Authorization')).toBe('Bearer my-token');
  });

  it('should not add Authorization header when no token', () => {
    localStorage.removeItem('access_token');
    const req = new HttpRequest('GET', '/api/test');
    const nextSpy = vi.fn().mockReturnValue(of({ body: {} }));

    runInterceptor(req, nextSpy);

    expect(nextSpy).toHaveBeenCalledOnce();
    const clonedReq = nextSpy.mock.calls[0][0] as HttpRequest<unknown>;
    expect(clonedReq.headers.get('Authorization')).toBeNull();
  });

  it('should pass through non-401 errors unchanged', () => {
    const req = new HttpRequest('GET', '/api/test');
    const error = new HttpErrorResponse({ status: 500, statusText: 'Server Error' });
    const nextSpy = vi.fn().mockReturnValue(throwError(() => error));

    let caughtError: unknown;
    runInterceptor(req, nextSpy).subscribe({
      error: (e) => { caughtError = e; },
    });

    expect(caughtError).toBe(error);
  });

  it('should pass through 401 on /api/auth/refresh', () => {
    const req = new HttpRequest('POST', '/api/auth/refresh', {});
    const error = new HttpErrorResponse({ status: 401 });
    const nextSpy = vi.fn().mockReturnValue(throwError(() => error));

    let caughtError: unknown;
    runInterceptor(req, nextSpy).subscribe({
      error: (e) => { caughtError = e; },
    });

    expect(caughtError).toBe(error);
  });

  it('should pass through 401 on /api/auth/logout', () => {
    const req = new HttpRequest('POST', '/api/auth/logout', {});
    const error = new HttpErrorResponse({ status: 401 });
    const nextSpy = vi.fn().mockReturnValue(throwError(() => error));

    let caughtError: unknown;
    runInterceptor(req, nextSpy).subscribe({
      error: (e) => { caughtError = e; },
    });

    expect(caughtError).toBe(error);
  });

  it('should attempt token refresh on 401 and retry with new token', () => {
    localStorage.setItem('access_token', 'old-token');
    localStorage.setItem('refresh_token', 'refresh-token');

    const req = new HttpRequest('GET', '/api/data');
    const error401 = new HttpErrorResponse({ status: 401 });

    let callCount = 0;
    const nextSpy = vi.fn().mockImplementation((r: HttpRequest<unknown>) => {
      callCount++;
      if (callCount === 1) {
        return throwError(() => error401);
      }
      return of({ body: { data: 'retried' } });
    });

    let result: unknown;
    runInterceptor(req, nextSpy).subscribe((res) => {
      result = res;
    });

    const refreshReq = httpMock.expectOne(
      (r) => r.url.includes('/api/auth/refresh'),
    );
    expect(refreshReq.request.method).toBe('POST');
    refreshReq.flush({
      id: 1, username: 'user', email: 'user@test.com', createdAt: '',
      accessToken: 'new-token', refreshToken: 'new-refresh',
    });

    expect(result).toEqual({ body: { data: 'retried' } });
    const retriedReq = nextSpy.mock.calls[1][0] as HttpRequest<unknown>;
    expect(retriedReq.headers.get('Authorization')).toBe('Bearer new-token');
  });

  it('should call logout and navigate to /login on refresh failure', () => {
    localStorage.setItem('access_token', 'expired-token');
    localStorage.setItem('refresh_token', 'refresh-token');

    const navigateSpy = vi.spyOn(router, 'navigate');

    const req = new HttpRequest('GET', '/api/data');
    const error401 = new HttpErrorResponse({ status: 401 });

    const nextSpy = vi.fn().mockReturnValue(throwError(() => error401));

    let caughtError: unknown;
    runInterceptor(req, nextSpy).subscribe({
      error: (e) => { caughtError = e; },
    });

    const refreshReq = httpMock.expectOne(
      (r) => r.url.includes('/api/auth/refresh'),
    );
    refreshReq.flush(
      { message: 'Invalid refresh token' },
      { status: 401, statusText: 'Unauthorized' },
    );

    expect(caughtError).toBeDefined();
    expect(authService.isLoggedIn()).toBe(false);
    expect(navigateSpy).toHaveBeenCalledWith(['/login']);
  });

  it('should queue concurrent 401 requests while refreshing', () => {
    localStorage.setItem('access_token', 'old-token');
    localStorage.setItem('refresh_token', 'refresh-token');

    const req1 = new HttpRequest('GET', '/api/data1');
    const req2 = new HttpRequest('GET', '/api/data2');
    const error401 = new HttpErrorResponse({ status: 401 });

    let callCount = 0;
    const nextSpy = vi.fn().mockImplementation((r: HttpRequest<unknown>) => {
      callCount++;
      if (callCount <= 2) {
        return throwError(() => error401);
      }
      return of({ body: { url: r.url } });
    });

    const results: unknown[] = [];
    runInterceptor(req1, nextSpy).subscribe((res) => results.push(res));
    runInterceptor(req2, nextSpy).subscribe((res) => results.push(res));

    const refreshReq = httpMock.expectOne(
      (r) => r.url.includes('/api/auth/refresh'),
    );
    refreshReq.flush({
      id: 1, username: 'user', email: 'user@test.com', createdAt: '',
      accessToken: 'queued-token', refreshToken: 'queued-refresh',
    });

    expect(results.length).toBe(2);
  });
});

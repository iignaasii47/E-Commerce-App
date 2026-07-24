import { TestBed } from '@angular/core/testing';
import { provideRouter, Router } from '@angular/router';
import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { authGuard } from './auth.guard';
import { AuthService } from './auth.service';

describe('authGuard', () => {
  let authService: AuthService;
  let router: Router;
  let httpMock: HttpTestingController;

  function runGuard(): boolean | ReturnType<Router['parseUrl']> {
    return TestBed.runInInjectionContext(() => authGuard());
  }

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

  it('should allow access when user is logged in', () => {
    (authService as any).currentUser.set({ id: 1, username: 'test', email: 'test@test.com' });

    const result = runGuard();
    expect(result).toBe(true);
  });

  it('should redirect to /login when user is not logged in', () => {
    authService.logout();

    const result = runGuard();
    expect(result).not.toBe(true);
    expect(result.toString()).toContain('/login');
  });

  it('should return UrlTree for /login when not authenticated', () => {
    authService.logout();

    const result = runGuard();
    expect(result).toEqual(router.parseUrl('/login'));
  });

  it('should allow access after login and deny after logout', () => {
    (authService as any).currentUser.set({ id: 1, username: 'test', email: 'test@test.com' });
    expect(runGuard()).toBe(true);

    authService.logout();
    const result = runGuard();
    expect(result).not.toBe(true);
    expect(result.toString()).toContain('/login');
  });
});

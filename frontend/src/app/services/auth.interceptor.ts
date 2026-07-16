import {
  HttpInterceptorFn,
  HttpRequest,
  HttpErrorResponse,
} from '@angular/common/http';
import { inject } from '@angular/core';
import { Router } from '@angular/router';
import { BehaviorSubject, catchError, filter, switchMap, take, throwError } from 'rxjs';
import { AuthService } from './auth.service';

let isRefreshing = false;
const refreshTokenSubject = new BehaviorSubject<string | null>(null);

export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const auth = inject(AuthService);
  const router = inject(Router);

  const token = auth.getAccessToken();
  const cloned = token
    ? req.clone({ headers: req.headers.set('Authorization', `Bearer ${token}`) })
    : req;

  return next(cloned).pipe(
    catchError((err: HttpErrorResponse) => {
      if (
        err.status !== 401 ||
        req.url.includes('/api/auth/refresh') ||
        req.url.includes('/api/auth/logout')
      ) {
        return throwError(() => err);
      }

      if (!isRefreshing) {
        isRefreshing = true;
        refreshTokenSubject.next(null);

        return auth.refreshToken().pipe(
          switchMap(() => {
            isRefreshing = false;
            const newToken = auth.getAccessToken()!;
            refreshTokenSubject.next(newToken);
            return next(addToken(req, newToken));
          }),
          catchError((refreshErr) => {
            isRefreshing = false;
            refreshTokenSubject.next(null);
            auth.logout();
            router.navigate(['/login']);
            return throwError(() => refreshErr);
          }),
        );
      }

      return refreshTokenSubject.pipe(
        filter((t): t is string => t !== null),
        take(1),
        switchMap((newToken) => next(addToken(req, newToken))),
      );
    }),
  );
};

function addToken(req: HttpRequest<unknown>, token: string): HttpRequest<unknown> {
  return req.clone({ headers: req.headers.set('Authorization', `Bearer ${token}`) });
}

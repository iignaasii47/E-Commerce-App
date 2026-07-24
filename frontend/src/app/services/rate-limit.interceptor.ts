import { HttpInterceptorFn, HttpErrorResponse } from '@angular/common/http';
import { inject } from '@angular/core';
import { catchError, throwError } from 'rxjs';
import { NotificationService } from './notification.service';

export const rateLimitInterceptor: HttpInterceptorFn = (req, next) => {
  const notification = inject(NotificationService);

  return next(req).pipe(
    catchError((err: HttpErrorResponse) => {
      if (err.status === 429) {
        const message =
          err.error?.error ?? 'Too many requests. Please try again later.';
        notification.error(message);
      }
      return throwError(() => err);
    }),
  );
};

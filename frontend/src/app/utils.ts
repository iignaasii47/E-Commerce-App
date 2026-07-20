import { HttpErrorResponse } from '@angular/common/http';

export function getStars(rating: number): number[] {
  return Array.from({ length: Math.floor(rating) }, (_, i) => i);
}

export function handleHttpError(err: HttpErrorResponse, fallback: string): string {
  if (err.status === 0) {
    return 'cannot connect to server — is the backend running?';
  }
  return err.error?.message ?? fallback;
}

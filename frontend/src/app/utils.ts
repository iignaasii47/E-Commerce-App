import { HttpErrorResponse } from '@angular/common/http';

export function getStars(rating: number): number[] {
  return Array.from({ length: Math.floor(rating) }, (_, i) => i);
}

export function handleHttpError(err: HttpErrorResponse, fallback: string): string {
  if (err.status === 0) {
    return 'cannot connect to server — is the backend running?';
  }

  if (err.error && typeof err.error === 'object' && !('message' in err.error)) {
    const messages: string[] = [];
    for (const key of Object.keys(err.error)) {
      if (Array.isArray(err.error[key])) {
        messages.push(...err.error[key]);
      }
    }
    if (messages.length > 0) {
      return messages.join('; ');
    }
  }

  if (typeof err.error?.error === 'string') {
    return err.error.error;
  }

  return err.error?.message ?? fallback;
}

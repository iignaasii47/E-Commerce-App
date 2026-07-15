import { Injectable, inject, signal, computed } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, tap } from 'rxjs';
import { User } from '../models';
import { environment } from '../../environments/environment';

interface RegisterRequest {
  username: string;
  email: string;
  password: string;
}

interface RegisterResponse {
  id: number;
  username: string;
  email: string;
  createdAt: string;
}

interface LoginRequest {
  email: string;
  password: string;
}

interface LoginResponse {
  id: number;
  username: string;
  email: string;
  createdAt: string;
  token: string;
}

@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly http = inject(HttpClient);

  private readonly apiUrl = environment.apiUrl + '/api/users';

  readonly currentUser = signal<User | null>(null);

  readonly isLoggedIn = computed(() => this.currentUser() !== null);

  readonly username = computed(() => this.currentUser()?.username ?? 'guest');

  constructor() {
    this.restoreSession();
  }

  register(username: string, email: string, password: string): Observable<RegisterResponse> {
    const body: RegisterRequest = { username, email, password };
    return this.http.post<RegisterResponse>(this.apiUrl, body).pipe(
      tap((res) => {
        this.currentUser.set({
          id: res.id,
          email: res.email,
          username: res.username,
        });
      }),
    );
  }

  login(email: string, password: string): Observable<LoginResponse> {
    const body: LoginRequest = { email, password };
    return this.http.post<LoginResponse>(`${this.apiUrl}/login`, body).pipe(
      tap((res) => {
        localStorage.setItem('jwt_token', res.token);
        localStorage.setItem('user', JSON.stringify({ id: res.id, username: res.username, email: res.email }));
        this.currentUser.set({
          id: res.id,
          username: res.username,
          email: res.email,
          token: res.token,
        });
      }),
    );
  }

  logout(): void {
    localStorage.removeItem('jwt_token');
    localStorage.removeItem('user');
    this.currentUser.set(null);
  }

  private decodeToken(token: string): { exp: number } | null {
    try {
      const payload = token.split('.')[1].replaceAll('-', '+').replaceAll('_', '/');
      return JSON.parse(atob(payload));
    } catch {
      return null;
    }
  }

  private isTokenExpired(): boolean {
    const token = localStorage.getItem('jwt_token');
    if (!token) return true;
    const decoded = this.decodeToken(token);
    if (!decoded?.exp) return false;
    return Date.now() >= decoded.exp * 1000;
  }

  private restoreSession(): void {
    if (this.isTokenExpired()) {
      this.logout();
      return;
    }
    const token = localStorage.getItem('jwt_token');
    const raw = localStorage.getItem('user');
    if (token && raw) {
      try {
        const { id, username, email } = JSON.parse(raw);
        this.currentUser.set({ id, username, email, token });
      } catch {
        localStorage.removeItem('jwt_token');
        localStorage.removeItem('user');
      }
    }
  }
}

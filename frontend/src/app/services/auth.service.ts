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
  accessToken: string;
  refreshToken: string;
}

interface RefreshRequest {
  refreshToken: string;
}

interface LogoutRequest {
  refreshToken: string;
}

@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly http = inject(HttpClient);

  private readonly apiUrl = environment.apiUrl + '/api/users';
  private readonly authUrl = environment.apiUrl + '/api/auth';

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
        this.storeTokens(res.accessToken, res.refreshToken);
        localStorage.setItem(
          'user',
          JSON.stringify({ id: res.id, username: res.username, email: res.email }),
        );
        this.currentUser.set({
          id: res.id,
          username: res.username,
          email: res.email,
        });
      }),
    );
  }

  refreshToken(): Observable<LoginResponse> {
    const refreshToken = localStorage.getItem('refresh_token');
    if (!refreshToken) {
      throw new Error('No refresh token available');
    }
    const body: RefreshRequest = { refreshToken };
    return this.http.post<LoginResponse>(`${this.authUrl}/refresh`, body).pipe(
      tap((res) => {
        this.storeTokens(res.accessToken, res.refreshToken);
        localStorage.setItem(
          'user',
          JSON.stringify({ id: res.id, username: res.username, email: res.email }),
        );
        this.currentUser.set({
          id: res.id,
          username: res.username,
          email: res.email,
        });
      }),
    );
  }

  logout(): void {
    const refreshToken = localStorage.getItem('refresh_token');
    if (refreshToken) {
      const body: LogoutRequest = { refreshToken };
      this.http.post(`${this.authUrl}/logout`, body).subscribe();
    }
    localStorage.removeItem('access_token');
    localStorage.removeItem('refresh_token');
    localStorage.removeItem('user');
    this.currentUser.set(null);
  }

  getAccessToken(): string | null {
    return localStorage.getItem('access_token');
  }

  private storeTokens(accessToken: string, refreshToken: string): void {
    localStorage.setItem('access_token', accessToken);
    localStorage.setItem('refresh_token', refreshToken);
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
    const token = localStorage.getItem('access_token');
    if (!token) return true;
    const decoded = this.decodeToken(token);
    if (!decoded?.exp) return true;
    return Date.now() >= decoded.exp * 1000;
  }

  private restoreSession(): void {
    if (this.isTokenExpired()) {
      this.logout();
      return;
    }
    const token = localStorage.getItem('access_token');
    const raw = localStorage.getItem('user');
    if (token && raw) {
      try {
        const { id, username, email } = JSON.parse(raw);
        this.currentUser.set({ id, username, email });
      } catch {
        this.logout();
      }
    }
  }
}

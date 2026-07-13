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

@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly http = inject(HttpClient);

  private readonly apiUrl = environment.apiUrl + '/api/users';

  readonly currentUser = signal<User | null>(null);

  readonly isLoggedIn = computed(() => this.currentUser() !== null);

  readonly username = computed(() => this.currentUser()?.username ?? 'guest');

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

  login(email: string, _password: string): boolean {
    const mockUser: User = {
      id: 1,
      email,
      username: email.split('@')[0],
      token: 'mock-jwt-token-' + Math.random().toString(36).slice(2),
    };
    this.currentUser.set(mockUser);
    return true;
  }

  logout(): void {
    this.currentUser.set(null);
  }
}

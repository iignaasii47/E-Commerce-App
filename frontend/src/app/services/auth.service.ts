import { Injectable, signal, computed } from '@angular/core';
import { User } from '../models';

@Injectable({ providedIn: 'root' })
export class AuthService {
  readonly currentUser = signal<User | null>(null);

  readonly isLoggedIn = computed(() => this.currentUser() !== null);

  readonly username = computed(() => this.currentUser()?.username ?? 'guest');

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

  register(username: string, email: string, _password: string): boolean {
    const mockUser: User = {
      id: Date.now(),
      email,
      username,
      token: 'mock-jwt-token-' + Math.random().toString(36).slice(2),
    };
    this.currentUser.set(mockUser);
    return true;
  }

  logout(): void {
    this.currentUser.set(null);
  }
}

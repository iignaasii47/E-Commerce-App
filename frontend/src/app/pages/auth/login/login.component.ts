import { Component, inject, signal } from '@angular/core';
import { Router, RouterLink } from '@angular/router';
import { HttpErrorResponse } from '@angular/common/http';
import { AuthService, NotificationService } from '../../../services';
import { TerminalInputComponent } from '../../../components/shared/terminal-input/terminal-input.component';
import { TerminalButtonComponent } from '../../../components/shared/terminal-button/terminal-button.component';
import { handleHttpError } from '../../../utils';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [RouterLink, TerminalInputComponent, TerminalButtonComponent],
  template: `
    <div class="page-container auth-page">
      <div class="auth-box">
        <h1 class="page-title">su - login</h1>
        <p class="page-subtitle">authenticate to your account</p>

        @if (errorMessage()) {
          <div class="error-banner">
            <span class="error-icon">[!!]</span> {{ errorMessage() }}
          </div>
        }

        <form class="auth-form" (submit)="onSubmit($event)">
          <div class="form-fields">
            <app-terminal-input
              id="email"
              label="email"
              placeholder="user&#64;terminal.com"
              [value]="email()"
              (valueChange)="email.set($event)" />
            <app-terminal-input
              id="password"
              type="password"
              label="pass"
              placeholder="••••••••"
              [value]="password()"
              (valueChange)="password.set($event)" />
          </div>

          <div class="auth-actions">
            <app-terminal-button variant="purple" type="button" [disabled]="loading()" (click)="onLoginAsGuest()">
              login as guest
            </app-terminal-button>
            <app-terminal-button variant="primary" type="submit" [disabled]="loading()">
              {{ loading() ? 'authenticating...' : 'login' }}
            </app-terminal-button>
          </div>
        </form>

        <div class="auth-footer">
          <p>don't have an account? <a routerLink="/register">register</a></p>
        </div>
      </div>
    </div>
  `,
  styles: `
    .auth-actions {
      display: flex;
      justify-content: space-between;
    }
  `,
})
export class LoginComponent {
  private readonly auth = inject(AuthService);
  private readonly router = inject(Router);
  private readonly notifications = inject(NotificationService);

  readonly email = signal('');
  readonly password = signal('');
  readonly loading = signal(false);
  readonly errorMessage = signal('');

  onLoginAsGuest(): void {
    this.email.set('guest@webshop.tui');
    this.password.set('guest');
    this.onSubmit(new Event('submit'));
  }

  onSubmit(event: Event): void {
    event.preventDefault();
    this.errorMessage.set('');

    if (!this.email() || !this.password()) {
      this.errorMessage.set('email and password required');
      return;
    }

    this.loading.set(true);

    this.auth.login(this.email(), this.password()).subscribe({
      next: () => {
        this.loading.set(false);
        this.notifications.success(`welcome back, ${this.auth.username()}`);
        this.router.navigate(['/']);
      },
      error: (err: HttpErrorResponse) => {
        this.loading.set(false);

        if (err.status === 401) {
          this.errorMessage.set(handleHttpError(err, 'invalid credentials'));
        } else {
          this.errorMessage.set(handleHttpError(err, 'login failed — please try again'));
        }
      },
    });
  }
}

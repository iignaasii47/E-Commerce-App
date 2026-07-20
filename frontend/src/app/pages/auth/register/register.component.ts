import { Component, inject, signal } from '@angular/core';
import { Router, RouterLink } from '@angular/router';
import { HttpErrorResponse } from '@angular/common/http';
import { AuthService, NotificationService } from '../../../services';
import { TerminalInputComponent } from '../../../components/shared/terminal-input/terminal-input.component';
import { TerminalButtonComponent } from '../../../components/shared/terminal-button/terminal-button.component';
import { handleHttpError } from '../../../utils';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [RouterLink, TerminalInputComponent, TerminalButtonComponent],
  template: `
    <div class="page-container auth-page">
      <div class="auth-box">
        <h1 class="page-title">useradd --register</h1>
        <p class="page-subtitle">create a new account</p>

        @if (errorMessage()) {
          <div class="error-banner">
            <span class="error-icon">[!!]</span> {{ errorMessage() }}
          </div>
        }

        <form class="auth-form" (submit)="onSubmit($event)">
          <div class="form-fields">
            <app-terminal-input
              label="user"
              placeholder="username"
              [value]="username()"
              (valueChange)="username.set($event)" />
            <app-terminal-input
              label="email"
              placeholder="user&#64;terminal.com"
              [value]="email()"
              (valueChange)="email.set($event)" />
            <app-terminal-input
              type="password"
              label="pass"
              placeholder="min 8 characters"
              [value]="password()"
              (valueChange)="password.set($event)" />
            <app-terminal-input
              type="password"
              label="confirm"
              placeholder="repeat password"
              [value]="confirmPassword()"
              (valueChange)="confirmPassword.set($event)" />
          </div>

          <div class="auth-actions">
            <app-terminal-button variant="primary" type="submit" [disabled]="loading()">
              {{ loading() ? 'creating...' : 'register' }}
            </app-terminal-button>
          </div>
        </form>

        <div class="auth-footer">
          <p>already have an account? <a routerLink="/login">login</a></p>
        </div>
      </div>
    </div>
  `,
  styles: `
    .auth-actions {
      display: flex;
      justify-content: flex-end;
    }
  `,
})
export class RegisterComponent {
  private readonly auth = inject(AuthService);
  private readonly router = inject(Router);
  private readonly notifications = inject(NotificationService);

  readonly username = signal('');
  readonly email = signal('');
  readonly password = signal('');
  readonly confirmPassword = signal('');
  readonly loading = signal(false);
  readonly errorMessage = signal('');

  onSubmit(event: Event): void {
    event.preventDefault();
    this.errorMessage.set('');

    if (!this.username() || !this.email() || !this.password()) {
      this.errorMessage.set('all fields are required');
      return;
    }

    if (this.password().length < 8) {
      this.errorMessage.set('password must be at least 8 characters');
      return;
    }

    if (this.password() !== this.confirmPassword()) {
      this.errorMessage.set('passwords do not match');
      return;
    }

    this.loading.set(true);

    this.auth.register(this.username(), this.email(), this.password()).subscribe({
      next: () => {
        this.loading.set(false);
        this.notifications.success(`account created. welcome, ${this.username()}`);
        this.router.navigate(['/']);
      },
      error: (err: HttpErrorResponse) => {
        this.loading.set(false);

        if (err.status === 409) {
          this.errorMessage.set(handleHttpError(err, 'username or email already taken'));
        } else if (err.status === 400) {
          const fields = err.error;
          if (typeof fields === 'object' && fields !== null) {
            const msgs = Object.values(fields).flat().join('; ');
            this.errorMessage.set(String(msgs));
          } else {
            this.errorMessage.set('validation failed — check your input');
          }
        } else {
          this.errorMessage.set(handleHttpError(err, 'unexpected error — please try again'));
        }
      },
    });
  }
}

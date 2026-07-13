import { Component, inject, signal } from '@angular/core';
import { Router, RouterLink } from '@angular/router';
import { AuthService, NotificationService } from '../../../services';
import { TerminalInputComponent } from '../../../components/shared/terminal-input/terminal-input.component';
import { TerminalButtonComponent } from '../../../components/shared/terminal-button/terminal-button.component';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [RouterLink, TerminalInputComponent, TerminalButtonComponent],
  template: `
    <div class="page-container auth-page">
      <div class="auth-box">
        <h1 class="page-title">useradd --register</h1>
        <p class="page-subtitle">create a new account</p>

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
              placeholder="••••••••"
              [value]="password()"
              (valueChange)="password.set($event)" />
            <app-terminal-input
              type="password"
              label="confirm"
              placeholder="••••••••"
              [value]="confirmPassword()"
              (valueChange)="confirmPassword.set($event)" />
          </div>

          <div class="auth-actions">
            <app-terminal-button variant="primary" type="submit">
              register
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
    .auth-page {
      display: flex;
      justify-content: center;
      align-items: center;
    }

    .auth-box {
      width: 100%;
      max-width: 380px;
      padding: 24px;
      border: 1px solid var(--border);
      background: var(--bg-secondary);
    }

    .auth-form {
      margin-top: 16px;
    }

    .form-fields {
      display: flex;
      flex-direction: column;
      gap: 8px;
      margin-bottom: 16px;
    }

    .auth-actions {
      display: flex;
      justify-content: flex-end;
    }

    .auth-footer {
      margin-top: 16px;
      padding-top: 12px;
      border-top: 1px solid var(--border);
      font-size: 11px;
      color: var(--text-muted);

      a {
        color: var(--accent-cyan);

        &:hover {
          color: var(--accent-green);
        }
      }
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

  onSubmit(event: Event): void {
    event.preventDefault();
    if (!this.username() || !this.email() || !this.password()) {
      this.notifications.error('all fields are required');
      return;
    }
    if (this.password() !== this.confirmPassword()) {
      this.notifications.error('passwords do not match');
      return;
    }
    const success = this.auth.register(this.username(), this.email(), this.password());
    if (success) {
      this.notifications.success(`account created. welcome, ${this.username()}`);
      this.router.navigate(['/']);
    }
  }
}

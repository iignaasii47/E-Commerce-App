import { Component, inject, signal } from '@angular/core';
import { Router, RouterLink } from '@angular/router';
import { AuthService, NotificationService } from '../../../services';
import { TerminalInputComponent } from '../../../components/shared/terminal-input/terminal-input.component';
import { TerminalButtonComponent } from '../../../components/shared/terminal-button/terminal-button.component';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [RouterLink, TerminalInputComponent, TerminalButtonComponent],
  template: `
    <div class="page-container auth-page">
      <div class="auth-box">
        <h1 class="page-title">su - login</h1>
        <p class="page-subtitle">authenticate to your account</p>

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
            <app-terminal-button variant="primary" type="submit">
              login
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
export class LoginComponent {
  private readonly auth = inject(AuthService);
  private readonly router = inject(Router);
  private readonly notifications = inject(NotificationService);

  readonly email = signal('');
  readonly password = signal('');

  onSubmit(event: Event): void {
    event.preventDefault();
    if (!this.email() || !this.password()) {
      this.notifications.error('email and password required');
      return;
    }
    const success = this.auth.login(this.email(), this.password());
    if (success) {
      this.notifications.success(`welcome back, ${this.auth.username()}`);
      this.router.navigate(['/']);
    }
  }
}

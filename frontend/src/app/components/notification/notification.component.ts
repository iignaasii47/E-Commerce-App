import { Component, inject } from '@angular/core';
import { NotificationService } from '../../services';

@Component({
  selector: 'app-notification',
  standalone: true,
  template: `
    <div class="notifications">
      @for (n of notification.notifications(); track n.id) {
        <div class="notification notification--{{ n.type }}" (click)="notification.dismiss(n.id)">
          <span class="notification__icon">
            @switch (n.type) {
              @case ('success') { [ok] }
              @case ('error') { [!!] }
              @case ('info') { [i] }
            }
          </span>
          <span class="notification__msg">{{ n.message }}</span>
        </div>
      }
    </div>
  `,
  styles: `
    .notifications {
      position: fixed;
      top: 48px;
      right: 16px;
      z-index: 1000;
      display: flex;
      flex-direction: column;
      gap: 6px;
      max-width: 360px;
    }

    .notification {
      display: flex;
      align-items: center;
      gap: 8px;
      padding: 8px 12px;
      background: var(--bg-secondary);
      border: 1px solid var(--border);
      font-size: 12px;
      cursor: pointer;
      animation: slideInRight 0.2s ease;
    }

    .notification__icon {
      flex-shrink: 0;
      font-weight: 700;
    }

    .notification--success {
      border-color: var(--accent-green);
      .notification__icon { color: var(--accent-green); }
    }

    .notification--error {
      border-color: var(--accent-red);
      .notification__icon { color: var(--accent-red); }
    }

    .notification--info {
      border-color: var(--accent-cyan);
      .notification__icon { color: var(--accent-cyan); }
    }
  `,
})
export class NotificationComponent {
  readonly notification = inject(NotificationService);
}

import { Injectable, signal } from '@angular/core';

export type NotificationType = 'success' | 'error' | 'info';

export interface Notification {
  id: number;
  message: string;
  type: NotificationType;
}

@Injectable({ providedIn: 'root' })
export class NotificationService {
  private counter = 0;

  readonly notifications = signal<Notification[]>([]);

  show(message: string, type: NotificationType = 'info'): void {
    const id = ++this.counter;
    this.notifications.set([...this.notifications(), { id, message, type }]);

    setTimeout(() => this.dismiss(id), 3000);
  }

  dismiss(id: number): void {
    this.notifications.set(this.notifications().filter((n) => n.id !== id));
  }

  success(message: string): void {
    this.show(message, 'success');
  }

  error(message: string): void {
    this.show(message, 'error');
  }

  info(message: string): void {
    this.show(message, 'info');
  }
}

import { TestBed } from '@angular/core/testing';
import { NotificationService } from './notification.service';

describe('NotificationService', () => {
  let service: NotificationService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(NotificationService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should start with empty notifications', () => {
    expect(service.notifications()).toEqual([]);
  });

  it('should add a notification via show()', () => {
    service.show('test message', 'info');
    const notifications = service.notifications();
    expect(notifications.length).toBe(1);
    expect(notifications[0].message).toBe('test message');
    expect(notifications[0].type).toBe('info');
    expect(notifications[0].id).toBe(1);
  });

  it('should dismiss a notification by id', () => {
    service.show('msg', 'info');
    service.dismiss(1);
    expect(service.notifications()).toEqual([]);
  });

  it('should auto-dismiss after 3000ms', () => {
    vi.useFakeTimers();
    service.show('auto-dismiss', 'info');
    expect(service.notifications().length).toBe(1);
    vi.advanceTimersByTime(3000);
    expect(service.notifications()).toEqual([]);
    vi.useRealTimers();
  });

  it('should call show with success type', () => {
    service.success('done');
    const n = service.notifications();
    expect(n.length).toBe(1);
    expect(n[0].type).toBe('success');
  });

  it('should call show with error type', () => {
    service.error('fail');
    const n = service.notifications();
    expect(n.length).toBe(1);
    expect(n[0].type).toBe('error');
  });

  it('should call show with info type', () => {
    service.info('info msg');
    const n = service.notifications();
    expect(n.length).toBe(1);
    expect(n[0].type).toBe('info');
  });

  it('should dismiss a non-existent id without error', () => {
    service.show('test', 'info');
    service.dismiss(999);
    expect(service.notifications().length).toBe(1);
  });

  it('should increment ids', () => {
    service.show('first', 'info');
    service.show('second', 'info');
    const notifications = service.notifications();
    expect(notifications[0].id).toBe(1);
    expect(notifications[1].id).toBe(2);
  });
});

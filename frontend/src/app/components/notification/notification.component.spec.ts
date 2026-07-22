import { TestBed } from '@angular/core/testing';
import { NotificationComponent } from './notification.component';
import { NotificationService } from '../../services';

describe('NotificationComponent', () => {
  afterEach(() => {
    vi.useFakeTimers();
    vi.advanceTimersByTime(5000);
    vi.useRealTimers();
  });

  async function setup() {
    await TestBed.configureTestingModule({
      imports: [NotificationComponent],
    }).compileComponents();
    const fixture = TestBed.createComponent(NotificationComponent);
    const component = fixture.componentInstance;
    fixture.detectChanges();
    return { fixture, component };
  }

  it('should create', async () => {
    const { component } = await setup();
    expect(component).toBeTruthy();
  });

  it('should render nothing when no notifications', async () => {
    const { fixture } = await setup();
    const notifications = fixture.nativeElement.querySelector('.notifications');
    expect(notifications).toBeTruthy();
    expect(notifications.children.length).toBe(0);
  });

  it('should render notifications from service', async () => {
    const { fixture } = await setup();
    const service = TestBed.inject(NotificationService);
    service.show('Test message', 'info');
    fixture.detectChanges();
    const items = fixture.nativeElement.querySelectorAll('.notification');
    expect(items.length).toBe(1);
    expect(items[0].textContent).toContain('Test message');
  });

  it('should render success notification with correct icon', async () => {
    const { fixture } = await setup();
    const service = TestBed.inject(NotificationService);
    service.success('Success!');
    fixture.detectChanges();
    const icon = fixture.nativeElement.querySelector('.notification__icon');
    expect(icon.textContent).toContain('ok');
  });

  it('should render error notification with correct icon', async () => {
    const { fixture } = await setup();
    const service = TestBed.inject(NotificationService);
    service.error('Error!');
    fixture.detectChanges();
    const icon = fixture.nativeElement.querySelector('.notification__icon');
    expect(icon.textContent).toContain('!!');
  });

  it('should render info notification with correct icon', async () => {
    const { fixture } = await setup();
    const service = TestBed.inject(NotificationService);
    service.info('Info!');
    fixture.detectChanges();
    const icon = fixture.nativeElement.querySelector('.notification__icon');
    expect(icon.textContent).toContain('i');
  });

  it('should dismiss notification on click', async () => {
    const { fixture } = await setup();
    const service = TestBed.inject(NotificationService);
    service.show('Click to dismiss', 'info');
    fixture.detectChanges();
    expect(service.notifications().length).toBe(1);
    const notificationEl = fixture.nativeElement.querySelector('.notification') as HTMLElement;
    notificationEl.click();
    expect(service.notifications().length).toBe(0);
  });

  it('should render multiple notifications', async () => {
    const { fixture } = await setup();
    const service = TestBed.inject(NotificationService);
    service.show('First', 'info');
    service.show('Second', 'success');
    fixture.detectChanges();
    const items = fixture.nativeElement.querySelectorAll('.notification');
    expect(items.length).toBe(2);
  });

  it('should apply correct type class', async () => {
    const { fixture } = await setup();
    const service = TestBed.inject(NotificationService);
    service.error('Error msg');
    fixture.detectChanges();
    const item = fixture.nativeElement.querySelector('.notification') as HTMLElement;
    expect(item.classList.contains('notification--error')).toBe(true);
  });
});

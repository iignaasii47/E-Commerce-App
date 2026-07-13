import { TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';
import { of, throwError } from 'rxjs';
import { LoginComponent } from './login.component';
import { AuthService, NotificationService } from '../../../services';

describe('LoginComponent', () => {
  async function setup() {
    await TestBed.configureTestingModule({
      imports: [LoginComponent],
      providers: [provideRouter([])],
    }).compileComponents();
    const fixture = TestBed.createComponent(LoginComponent);
    const component = fixture.componentInstance;
    fixture.detectChanges();
    return { fixture, component };
  }

  it('should create', async () => {
    const { component } = await setup();
    expect(component).toBeTruthy();
  });

  it('should render login form', async () => {
    const { fixture } = await setup();
    expect(fixture.nativeElement.textContent).toContain('su - login');
    expect(fixture.nativeElement.querySelector('.auth-form')).toBeTruthy();
  });

  it('should have register link', async () => {
    const { fixture } = await setup();
    expect(fixture.nativeElement.textContent).toContain("don't have an account?");
    const link = fixture.nativeElement.querySelector('a[routerLink="/register"]') as HTMLElement;
    expect(link).toBeTruthy();
  });

  it('should start with empty email and password', async () => {
    const { component } = await setup();
    expect(component.email()).toBe('');
    expect(component.password()).toBe('');
  });

  it('should show error when submitting with empty fields', async () => {
    const { fixture } = await setup();
    const form = fixture.nativeElement.querySelector('.auth-form') as HTMLFormElement;
    form.dispatchEvent(new Event('submit'));
    fixture.detectChanges();
    expect(fixture.nativeElement.textContent).toContain('email and password required');
  });

  it('should call auth.login and navigate on valid submit', async () => {
    const { fixture, component } = await setup();
    const auth = TestBed.inject(AuthService);
    const notifications = TestBed.inject(NotificationService);
    const routerSpy = vi.spyOn((component as any).router, 'navigate');
    const loginSpy = vi.spyOn(auth, 'login').mockImplementation((email: string, _password: string) => {
      (auth as any).currentUser.set({ id: 1, username: email.split('@')[0], email, token: 't' });
      return of({ id: 1, username: email.split('@')[0], email, createdAt: '', token: 't' });
    });
    const successSpy = vi.spyOn(notifications, 'success');

    component.email.set('test@example.com');
    component.password.set('password123');
    fixture.detectChanges();

    const form = fixture.nativeElement.querySelector('.auth-form') as HTMLFormElement;
    form.dispatchEvent(new Event('submit'));
    fixture.detectChanges();

    expect(loginSpy).toHaveBeenCalledWith('test@example.com', 'password123');
    expect(successSpy).toHaveBeenCalledWith('welcome back, test');
    expect(routerSpy).toHaveBeenCalledWith(['/']);
  });

  it('should show error banner on login failure', async () => {
    const { fixture, component } = await setup();
    const auth = TestBed.inject(AuthService);
    vi.spyOn(auth, 'login').mockReturnValue(
      throwError(() => ({ status: 401, error: { message: 'invalid credentials' } })),
    );

    component.email.set('bad@test.com');
    component.password.set('wrong');
    fixture.detectChanges();

    const form = fixture.nativeElement.querySelector('.auth-form') as HTMLFormElement;
    form.dispatchEvent(new Event('submit'));
    fixture.detectChanges();

    expect(fixture.nativeElement.textContent).toContain('invalid credentials');
  });
});

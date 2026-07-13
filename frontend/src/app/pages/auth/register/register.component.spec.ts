import { TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';
import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { RegisterComponent } from './register.component';
import { AuthService, NotificationService } from '../../../services';
import { environment } from '../../../../environments/environment';

describe('RegisterComponent', () => {
  async function setup() {
    await TestBed.configureTestingModule({
      imports: [RegisterComponent],
      providers: [provideRouter([]), provideHttpClient(), provideHttpClientTesting()],
    }).compileComponents();
    const fixture = TestBed.createComponent(RegisterComponent);
    const component = fixture.componentInstance;
    fixture.detectChanges();
    return { fixture, component };
  }

  it('should create', async () => {
    const { component } = await setup();
    expect(component).toBeTruthy();
  });

  it('should render registration form', async () => {
    const { fixture } = await setup();
    expect(fixture.nativeElement.textContent).toContain('useradd --register');
    expect(fixture.nativeElement.querySelector('.auth-form')).toBeTruthy();
    expect(fixture.nativeElement.textContent).toContain('already have an account?');
  });

  it('should start with empty form fields', async () => {
    const { component } = await setup();
    expect(component.username()).toBe('');
    expect(component.email()).toBe('');
    expect(component.password()).toBe('');
    expect(component.confirmPassword()).toBe('');
    expect(component.loading()).toBe(false);
    expect(component.errorMessage()).toBe('');
  });

  it('should validate required fields', async () => {
    const { fixture, component } = await setup();
    const form = fixture.nativeElement.querySelector('.auth-form') as HTMLFormElement;
    form.dispatchEvent(new Event('submit'));
    expect(component.errorMessage()).toBe('all fields are required');
  });

  it('should validate password length', async () => {
    const { fixture, component } = await setup();
    component.username.set('test');
    component.email.set('test@test.com');
    component.password.set('short');
    component.confirmPassword.set('short');
    const form = fixture.nativeElement.querySelector('.auth-form') as HTMLFormElement;
    form.dispatchEvent(new Event('submit'));
    expect(component.errorMessage()).toBe('password must be at least 8 characters');
  });

  it('should validate passwords match', async () => {
    const { fixture, component } = await setup();
    component.username.set('test');
    component.email.set('test@test.com');
    component.password.set('password123');
    component.confirmPassword.set('different');
    const form = fixture.nativeElement.querySelector('.auth-form') as HTMLFormElement;
    form.dispatchEvent(new Event('submit'));
    expect(component.errorMessage()).toBe('passwords do not match');
  });

  it('should show error banner when errorMessage is set', async () => {
    const { fixture, component } = await setup();
    component.errorMessage.set('test error');
    fixture.detectChanges();
    const banner = fixture.nativeElement.querySelector('.error-banner') as HTMLElement;
    expect(banner).toBeTruthy();
    expect(banner.textContent).toContain('test error');
  });

  it('should not show error banner when no error', async () => {
    const { fixture } = await setup();
    expect(fixture.nativeElement.querySelector('.error-banner')).toBeFalsy();
  });

  it('should register successfully and navigate', async () => {
    const { fixture, component } = await setup();
    const notifications = TestBed.inject(NotificationService);
    const successSpy = vi.spyOn(notifications, 'success');
    const routerSpy = vi.spyOn((component as any).router, 'navigate');
    const httpMock = TestBed.inject(HttpTestingController);

    component.username.set('newuser');
    component.email.set('new@test.com');
    component.password.set('password123');
    component.confirmPassword.set('password123');

    const form = fixture.nativeElement.querySelector('.auth-form') as HTMLFormElement;
    form.dispatchEvent(new Event('submit'));

    expect(component.loading()).toBe(true);

    const req = httpMock.expectOne(environment.apiUrl + '/api/users');
    expect(req.request.method).toBe('POST');
    req.flush({ id: 1, username: 'newuser', email: 'new@test.com', createdAt: '2026-01-01' });

    expect(component.loading()).toBe(false);
    expect(successSpy).toHaveBeenCalledWith('account created. welcome, newuser');
    expect(routerSpy).toHaveBeenCalledWith(['/']);
    httpMock.verify();
  });

  it('should handle 409 conflict error', async () => {
    const { fixture, component } = await setup();
    const httpMock = TestBed.inject(HttpTestingController);

    component.username.set('existing');
    component.email.set('exists@test.com');
    component.password.set('password123');
    component.confirmPassword.set('password123');

    const form = fixture.nativeElement.querySelector('.auth-form') as HTMLFormElement;
    form.dispatchEvent(new Event('submit'));

    const req = httpMock.expectOne(environment.apiUrl + '/api/users');
    req.flush({ message: 'username already taken' }, { status: 409, statusText: 'Conflict' });

    expect(component.errorMessage()).toBe('username already taken');
    expect(component.loading()).toBe(false);
    httpMock.verify();
  });

  it('should handle 409 conflict with default message', async () => {
    const { fixture, component } = await setup();
    const httpMock = TestBed.inject(HttpTestingController);

    component.username.set('existing');
    component.email.set('exists@test.com');
    component.password.set('password123');
    component.confirmPassword.set('password123');

    const form = fixture.nativeElement.querySelector('.auth-form') as HTMLFormElement;
    form.dispatchEvent(new Event('submit'));

    const req = httpMock.expectOne(environment.apiUrl + '/api/users');
    req.flush({}, { status: 409, statusText: 'Conflict' });

    expect(component.errorMessage()).toBe('username or email already taken');
    httpMock.verify();
  });

  it('should handle 400 validation error with field messages', async () => {
    const { fixture, component } = await setup();
    const httpMock = TestBed.inject(HttpTestingController);

    component.username.set('a');
    component.email.set('bad');
    component.password.set('password123');
    component.confirmPassword.set('password123');

    const form = fixture.nativeElement.querySelector('.auth-form') as HTMLFormElement;
    form.dispatchEvent(new Event('submit'));

    const req = httpMock.expectOne(environment.apiUrl + '/api/users');
    req.flush({ username: ['username too short'], email: ['invalid email'] }, { status: 400, statusText: 'Bad Request' });

    expect(component.errorMessage()).toContain('username too short');
    expect(component.errorMessage()).toContain('invalid email');
    httpMock.verify();
  });

  it('should handle 400 without object body', async () => {
    const { fixture, component } = await setup();
    const httpMock = TestBed.inject(HttpTestingController);

    component.username.set('test');
    component.email.set('test@test.com');
    component.password.set('password123');
    component.confirmPassword.set('password123');

    const form = fixture.nativeElement.querySelector('.auth-form') as HTMLFormElement;
    form.dispatchEvent(new Event('submit'));

    const req = httpMock.expectOne(environment.apiUrl + '/api/users');
    req.flush('plain error', { status: 400, statusText: 'Bad Request' });

    expect(component.errorMessage()).toBe('validation failed — check your input');
    httpMock.verify();
  });

  it('should handle network error (status 0)', async () => {
    const { fixture, component } = await setup();
    const httpMock = TestBed.inject(HttpTestingController);

    component.username.set('test');
    component.email.set('test@test.com');
    component.password.set('password123');
    component.confirmPassword.set('password123');

    const form = fixture.nativeElement.querySelector('.auth-form') as HTMLFormElement;
    form.dispatchEvent(new Event('submit'));

    const req = httpMock.expectOne(environment.apiUrl + '/api/users');
    req.flush('Network error', { status: 0, statusText: 'Unknown Error' });

    expect(component.errorMessage()).toBe('cannot connect to server — is the backend running?');
    httpMock.verify();
  });

  it('should handle unexpected error', async () => {
    const { fixture, component } = await setup();
    const httpMock = TestBed.inject(HttpTestingController);

    component.username.set('test');
    component.email.set('test@test.com');
    component.password.set('password123');
    component.confirmPassword.set('password123');

    const form = fixture.nativeElement.querySelector('.auth-form') as HTMLFormElement;
    form.dispatchEvent(new Event('submit'));

    const req = httpMock.expectOne(environment.apiUrl + '/api/users');
    req.flush('Server error', { status: 500, statusText: 'Internal Server Error' });

    expect(component.errorMessage()).toBe('unexpected error — please try again');
    httpMock.verify();
  });

  it('should disable submit button while loading', async () => {
    const { fixture, component } = await setup();
    component.loading.set(true);
    fixture.detectChanges();
    const btn = fixture.nativeElement.querySelector('.t-btn') as HTMLButtonElement;
    expect(btn.disabled).toBe(true);
    expect(fixture.nativeElement.textContent).toContain('creating...');
  });
});

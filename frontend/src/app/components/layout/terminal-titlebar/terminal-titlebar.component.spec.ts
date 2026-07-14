import { TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';
import { TerminalTitlebarComponent } from './terminal-titlebar.component';
import { AuthService } from '../../../services';

describe('TerminalTitlebarComponent', () => {
  async function setup() {
    await TestBed.configureTestingModule({
      imports: [TerminalTitlebarComponent],
      providers: [provideRouter([])],
    }).compileComponents();
    const fixture = TestBed.createComponent(TerminalTitlebarComponent);
    const component = fixture.componentInstance;
    fixture.detectChanges();
    return { fixture, component };
  }

  it('should create', async () => {
    const { component } = await setup();
    expect(component).toBeTruthy();
  });

  it('should render the titlebar with dots', async () => {
    const { fixture } = await setup();
    const compiled = fixture.nativeElement as HTMLElement;
    expect(compiled.querySelector('.titlebar')).toBeTruthy();
    expect(compiled.querySelectorAll('.dot').length).toBe(3);
  });

  it('should display the terminal title', async () => {
    const { fixture } = await setup();
    const compiled = fixture.nativeElement as HTMLElement;
    expect(compiled.querySelector('.titlebar__title')?.textContent).toContain('term-shop');
  });

  it('should show login link when not authenticated', async () => {
    const { fixture } = await setup();
    const auth = TestBed.inject(AuthService);
    auth.logout();
    fixture.detectChanges();
    const action = fixture.nativeElement.querySelector('.titlebar__action') as HTMLElement;
    expect(action).toBeTruthy();
    expect(action.textContent).toContain('login');
  });

  it('should show logout link when authenticated', async () => {
    const { fixture } = await setup();
    const auth = TestBed.inject(AuthService);
    (auth as any).currentUser.set({ id: 1, username: 'test', email: 'test@user.com', token: 't' });
    fixture.detectChanges();
    expect(fixture.nativeElement.textContent).toContain('test');
    const action = fixture.nativeElement.querySelector('.titlebar__action') as HTMLElement;
    expect(action).toBeTruthy();
    expect(action.textContent).toContain('logout');
  });

  it('should hide login link when authenticated', async () => {
    const { fixture } = await setup();
    const auth = TestBed.inject(AuthService);
    auth.logout();
    fixture.detectChanges();
    expect(fixture.nativeElement.textContent).toContain('login');

    (auth as any).currentUser.set({ id: 1, username: 'test', email: 'test@user.com', token: 't' });
    fixture.detectChanges();
    expect(fixture.nativeElement.textContent).not.toContain('login');
  });

  it('should logout and navigate to home when logout is clicked', async () => {
    const { fixture, component } = await setup();
    const auth = TestBed.inject(AuthService);
    (auth as any).currentUser.set({ id: 1, username: 'test', email: 'test@user.com', token: 't' });
    fixture.detectChanges();

    const routerSpy = vi.spyOn((component as any).router, 'navigate');
    const logoutSpy = vi.spyOn(auth, 'logout');

    const action = fixture.nativeElement.querySelector('.titlebar__action') as HTMLElement;
    action.click();

    expect(logoutSpy).toHaveBeenCalled();
    expect(routerSpy).toHaveBeenCalledWith(['/login']);
  });
});

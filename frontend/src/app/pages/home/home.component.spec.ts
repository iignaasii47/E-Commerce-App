import { TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';
import { HomeComponent } from './home.component';
import { AuthService } from '../../services';

describe('HomeComponent', () => {
  async function setup() {
    await TestBed.configureTestingModule({
      imports: [HomeComponent],
      providers: [provideRouter([])],
    }).compileComponents();
    const fixture = TestBed.createComponent(HomeComponent);
    const component = fixture.componentInstance;
    fixture.detectChanges();
    return { fixture, component };
  }

  it('should create', async () => {
    const { component } = await setup();
    expect(component).toBeTruthy();
  });

  it('should render ascii banner', async () => {
    const { fixture } = await setup();
    const banner = fixture.nativeElement.querySelector('.ascii-banner') as HTMLElement;
    expect(banner).toBeTruthy();
    expect(banner.textContent).toContain('_____');
  });

  it('should render tagline', async () => {
    const { fixture } = await setup();
    expect(fixture.nativeElement.textContent).toContain('terminal-based commerce interface');
  });

  it('should show guest username when not logged in', async () => {
    const { fixture } = await setup();
    const auth = TestBed.inject(AuthService);
    auth.logout();
    fixture.detectChanges();
    const sessionRow = fixture.nativeElement.querySelector('.info-grid') as HTMLElement;
    expect(sessionRow.textContent).toContain('guest@term-shop');
  });

  it('should show username when logged in', async () => {
    const { fixture } = await setup();
    const auth = TestBed.inject(AuthService);
    (auth as any).currentUser.set({ id: 1, username: 'testuser', email: 'testuser@test.com', token: 't' });
    fixture.detectChanges();
    const sessionRow = fixture.nativeElement.querySelector('.info-grid') as HTMLElement;
    expect(sessionRow.textContent).toContain('testuser@term-shop');
  });

  it('should show quick action items', async () => {
    const { fixture } = await setup();
    const items = fixture.nativeElement.querySelectorAll('.action-item');
    expect(items.length).toBe(2);
  });

  it('should show system info section', async () => {
    const { fixture } = await setup();
    expect(fixture.nativeElement.textContent).toContain('10 items in stock');
    expect(fixture.nativeElement.textContent).toContain('operational');
  });
});

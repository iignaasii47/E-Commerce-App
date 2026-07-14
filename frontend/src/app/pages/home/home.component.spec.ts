import { TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';
import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { HomeComponent } from './home.component';
import { AuthService } from '../../services';
import { environment } from '../../../environments/environment';

describe('HomeComponent', () => {
  async function setup() {
    await TestBed.configureTestingModule({
      imports: [HomeComponent],
      providers: [provideRouter([]), provideHttpClient(), provideHttpClientTesting()],
    }).compileComponents();
    const httpMock = TestBed.inject(HttpTestingController);
    const fixture = TestBed.createComponent(HomeComponent);
    httpMock.expectOne(environment.apiUrl + '/api/status').flush({ api: 'UP', database: 'UP', timestamp: '2026-01-01T00:00:00Z' });
    const component = fixture.componentInstance;
    fixture.detectChanges();
    return { fixture, component, httpMock };
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
    const grids = fixture.nativeElement.querySelectorAll('.info-grid');
    const lastGrid = grids[grids.length - 1] as HTMLElement;
    expect(lastGrid.textContent).toContain('guest@term-shop');
  });

  it('should show username when logged in', async () => {
    const { fixture } = await setup();
    const auth = TestBed.inject(AuthService);
    (auth as any).currentUser.set({ id: 1, username: 'testuser', email: 'testuser@test.com', token: 't' });
    fixture.detectChanges();
    const grids = fixture.nativeElement.querySelectorAll('.info-grid');
    const lastGrid = grids[grids.length - 1] as HTMLElement;
    expect(lastGrid.textContent).toContain('testuser@term-shop');
  });

  it('should show quick action items', async () => {
    const { fixture } = await setup();
    const items = fixture.nativeElement.querySelectorAll('.action-item');
    expect(items.length).toBe(3);
  });

  it('should show tech stack section', async () => {
    const { fixture } = await setup();
    expect(fixture.nativeElement.textContent).toContain('tech stack');
    expect(fixture.nativeElement.textContent).toContain('Angular');
    expect(fixture.nativeElement.textContent).toContain('Spring Boot');
    expect(fixture.nativeElement.textContent).toContain('PostgreSQL');
  });

  it('should show service status section', async () => {
    const { fixture } = await setup();
    expect(fixture.nativeElement.textContent).toContain('service status');
    expect(fixture.nativeElement.textContent).toContain('api:');
    expect(fixture.nativeElement.textContent).toContain('database:');
  });

  it('should show status as online after API responds', async () => {
    const { fixture } = await setup();
    const dots = fixture.nativeElement.querySelectorAll('.status-dot.online');
    expect(dots.length).toBe(2);
  });
});

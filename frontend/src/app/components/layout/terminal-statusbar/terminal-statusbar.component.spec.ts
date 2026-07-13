import { TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';
import { TerminalStatusbarComponent } from './terminal-statusbar.component';
import { AuthService, CartService } from '../../../services';

describe('TerminalStatusbarComponent', () => {
  async function setup() {
    await TestBed.configureTestingModule({
      imports: [TerminalStatusbarComponent],
      providers: [provideRouter([])],
    }).compileComponents();
    const fixture = TestBed.createComponent(TerminalStatusbarComponent);
    const component = fixture.componentInstance;
    fixture.detectChanges();
    return { fixture, component };
  }

  it('should create', async () => {
    const { component } = await setup();
    expect(component).toBeTruthy();
  });

  it('should render the statusbar', async () => {
    const { fixture } = await setup();
    expect(fixture.nativeElement.querySelector('.statusbar')).toBeTruthy();
  });

  it('should show connected status', async () => {
    const { fixture } = await setup();
    expect(fixture.nativeElement.querySelector('.status-dot--connected')).toBeTruthy();
    expect(fixture.nativeElement.textContent).toContain('connected');
  });

  it('should show login link when not authenticated', async () => {
    const { fixture } = await setup();
    const auth = TestBed.inject(AuthService);
    auth.logout();
    fixture.detectChanges();
    const link = fixture.nativeElement.querySelector('.statusbar__link') as HTMLElement;
    expect(link).toBeTruthy();
    expect(link.textContent).toContain('login');
  });

  it('should show username when authenticated', async () => {
    const { fixture } = await setup();
    const auth = TestBed.inject(AuthService);
    auth.login('test@user.com', 'pass');
    fixture.detectChanges();
    const link = fixture.nativeElement.querySelector('.statusbar__link');
    expect(link).toBeFalsy();
    expect(fixture.nativeElement.textContent).toContain('user:');
    expect(fixture.nativeElement.textContent).toContain('test');
  });

  it('should show cart item count and total', async () => {
    const { fixture } = await setup();
    const cart = TestBed.inject(CartService);
    const product = {
      id: 1, name: 'Test', description: '', price: 29.99,
      category: 'accessories', image: '', stock: 10, rating: 4,
    };
    cart.addToCart(product, 2);
    fixture.detectChanges();
    expect(fixture.nativeElement.textContent).toContain('cart:');
    expect(fixture.nativeElement.textContent).toContain('2 items');
    expect(fixture.nativeElement.textContent).toContain('$59.98');
  });

  it('should display breadcrumb route', async () => {
    const { fixture } = await setup();
    expect(fixture.nativeElement.textContent).toContain('~/');
  });
});

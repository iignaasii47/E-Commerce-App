import { TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';
import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TerminalStatusbarComponent } from './terminal-statusbar.component';
import { CartService } from '../../../services';
import { environment } from '../../../../environments/environment';

describe('TerminalStatusbarComponent', () => {
  async function setup() {
    await TestBed.configureTestingModule({
      imports: [TerminalStatusbarComponent],
      providers: [provideRouter([]), provideHttpClient(), provideHttpClientTesting()],
    }).compileComponents();
    const httpMock = TestBed.inject(HttpTestingController);
    const fixture = TestBed.createComponent(TerminalStatusbarComponent);
    httpMock.expectOne(environment.apiUrl + '/api/cart').flush([]);
    const component = fixture.componentInstance;
    fixture.detectChanges();
    return { fixture, component, httpMock };
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

  it('should show cart item count and total', async () => {
    const { fixture } = await setup();
    const cart = TestBed.inject(CartService);
    cart.items.set([{ id: 1, productId: 1, productName: 'Test', unitPrice: 29.99, quantity: 2, subtotal: 59.98 }]);
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

import { TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';
import { NotFoundComponent } from './not-found.component';

describe('NotFoundComponent', () => {
  async function setup() {
    await TestBed.configureTestingModule({
      imports: [NotFoundComponent],
      providers: [provideRouter([])],
    }).compileComponents();
    const fixture = TestBed.createComponent(NotFoundComponent);
    const component = fixture.componentInstance;
    fixture.detectChanges();
    return { fixture, component };
  }

  it('should create', async () => {
    const { component } = await setup();
    expect(component).toBeTruthy();
  });

  it('should render 404 error code', async () => {
    const { fixture } = await setup();
    const code = fixture.nativeElement.querySelector('.error-code') as HTMLElement;
    expect(code.textContent).toContain('404');
  });

  it('should display current path in error message', async () => {
    const { fixture } = await setup();
    const msg = fixture.nativeElement.querySelector('.error-msg') as HTMLElement;
    expect(msg.textContent).toContain('$ command not found:');
  });

  it('should render ascii art', async () => {
    const { fixture } = await setup();
    const ascii = fixture.nativeElement.querySelector('.error-ascii') as HTMLElement;
    expect(ascii).toBeTruthy();
    expect(ascii.textContent).toContain('____');
  });

  it('should have back link to home', async () => {
    const { fixture } = await setup();
    const link = fixture.nativeElement.querySelector('.back-link') as HTMLElement;
    expect(link).toBeTruthy();
    expect(link.textContent).toContain('cd ~');
    expect(link.getAttribute('routerLink')).toBe('/');
  });
});

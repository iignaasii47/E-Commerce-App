import { TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';
import { TerminalTitlebarComponent } from './terminal-titlebar.component';

describe('TerminalTitlebarComponent', () => {
  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [TerminalTitlebarComponent],
      providers: [provideRouter([])],
    }).compileComponents();
  });

  it('should create', () => {
    const fixture = TestBed.createComponent(TerminalTitlebarComponent);
    expect(fixture.componentInstance).toBeTruthy();
  });

  it('should render the titlebar with dots', () => {
    const fixture = TestBed.createComponent(TerminalTitlebarComponent);
    fixture.detectChanges();
    const compiled = fixture.nativeElement as HTMLElement;
    expect(compiled.querySelector('.titlebar')).toBeTruthy();
    expect(compiled.querySelectorAll('.dot').length).toBe(3);
  });

  it('should display the terminal title', () => {
    const fixture = TestBed.createComponent(TerminalTitlebarComponent);
    fixture.detectChanges();
    const compiled = fixture.nativeElement as HTMLElement;
    expect(compiled.querySelector('.titlebar__title')?.textContent).toContain('term-shop');
  });
});

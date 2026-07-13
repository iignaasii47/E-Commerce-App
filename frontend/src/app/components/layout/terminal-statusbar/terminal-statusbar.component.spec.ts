import { TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';
import { TerminalStatusbarComponent } from './terminal-statusbar.component';

describe('TerminalStatusbarComponent', () => {
  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [TerminalStatusbarComponent],
      providers: [provideRouter([])],
    }).compileComponents();
  });

  it('should create', () => {
    const fixture = TestBed.createComponent(TerminalStatusbarComponent);
    expect(fixture.componentInstance).toBeTruthy();
  });

  it('should render the statusbar', () => {
    const fixture = TestBed.createComponent(TerminalStatusbarComponent);
    fixture.detectChanges();
    const compiled = fixture.nativeElement as HTMLElement;
    expect(compiled.querySelector('.statusbar')).toBeTruthy();
  });

  it('should show connected status', () => {
    const fixture = TestBed.createComponent(TerminalStatusbarComponent);
    fixture.detectChanges();
    const compiled = fixture.nativeElement as HTMLElement;
    expect(compiled.querySelector('.status-dot--connected')).toBeTruthy();
  });
});

import { TestBed } from '@angular/core/testing';
import { TerminalButtonComponent } from './terminal-button.component';

describe('TerminalButtonComponent', () => {
  async function setup() {
    await TestBed.configureTestingModule({
      imports: [TerminalButtonComponent],
    }).compileComponents();
    const fixture = TestBed.createComponent(TerminalButtonComponent);
    const component = fixture.componentInstance;
    fixture.detectChanges();
    return { fixture, component };
  }

  it('should create', async () => {
    const { component } = await setup();
    expect(component).toBeTruthy();
  });

  it('should render $ prefix', async () => {
    const { fixture } = await setup();
    const compiled = fixture.nativeElement as HTMLElement;
    expect(compiled.querySelector('.t-btn__prefix')?.textContent).toBe('$');
  });

  it('should default to default variant', async () => {
    const { fixture } = await setup();
    const btn = fixture.nativeElement.querySelector('.t-btn') as HTMLElement;
    expect(btn.classList.contains('t-btn--default')).toBe(true);
  });

  it('should apply variant class', async () => {
    const { fixture, component } = await setup();
    fixture.componentRef.setInput('variant', 'primary');
    fixture.detectChanges();
    const btn = fixture.nativeElement.querySelector('.t-btn') as HTMLElement;
    expect(btn.classList.contains('t-btn--primary')).toBe(true);
  });

  it('should apply danger variant class', async () => {
    const { fixture, component } = await setup();
    fixture.componentRef.setInput('variant', 'danger');
    fixture.detectChanges();
    const btn = fixture.nativeElement.querySelector('.t-btn') as HTMLElement;
    expect(btn.classList.contains('t-btn--danger')).toBe(true);
  });

  it('should apply ghost variant class', async () => {
    const { fixture, component } = await setup();
    fixture.componentRef.setInput('variant', 'ghost');
    fixture.detectChanges();
    const btn = fixture.nativeElement.querySelector('.t-btn') as HTMLElement;
    expect(btn.classList.contains('t-btn--ghost')).toBe(true);
  });

  it('should disable button when disabled input is true', async () => {
    const { fixture, component } = await setup();
    fixture.componentRef.setInput('disabled', true);
    fixture.detectChanges();
    const btn = fixture.nativeElement.querySelector('.t-btn') as HTMLButtonElement;
    expect(btn.disabled).toBe(true);
  });

  it('should set button type attribute', async () => {
    const { fixture, component } = await setup();
    fixture.componentRef.setInput('type', 'submit');
    fixture.detectChanges();
    const btn = fixture.nativeElement.querySelector('.t-btn') as HTMLButtonElement;
    expect(btn.type).toBe('submit');
  });

  it('should render transcluded content', async () => {
    await TestBed.configureTestingModule({
      imports: [TerminalButtonComponent],
    }).compileComponents();
    const fixture = TestBed.createComponent(TerminalButtonComponent);
    fixture.nativeElement.innerHTML = '<app-terminal-button>click me</app-terminal-button>';
    const debugEl = fixture.debugElement;
    fixture.detectChanges();
    expect(fixture.nativeElement.textContent).toContain('click me');
  });

  it('should default type to button', async () => {
    const { component } = await setup();
    expect(component.type()).toBe('button');
  });

  it('should default disabled to false', async () => {
    const { component } = await setup();
    expect(component.disabled()).toBe(false);
  });
});

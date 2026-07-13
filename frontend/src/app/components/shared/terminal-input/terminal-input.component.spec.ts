import { TestBed } from '@angular/core/testing';
import { TerminalInputComponent } from './terminal-input.component';

describe('TerminalInputComponent', () => {
  async function setup() {
    await TestBed.configureTestingModule({
      imports: [TerminalInputComponent],
    }).compileComponents();
    const fixture = TestBed.createComponent(TerminalInputComponent);
    const component = fixture.componentInstance;
    fixture.detectChanges();
    return { fixture, component };
  }

  it('should create', async () => {
    const { component } = await setup();
    expect(component).toBeTruthy();
  });

  it('should render > prompt', async () => {
    const { fixture } = await setup();
    expect(fixture.nativeElement.querySelector('.t-input__prompt')?.textContent).toBe('>');
  });

  it('should render label when provided', async () => {
    const { fixture } = await setup();
    fixture.componentRef.setInput('label', 'Email');
    fixture.detectChanges();
    const label = fixture.nativeElement.querySelector('.t-input__label') as HTMLElement;
    expect(label).toBeTruthy();
    expect(label.textContent).toBe('Email');
  });

  it('should not render label when not provided', async () => {
    const { fixture } = await setup();
    expect(fixture.nativeElement.querySelector('.t-input__label')).toBeFalsy();
  });

  it('should set input id from input', async () => {
    const { fixture } = await setup();
    fixture.componentRef.setInput('id', 'my-input');
    fixture.detectChanges();
    const input = fixture.nativeElement.querySelector('.t-input__field') as HTMLInputElement;
    expect(input.id).toBe('my-input');
  });

  it('should set input type from input', async () => {
    const { fixture } = await setup();
    fixture.componentRef.setInput('type', 'password');
    fixture.detectChanges();
    const input = fixture.nativeElement.querySelector('.t-input__field') as HTMLInputElement;
    expect(input.type).toBe('password');
  });

  it('should set input placeholder from input', async () => {
    const { fixture } = await setup();
    fixture.componentRef.setInput('placeholder', 'enter text');
    fixture.detectChanges();
    const input = fixture.nativeElement.querySelector('.t-input__field') as HTMLInputElement;
    expect(input.placeholder).toBe('enter text');
  });

  it('should reflect value input', async () => {
    const { fixture } = await setup();
    fixture.componentRef.setInput('value', 'test value');
    fixture.detectChanges();
    const input = fixture.nativeElement.querySelector('.t-input__field') as HTMLInputElement;
    expect(input.value).toBe('test value');
  });

  it('should disable input when disabled is true', async () => {
    const { fixture } = await setup();
    fixture.componentRef.setInput('disabled', true);
    fixture.detectChanges();
    const input = fixture.nativeElement.querySelector('.t-input__field') as HTMLInputElement;
    expect(input.disabled).toBe(true);
  });

  it('should emit valueChange on input event', async () => {
    const { fixture } = await setup();
    let emitted = '';
    fixture.componentInstance.valueChange.subscribe((v: string) => (emitted = v));
    const input = fixture.nativeElement.querySelector('.t-input__field') as HTMLInputElement;
    input.value = 'hello';
    input.dispatchEvent(new Event('input'));
    expect(emitted).toBe('hello');
  });

  it('should set isFocused on focus', async () => {
    const { fixture, component } = await setup();
    const input = fixture.nativeElement.querySelector('.t-input__field') as HTMLInputElement;
    input.dispatchEvent(new Event('focus'));
    expect(component.isFocused).toBe(true);
  });

  it('should clear isFocused on blur', async () => {
    const { fixture, component } = await setup();
    component.isFocused = true;
    const input = fixture.nativeElement.querySelector('.t-input__field') as HTMLInputElement;
    input.dispatchEvent(new Event('blur'));
    expect(component.isFocused).toBe(false);
  });

  it('should add focused class when focused', async () => {
    const { fixture } = await setup();
    const input = fixture.nativeElement.querySelector('.t-input__field') as HTMLInputElement;
    input.dispatchEvent(new Event('focus'));
    fixture.detectChanges();
    const wrapper = fixture.nativeElement.querySelector('.t-input-wrapper') as HTMLElement;
    expect(wrapper.classList.contains('focused')).toBe(true);
  });
});

import { Component, input, output } from '@angular/core';

@Component({
  selector: 'app-terminal-input',
  standalone: true,
  template: `
    <div class="t-input-wrapper" [class.focused]="isFocused">
      <span class="t-input__prompt">&gt;</span>
      @if (label()) {
        <label class="t-input__label" [for]="id()">{{ label() }}</label>
      }
      <input
        class="t-input__field"
        [id]="id()"
        [type]="type()"
        [placeholder]="placeholder()"
        [value]="value()"
        [disabled]="disabled()"
        (input)="onInput($event)"
        (focus)="isFocused = true"
        (blur)="isFocused = false" />
    </div>
  `,
  styles: `
    .t-input-wrapper {
      display: flex;
      align-items: center;
      gap: 8px;
      background: var(--bg-primary);
      border: 1px solid var(--border);
      padding: 6px 10px;
      transition: border-color 0.15s;

      &.focused {
        border-color: var(--accent-green);
      }
    }

    .t-input__prompt {
      color: var(--accent-green);
      font-weight: 600;
      flex-shrink: 0;
    }

    .t-input__label {
      color: var(--text-muted);
      font-size: 12px;
      flex-shrink: 0;
      white-space: nowrap;
    }

    .t-input__field {
      flex: 1;
      background: transparent;
      border: none;
      outline: none;
      color: var(--text-bright);
      font-size: 13px;
      min-width: 0;

      &::placeholder {
        color: var(--text-muted);
      }

      &:disabled {
        opacity: 0.5;
      }
    }
  `,
})
export class TerminalInputComponent {
  id = input('');
  type = input('text');
  placeholder = input('');
  label = input('');
  value = input('');
  disabled = input(false);

  valueChange = output<string>();

  isFocused = false;

  onInput(event: Event): void {
    const target = event.target as HTMLInputElement;
    this.valueChange.emit(target.value);
  }
}

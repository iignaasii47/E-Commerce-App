import { Component, inject, signal, viewChild, ElementRef, AfterViewInit, OnInit, effect } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { HttpErrorResponse } from '@angular/common/http';
import { ChatMessage } from '../../models/chat-message.model';
import { ChatbotService } from '../../services/chatbot.service';
import { AuthService } from '../../services/auth.service';
import { CartService } from '../../services/cart.service';

@Component({
  selector: 'app-chatbot',
  standalone: true,
  imports: [FormsModule],
  template: `
    <div class="chat">
      <div class="chat__header">
        <span class="chat__header-cmd">$ ./assistant</span>
        <span class="chat__header-badge">chat</span>
      </div>

      <div class="chat__messages" #messagesContainer (click)="focusInput()">
        @for (msg of messages(); track msg.timestamp) {
          <div class="chat__message" [class.chat__message--user]="msg.role === 'user'" [class.chat__message--error]="msg.role === 'bot' && msg.content.startsWith('[error]')">
            <div class="chat__message-prefix">
              <span class="chat__prompt-timestamp">[{{ formatTimestamp(msg.timestamp) }}]</span>
              @if (msg.role === 'user') {
                <span class="chat__prompt-user"
                  >{{ auth.username() }}&#64;term-shop
                  <span class="chat__prompt-sep">~</span>
                  <span class="chat__prompt-dollar">$</span></span
                >
              } @else {
                <span class="chat__prompt-bot"
                  >assistant&#64;term-shop
                  <span class="chat__prompt-sep">~</span>
                  <span class="chat__prompt-dollar">$</span></span
                >
              }
            </div>
            <div class="chat__message-content">{{ msg.content }}@if (isLoading() && !isTyping() && $last && msg.role === 'bot' && msg.content === ''){<span class="chat__cursor">_</span>}</div>
          </div>
        }
        @if (isLoading() && !isTyping()) {
          <div class="chat__message">
            <div class="chat__message-prefix">
              <span class="chat__prompt-bot">assistant&#64;term-shop <span class="chat__prompt-sep">~</span> <span class="chat__prompt-dollar">$</span></span>
            </div>
            <div class="chat__message-content">
              <span class="chat__spinner">{{ spinnerChar() }}</span>
              <span class="chat__cursor">_</span>
            </div>
          </div>
        }
      </div>

      <form class="chat__input" (submit)="sendMessage(); $event.preventDefault()">
        <span class="chat__input-prompt">&gt;</span>
        <div class="chat__input-visual">
          <span class="chat__input-display">{{ inputValue }}</span>
          <span class="chat__input-cursor" [class.chat__input-cursor--blink]="focused()" [style.left]="cursorPos() + 'ch'"></span>
          <input
            class="chat__input-field"
            #chatInput
            type="text"
            placeholder="type a message..."
            [ngModel]="inputValue"
            (ngModelChange)="inputValue = $event"
            name="message"
            autocomplete="off"
            [disabled]="isLoading()"
            (keydown)="onKeydown($event)"
            (keyup)="updateCursorPos($event)"
            (click)="updateCursorPos($event)"
            (focus)="focused.set(true)"
            (blur)="focused.set(false)" />
        </div>
      </form>
    </div>
  `,
  styles: `
    .chat {
      display: flex;
      flex-direction: column;
      height: 100%;
    }

    .chat__header {
      display: flex;
      align-items: center;
      gap: 10px;
      padding: 8px 12px;
      border-bottom: 1px solid var(--border);
      background: var(--bg-secondary);
      flex-shrink: 0;
    }

    .chat__header-cmd {
      color: var(--accent-green);
      font-size: 12px;
      font-weight: 600;
    }

    .chat__header-badge {
      font-size: 10px;
      color: var(--bg-primary);
      background: var(--accent-cyan);
      padding: 1px 6px;
      border-radius: 2px;
    }

    .chat__messages {
      flex: 1;
      overflow-y: auto;
      padding: 12px;
      display: flex;
      flex-direction: column;
      gap: 6px;
    }

    .chat__message {
      display: flex;
      flex-direction: column;
      gap: 1px;
    }

    .chat__message-prefix {
      font-size: 11px;
      display: flex;
      align-items: center;
      gap: 6px;
    }

    .chat__prompt-timestamp {
      color: var(--text-muted);
      opacity: 0.5;
      font-size: 10px;
    }

    .chat__prompt-user {
      color: var(--text-muted);
    }

    .chat__prompt-bot {
      color: var(--accent-green);
      opacity: 0.8;
    }

    .chat__prompt-sep {
      color: var(--text-muted);
      opacity: 0.5;
    }

    .chat__prompt-dollar {
      color: inherit;
    }

    .chat__message-content {
      font-size: 13px;
      line-height: 1.6;
      color: var(--text-primary);
      white-space: pre-wrap;
      word-break: break-word;
      padding-left: 4px;
    }

    .chat__message--user .chat__message-content {
      color: var(--text-bright);
    }

    .chat__message--error .chat__message-content {
      color: var(--accent-red);
    }

    .chat__cursor {
      animation: blink 1s step-end infinite;
      color: var(--accent-green);
    }

    .chat__spinner {
      color: var(--accent-cyan);
    }

    @keyframes blink {
      0%, 100% { opacity: 1; }
      50% { opacity: 0; }
    }

    .chat__input {
      display: flex;
      align-items: center;
      gap: 8px;
      padding: 8px 12px;
      border-top: 1px solid var(--border);
      background: var(--bg-secondary);
      flex-shrink: 0;
    }

    .chat__input-prompt {
      color: var(--accent-green);
      font-weight: 600;
      font-size: 14px;
      flex-shrink: 0;
    }

    .chat__input-visual {
      position: relative;
      flex: 1;
      min-height: 1.5em;
    }

    .chat__input-display {
      position: absolute;
      left: 0;
      top: 50%;
      transform: translateY(-50%);
      pointer-events: none;
      color: var(--text-bright);
      font-size: 13px;
      font-family: inherit;
      white-space: pre;
      overflow: hidden;
      width: 100%;
    }

    .chat__input-cursor {
      position: absolute;
      top: 0;
      left: 0;
      width: 1ch;
      height: 100%;
      border-bottom: 2px solid transparent;
    }

    .chat__input-cursor--blink {
      border-bottom-color: var(--accent-green);
      animation: blink 1s step-end infinite;
    }

    .chat__input-field {
      width: 100%;
      background: transparent;
      border: none;
      outline: none;
      color: transparent;
      caret-color: transparent;
      font-size: 13px;
      font-family: inherit;
      padding: 0;
      position: relative;
      z-index: 1;

      &::placeholder {
        color: var(--text-muted);
      }

      &:disabled {
        opacity: 0.5;
      }
    }
  `,
})
export class ChatbotComponent implements OnInit, AfterViewInit {
  private readonly chatbotService = inject(ChatbotService);
  readonly auth = inject(AuthService);
  readonly cartService = inject(CartService);

  readonly messages = signal<ChatMessage[]>([]);
  readonly isLoading = signal(false);
  readonly spinnerChar = signal('');
  readonly focused = signal(false);
  readonly cursorPos = signal(0);
  inputValue = '';

  readonly messagesContainer = viewChild<ElementRef<HTMLDivElement>>('messagesContainer');
  private readonly chatInput = viewChild<ElementRef<HTMLInputElement>>('chatInput');

  typewriterSpeed = 10;

  private commandHistory: string[] = [];
  private historyIndex = -1;
  private isTypingFlag = false;
  private pendingTypewriter: ReturnType<typeof setInterval> | null = null;

  private readonly scrollEffect = effect(() => {
    this.messages();
    setTimeout(() => this.scrollToBottom());
  });

  private readonly spinnerEffect = effect((onCleanup) => {
    if (this.isLoading()) {
      const chars = ['\\', '|', '/', '-'];
      let i = 0;
      this.spinnerChar.set(chars[0]);
      const interval = setInterval(() => {
        i = (i + 1) % chars.length;
        this.spinnerChar.set(chars[i]);
      }, 150);
      onCleanup(() => {
        clearInterval(interval);
        this.spinnerChar.set('');
      });
    }
  });

  ngOnInit(): void {
    this.isLoading.set(true);
    this.chatbotService.getGreeting().subscribe((greeting) => {
      this.messages.update((msgs) => [
        ...msgs,
        { role: 'bot', content: greeting, timestamp: new Date() },
      ]);
      this.isLoading.set(false);
      this.focusInput();
    });
  }

  ngAfterViewInit(): void {
    this.scrollToBottom();
    setTimeout(() => this.focusInput());
  }

  isTyping(): boolean {
    return this.isTypingFlag;
  }

  focusInput(): void {
    setTimeout(() => this.chatInput()?.nativeElement.focus());
  }

  updateCursorPos(event: Event): void {
    this.cursorPos.set((event.target as HTMLInputElement).selectionStart ?? this.inputValue.length);
  }

  formatTimestamp(date: Date): string {
    const h = date.getHours().toString().padStart(2, '0');
    const m = date.getMinutes().toString().padStart(2, '0');
    const s = date.getSeconds().toString().padStart(2, '0');
    return `${h}:${m}:${s}`;
  }

  onKeydown(event: KeyboardEvent): void {
    if (event.key === 'ArrowUp') {
      event.preventDefault();
      if (this.commandHistory.length > 0) {
        if (this.historyIndex < this.commandHistory.length - 1) {
          this.historyIndex++;
          this.inputValue =
            this.commandHistory[this.commandHistory.length - 1 - this.historyIndex];
        }
      }
    } else if (event.key === 'ArrowDown') {
      event.preventDefault();
      if (this.historyIndex > 0) {
        this.historyIndex--;
        this.inputValue =
          this.commandHistory[this.commandHistory.length - 1 - this.historyIndex];
      } else {
        this.historyIndex = -1;
        this.inputValue = '';
      }
    }
  }

  sendMessage(): void {
    const text = this.inputValue.trim();
    if (!text || this.isLoading()) return;

    if (text.toLowerCase() === 'clear') {
      this.messages.set([]);
      this.inputValue = '';
      this.focusInput();
      return;
    }

    this.commandHistory.push(text);
    this.historyIndex = -1;

    const history = this.messages().map((m) => ({ role: m.role, content: m.content }));

    this.messages.update((msgs) => [
      ...msgs,
      { role: 'user', content: text, timestamp: new Date() },
    ]);
    this.inputValue = '';
    this.isLoading.set(true);
    this.isTypingFlag = false;

    this.chatbotService.sendMessage(text, history).subscribe({
      next: ({ reply, toolsUsed }) => {
        this.startTypewriter(reply, toolsUsed);
      },
      error: (err: HttpErrorResponse) => {
        this.isLoading.set(false);
        this.isTypingFlag = false;
        const message = err.error?.message ?? err.message ?? 'an error occurred';
        this.messages.update((msgs) => [
          ...msgs,
          { role: 'bot', content: `[error] ${message}`, timestamp: new Date() },
        ]);
        this.focusInput();
      },
    });
  }

  private startTypewriter(reply: string, toolsUsed?: string[]): void {
    this.isTypingFlag = true;

    if (this.typewriterSpeed <= 0) {
      this.messages.update((msgs) => [
        ...msgs,
        { role: 'bot', content: reply, timestamp: new Date() },
      ]);
      this.finishTypewriter(toolsUsed);
      return;
    }

    this.messages.update((msgs) => [
      ...msgs,
      { role: 'bot', content: '', timestamp: new Date() },
    ]);

    let i = 0;
    this.pendingTypewriter = setInterval(() => {
      if (i < reply.length) {
        let chunk: string;
        if (reply[i] === ' ') {
          let end = i + 1;
          while (end < reply.length && reply[end] === ' ') end++;
          chunk = reply.slice(i, end);
          i = end;
        } else {
          chunk = reply[i];
          i++;
        }
        this.messages.update((msgs) => {
          const updated = [...msgs];
          updated[updated.length - 1] = {
            ...updated[updated.length - 1],
            content: updated[updated.length - 1].content + chunk,
          };
          return updated;
        });
      } else {
        this.finishTypewriter(toolsUsed);
      }
    }, this.typewriterSpeed);
  }

  private finishTypewriter(toolsUsed?: string[]): void {
    if (this.pendingTypewriter) {
      clearInterval(this.pendingTypewriter);
      this.pendingTypewriter = null;
    }
    this.isLoading.set(false);
    this.isTypingFlag = false;
    this.focusInput();
    if (toolsUsed?.some((t) => t === 'add_to_cart' || t === 'remove_from_cart')) {
      this.cartService.loadCart();
    }
  }

  private scrollToBottom(): void {
    const el = this.messagesContainer();
    if (el) {
      el.nativeElement.scrollTop = el.nativeElement.scrollHeight;
    }
  }
}

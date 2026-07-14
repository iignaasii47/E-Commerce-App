import { Component, inject, signal, viewChild, ElementRef, AfterViewInit, OnInit, effect } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ChatMessage } from '../../models/chat-message.model';
import { ChatbotService } from '../../services/chatbot.service';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-chatbot',
  standalone: true,
  imports: [FormsModule],
  template: `
    <div class="chat">
      <div class="chat__header">
        <span class="chat__header-cmd">$ ./assistant</span>
        <span class="chat__header-badge">chat</span>
        @if (isLoading()) {
          <span class="chat__typing-indicator">▌typing...▐</span>
        }
      </div>

      <div class="chat__messages" #messagesContainer>
        @for (msg of messages(); track msg.timestamp) {
          <div class="chat__message" [class.chat__message--user]="msg.role === 'user'">
            <div class="chat__message-prefix">
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
            <div class="chat__message-content">{{ msg.content }}</div>
          </div>
        }
      </div>

      <form class="chat__input" (submit)="sendMessage(); $event.preventDefault()">
        <span class="chat__input-prompt">&gt;</span>
        <input
          class="chat__input-field"
          type="text"
          placeholder="type a message..."
          [(ngModel)]="inputValue"
          name="message"
          autocomplete="off"
          [disabled]="isLoading()" />
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

    .chat__typing-indicator {
      margin-left: auto;
      font-size: 11px;
      color: var(--accent-green);
      animation: pulse 1s ease-in-out infinite;
    }

    @keyframes pulse {
      0%, 100% { opacity: 1; }
      50% { opacity: 0.4; }
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
    }

    .chat__input-field {
      flex: 1;
      background: transparent;
      border: none;
      outline: none;
      color: var(--text-bright);
      font-size: 13px;
      font-family: inherit;

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

  readonly messages = signal<ChatMessage[]>([]);
  readonly isLoading = signal(false);
  inputValue = '';

  readonly messagesContainer = viewChild<ElementRef<HTMLDivElement>>('messagesContainer');

  private readonly scrollEffect = effect(() => {
    this.messages();
    if (!this.isLoading()) {
      setTimeout(() => this.scrollToBottom());
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
    });
  }

  ngAfterViewInit(): void {
    this.scrollToBottom();
  }

  sendMessage(): void {
    const text = this.inputValue.trim();
    if (!text || this.isLoading()) return;

    const history = this.messages().map((m) => ({ role: m.role, content: m.content }));

    this.messages.update((msgs) => [
      ...msgs,
      { role: 'user', content: text, timestamp: new Date() },
    ]);
    this.inputValue = '';
    this.isLoading.set(true);

    this.chatbotService.sendMessage(text, history).subscribe((response) => {
      this.messages.update((msgs) => [
        ...msgs,
        { role: 'bot', content: response, timestamp: new Date() },
      ]);
      this.isLoading.set(false);
    });
  }

  private scrollToBottom(): void {
    const el = this.messagesContainer();
    if (el) {
      el.nativeElement.scrollTop = el.nativeElement.scrollHeight;
    }
  }
}

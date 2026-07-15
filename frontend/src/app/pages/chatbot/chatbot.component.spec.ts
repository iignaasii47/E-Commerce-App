import { TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';
import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { of, throwError } from 'rxjs';
import { ChatbotComponent } from './chatbot.component';
import { ChatbotService, CartService } from '../../services';
import { environment } from '../../../environments/environment';

describe('ChatbotComponent', () => {
  async function setup() {
    await TestBed.configureTestingModule({
      imports: [ChatbotComponent],
      providers: [provideRouter([]), provideHttpClient(), provideHttpClientTesting()],
    }).compileComponents();
    const httpMock = TestBed.inject(HttpTestingController);
    const fixture = TestBed.createComponent(ChatbotComponent);
    httpMock.expectOne(environment.apiUrl + '/api/cart').flush([]);
    const component = fixture.componentInstance;
    component.typewriterSpeed = 0;
    return { fixture, component, httpMock };
  }

  it('should create', async () => {
    const { component } = await setup();
    expect(component).toBeTruthy();
  });

  it('should render header with command and badge', async () => {
    const { fixture } = await setup();
    fixture.detectChanges();
    expect(fixture.nativeElement.textContent).toContain('./assistant');
    expect(fixture.nativeElement.textContent).toContain('chat');
  });

  it('should render input field with prompt', async () => {
    const { fixture } = await setup();
    fixture.detectChanges();
    const input = fixture.nativeElement.querySelector('.chat__input-field') as HTMLElement;
    expect(input).toBeTruthy();
    expect(input.getAttribute('placeholder')).toBe('type a message...');
    expect(fixture.nativeElement.querySelector('.chat__input-prompt')?.textContent).toBe('>');
  });

  it('should call getGreeting on init', async () => {
    const { fixture } = await setup();
    const chatbot = TestBed.inject(ChatbotService);
    const greetingSpy = vi.spyOn(chatbot, 'getGreeting').mockReturnValue(of('Mock greeting'));
    fixture.detectChanges();
    expect(greetingSpy).toHaveBeenCalled();
  });

  it('should add greeting message on init', async () => {
    const { fixture, component } = await setup();
    const chatbot = TestBed.inject(ChatbotService);
    vi.spyOn(chatbot, 'getGreeting').mockReturnValue(of('Mock greeting'));
    fixture.detectChanges();
    expect(component.messages().length).toBe(1);
    expect(component.messages()[0].content).toBe('Mock greeting');
    expect(component.messages()[0].role).toBe('bot');
  });

  it('should show loading spinner in chat when loading', async () => {
    const { fixture, component } = await setup();
    component.isLoading.set(true);
    fixture.detectChanges();
    const spinner = fixture.nativeElement.querySelector('.chat__spinner');
    expect(spinner).toBeTruthy();
  });

  it('should hide loading spinner when not loading', async () => {
    const { fixture, component } = await setup();
    const chatbot = TestBed.inject(ChatbotService);
    vi.spyOn(chatbot, 'getGreeting').mockReturnValue(of('greeting'));
    fixture.detectChanges();
    expect(component.isLoading()).toBe(false);
    expect(fixture.nativeElement.querySelector('.chat__spinner')).toBeFalsy();
  });

  it('should render messages in the chat', async () => {
    const { fixture, component } = await setup();
    const chatbot = TestBed.inject(ChatbotService);
    vi.spyOn(chatbot, 'getGreeting').mockReturnValue(of('greeting'));
    fixture.detectChanges();

    component.messages.set([
      { role: 'user', content: 'hello', timestamp: new Date() },
      { role: 'bot', content: 'hi there', timestamp: new Date() },
    ]);
    fixture.detectChanges();

    const messageEls = fixture.nativeElement.querySelectorAll('.chat__message');
    expect(messageEls.length).toBe(2);
    expect(fixture.nativeElement.textContent).toContain('hello');
    expect(fixture.nativeElement.textContent).toContain('hi there');
  });

  it('should style user and bot messages differently', async () => {
    const { fixture, component } = await setup();
    const chatbot = TestBed.inject(ChatbotService);
    vi.spyOn(chatbot, 'getGreeting').mockReturnValue(of('greeting'));
    fixture.detectChanges();

    component.messages.set([
      { role: 'user', content: 'user msg', timestamp: new Date() },
      { role: 'bot', content: 'bot msg', timestamp: new Date() },
    ]);
    fixture.detectChanges();

    const messages = fixture.nativeElement.querySelectorAll('.chat__message');
    expect(messages[0].classList.contains('chat__message--user')).toBe(true);
    expect(messages[1].classList.contains('chat__message--user')).toBe(false);
  });

  it('should show user prompt prefix for user messages', async () => {
    const { fixture, component } = await setup();
    const chatbot = TestBed.inject(ChatbotService);
    vi.spyOn(chatbot, 'getGreeting').mockReturnValue(of('greeting'));
    fixture.detectChanges();

    component.messages.set([
      { role: 'user', content: 'test', timestamp: new Date() },
    ]);
    fixture.detectChanges();

    expect(fixture.nativeElement.querySelector('.chat__prompt-user')).toBeTruthy();
    expect(fixture.nativeElement.querySelector('.chat__prompt-bot')).toBeFalsy();
  });

  it('should show bot prompt prefix for bot messages', async () => {
    const { fixture, component } = await setup();
    const chatbot = TestBed.inject(ChatbotService);
    vi.spyOn(chatbot, 'getGreeting').mockReturnValue(of('greeting'));
    fixture.detectChanges();

    component.messages.set([
      { role: 'bot', content: 'reply', timestamp: new Date() },
    ]);
    fixture.detectChanges();

    expect(fixture.nativeElement.querySelector('.chat__prompt-bot')).toBeTruthy();
    expect(fixture.nativeElement.querySelector('.chat__prompt-user')).toBeFalsy();
  });

  it('should add user message and call sendMessage on submit', async () => {
    const { fixture, component } = await setup();
    const chatbot = TestBed.inject(ChatbotService);
    vi.spyOn(chatbot, 'getGreeting').mockReturnValue(of('greeting'));
    const sendSpy = vi.spyOn(chatbot, 'sendMessage').mockReturnValue(of({ reply: 'Bot reply', toolsUsed: [] }));
    fixture.detectChanges();

    component.inputValue = 'hello';
    component.sendMessage();
    fixture.detectChanges();

    expect(sendSpy).toHaveBeenCalledWith('hello', [{ role: 'bot', content: 'greeting' }]);
    expect(component.messages().length).toBe(3);
    expect(component.messages()[0].role).toBe('bot');
    expect(component.messages()[0].content).toBe('greeting');
    expect(component.messages()[1].role).toBe('user');
    expect(component.messages()[1].content).toBe('hello');
    expect(component.messages()[2].role).toBe('bot');
    expect(component.messages()[2].content).toBe('Bot reply');
    expect(component.inputValue).toBe('');
  });

  it('should add bot response after sendMessage', async () => {
    const { fixture, component } = await setup();
    const chatbot = TestBed.inject(ChatbotService);
    vi.spyOn(chatbot, 'getGreeting').mockReturnValue(of('greeting'));
    vi.spyOn(chatbot, 'sendMessage').mockReturnValue(of({ reply: 'Bot reply', toolsUsed: [] }));
    fixture.detectChanges();

    component.inputValue = 'hello';
    component.sendMessage();
    fixture.detectChanges();

    expect(component.messages().length).toBe(3);
    expect(component.messages()[2].role).toBe('bot');
    expect(component.messages()[2].content).toBe('Bot reply');
  });

  it('should not send empty message', async () => {
    const { component } = await setup();
    const chatbot = TestBed.inject(ChatbotService);
    vi.spyOn(chatbot, 'getGreeting').mockReturnValue(of('greeting'));
    const sendSpy = vi.spyOn(chatbot, 'sendMessage');

    component.inputValue = '   ';
    component.sendMessage();

    expect(sendSpy).not.toHaveBeenCalled();
  });

  it('should not send message while loading', async () => {
    const { component } = await setup();
    const chatbot = TestBed.inject(ChatbotService);
    const sendSpy = vi.spyOn(chatbot, 'sendMessage');

    component.isLoading.set(true);
    component.inputValue = 'hello';
    component.sendMessage();

    expect(sendSpy).not.toHaveBeenCalled();
  });

  it('should disable input while loading', async () => {
    const { fixture, component } = await setup();
    component.isLoading.set(true);
    fixture.detectChanges();
    expect(component.isLoading()).toBe(true);
  });

  it('should enable input when not loading', async () => {
    const { fixture, component } = await setup();
    const chatbot = TestBed.inject(ChatbotService);
    vi.spyOn(chatbot, 'getGreeting').mockReturnValue(of('greeting'));
    fixture.detectChanges();
    const input = fixture.nativeElement.querySelector('.chat__input-field') as HTMLInputElement;
    expect(input.disabled).toBe(false);
  });

  it('should show error message when sendMessage fails', async () => {
    const { fixture, component } = await setup();
    const chatbot = TestBed.inject(ChatbotService);
    vi.spyOn(chatbot, 'getGreeting').mockReturnValue(of('greeting'));
    vi.spyOn(chatbot, 'sendMessage').mockReturnValue(
      throwError(() => ({ error: { message: 'rate limit exceeded' }, status: 429, message: 'Too Many Requests' })),
    );
    fixture.detectChanges();

    component.inputValue = 'hello';
    component.sendMessage();
    fixture.detectChanges();

    expect(component.messages().length).toBe(3);
    expect(component.messages()[2].role).toBe('bot');
    expect(component.messages()[2].content).toBe('[error] rate limit exceeded');
    expect(component.isLoading()).toBe(false);
  });

  it('should show generic error when error has no message', async () => {
    const { fixture, component } = await setup();
    const chatbot = TestBed.inject(ChatbotService);
    vi.spyOn(chatbot, 'getGreeting').mockReturnValue(of('greeting'));
    vi.spyOn(chatbot, 'sendMessage').mockReturnValue(
      throwError(() => ({ status: 500 })),
    );
    fixture.detectChanges();

    component.inputValue = 'hello';
    component.sendMessage();
    fixture.detectChanges();

    expect(component.messages().length).toBe(3);
    expect(component.messages()[2].content).toContain('an error occurred');
    expect(component.isLoading()).toBe(false);
  });

  it('should reload cart when sendMessage returns add_to_cart tool', async () => {
    const { fixture, component, httpMock } = await setup();
    const chatbot = TestBed.inject(ChatbotService);
    const cartService = TestBed.inject(CartService);
    vi.spyOn(chatbot, 'getGreeting').mockReturnValue(of('greeting'));
    vi.spyOn(chatbot, 'sendMessage').mockReturnValue(of({ reply: 'Added!', toolsUsed: ['add_to_cart'] }));
    fixture.detectChanges();

    component.inputValue = 'add item';
    component.sendMessage();
    httpMock.expectOne(environment.apiUrl + '/api/cart').flush([]);
    fixture.detectChanges();

    expect((cartService as any).items().length).toBe(0);
  });

  it('should reload cart when sendMessage returns remove_from_cart tool', async () => {
    const { fixture, component, httpMock } = await setup();
    const chatbot = TestBed.inject(ChatbotService);
    const cartService = TestBed.inject(CartService);
    vi.spyOn(chatbot, 'getGreeting').mockReturnValue(of('greeting'));
    vi.spyOn(chatbot, 'sendMessage').mockReturnValue(of({ reply: 'Removed!', toolsUsed: ['remove_from_cart'] }));
    fixture.detectChanges();

    component.inputValue = 'remove item';
    component.sendMessage();
    httpMock.expectOne(environment.apiUrl + '/api/cart').flush([]);
    fixture.detectChanges();

    expect((cartService as any).items().length).toBe(0);
  });

  it('should not reload cart when sendMessage has no cart tools', async () => {
    const { fixture, component, httpMock } = await setup();
    const chatbot = TestBed.inject(ChatbotService);
    vi.spyOn(chatbot, 'getGreeting').mockReturnValue(of('greeting'));
    vi.spyOn(chatbot, 'sendMessage').mockReturnValue(of({ reply: 'OK', toolsUsed: ['search_products'] }));
    fixture.detectChanges();

    component.inputValue = 'search';
    component.sendMessage();
    fixture.detectChanges();

    httpMock.verify();
  });

  it('should clear messages on clear command', async () => {
    const { component } = await setup();
    const chatbot = TestBed.inject(ChatbotService);
    vi.spyOn(chatbot, 'getGreeting').mockReturnValue(of('greeting'));

    component.messages.set([
      { role: 'user', content: 'hello', timestamp: new Date() },
      { role: 'bot', content: 'hi', timestamp: new Date() },
    ]);
    component.inputValue = 'clear';
    component.sendMessage();

    expect(component.messages().length).toBe(0);
    expect(component.inputValue).toBe('');
  });

  it('should show timestamps on messages', async () => {
    const { fixture, component } = await setup();
    const chatbot = TestBed.inject(ChatbotService);
    vi.spyOn(chatbot, 'getGreeting').mockReturnValue(of('greeting'));
    fixture.detectChanges();

    component.messages.set([
      { role: 'user', content: 'hello', timestamp: new Date('2026-01-01T12:30:45') },
    ]);
    fixture.detectChanges();

    const timestamp = fixture.nativeElement.querySelector('.chat__prompt-timestamp');
    expect(timestamp).toBeTruthy();
    expect(timestamp.textContent).toContain('12:30:45');
  });

  it('should navigate command history on arrow up/down', async () => {
    const { component } = await setup();
    const chatbot = TestBed.inject(ChatbotService);
    vi.spyOn(chatbot, 'sendMessage').mockReturnValue(of({ reply: 'ok', toolsUsed: [] }));

    component.inputValue = 'first';
    component.sendMessage();
    expect(component['commandHistory'].length).toBe(1);

    component.inputValue = 'second';
    component.sendMessage();
    expect(component['commandHistory'].length).toBe(2);

    const upEvent = new KeyboardEvent('keydown', { key: 'ArrowUp' });
    component.onKeydown(upEvent);
    expect(component.inputValue).toBe('second');

    component.onKeydown(upEvent);
    expect(component.inputValue).toBe('first');

    const downEvent = new KeyboardEvent('keydown', { key: 'ArrowDown' });
    component.onKeydown(downEvent);
    expect(component.inputValue).toBe('second');
  });

  it('should not navigate arrow up with empty history', async () => {
    const { component } = await setup();
    const event = new KeyboardEvent('keydown', { key: 'ArrowUp' });
    component.onKeydown(event);
    expect(component.inputValue).toBe('');
  });

  it('should reset to empty on arrow down when at newest', async () => {
    const { component } = await setup();
    const chatbot = TestBed.inject(ChatbotService);
    vi.spyOn(chatbot, 'sendMessage').mockReturnValue(of({ reply: 'ok', toolsUsed: [] }));

    component.inputValue = 'hello';
    component.sendMessage();

    const upEvent = new KeyboardEvent('keydown', { key: 'ArrowUp' });
    component.onKeydown(upEvent);
    expect(component.inputValue).toBe('hello');

    const downEvent = new KeyboardEvent('keydown', { key: 'ArrowDown' });
    component.onKeydown(downEvent);
    expect(component.inputValue).toBe('');
  });

  it('should ignore other keydown keys', async () => {
    const { component } = await setup();
    component.inputValue = 'test';
    const event = new KeyboardEvent('keydown', { key: 'Escape' });
    const preventDefault = vi.spyOn(event, 'preventDefault');
    component.onKeydown(event);
    expect(preventDefault).not.toHaveBeenCalled();
    expect(component.inputValue).toBe('test');
  });

  it('isTyping should return flag state', async () => {
    const { component } = await setup();
    expect(component.isTyping()).toBe(false);
  });

  it('formatTimestamp should format date correctly', async () => {
    const { component } = await setup();
    const date = new Date('2026-06-15T08:05:03');
    expect(component.formatTimestamp(date)).toBe('08:05:03');
  });

  it('formatTimestamp should pad single digits', async () => {
    const { component } = await setup();
    const date = new Date('2026-01-01T01:01:01');
    expect(component.formatTimestamp(date)).toBe('01:01:01');
  });

  it('updateCursorPos should set cursor position from event', async () => {
    const { component } = await setup();
    const event = { target: { selectionStart: 3 } } as unknown as Event;
    component.updateCursorPos(event);
    expect(component.cursorPos()).toBe(3);
  });

  it('updateCursorPos should fallback to length when selectionStart is null', async () => {
    const { component } = await setup();
    component.inputValue = 'hello';
    const event = { target: { selectionStart: null } } as unknown as Event;
    component.updateCursorPos(event);
    expect(component.cursorPos()).toBe(5);
  });

  it('should not send message when trimmed text is empty', async () => {
    const { component } = await setup();
    const chatbot = TestBed.inject(ChatbotService);
    vi.spyOn(chatbot, 'getGreeting').mockReturnValue(of('greeting'));
    vi.spyOn(chatbot, 'sendMessage');

    component.inputValue = '';
    component.sendMessage();
    component.inputValue = '   ';
    component.sendMessage();

    expect(chatbot.sendMessage).not.toHaveBeenCalled();
  });
});

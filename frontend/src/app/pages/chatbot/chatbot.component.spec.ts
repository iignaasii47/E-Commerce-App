import { TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';
import { of, throwError } from 'rxjs';
import { ChatbotComponent } from './chatbot.component';
import { ChatbotService } from '../../services/chatbot.service';

describe('ChatbotComponent', () => {
  async function setup() {
    await TestBed.configureTestingModule({
      imports: [ChatbotComponent],
      providers: [provideRouter([])],
    }).compileComponents();
    const fixture = TestBed.createComponent(ChatbotComponent);
    const component = fixture.componentInstance;
    return { fixture, component };
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

  it('should show typing indicator when loading', async () => {
    const { fixture, component } = await setup();
    component.isLoading.set(true);
    fixture.detectChanges();
    const indicator = fixture.nativeElement.querySelector('.chat__typing-indicator') as HTMLElement;
    expect(indicator).toBeTruthy();
    expect(indicator.textContent).toContain('typing');
  });

  it('should hide typing indicator when not loading', async () => {
    const { fixture, component } = await setup();
    const chatbot = TestBed.inject(ChatbotService);
    vi.spyOn(chatbot, 'getGreeting').mockReturnValue(of('greeting'));
    fixture.detectChanges();
    expect(component.isLoading()).toBe(false);
    expect(fixture.nativeElement.querySelector('.chat__typing-indicator')).toBeFalsy();
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
    const sendSpy = vi.spyOn(chatbot, 'sendMessage').mockReturnValue(of('Bot reply'));
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
    vi.spyOn(chatbot, 'sendMessage').mockReturnValue(of('Bot reply'));
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
});

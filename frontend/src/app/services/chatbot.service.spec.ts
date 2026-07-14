import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { ChatbotService } from './chatbot.service';
import { AuthService } from './auth.service';
import { environment } from '../../environments/environment';

describe('ChatbotService', () => {
  let service: ChatbotService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()],
    });
    service = TestBed.inject(ChatbotService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('sendMessage should POST to /api/chat and return reply', () => {
    const history = [{ role: 'user', content: 'hello' }];
    let reply: string | undefined;

    service.sendMessage('hello', history).subscribe((r) => {
      reply = r;
    });

    const req = httpMock.expectOne(environment.apiUrl + '/api/chat');
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual({ message: 'hello', history });
    req.flush({ reply: 'Hi there!' });

    expect(reply).toBe('Hi there!');
  });

  it('sendMessage should send conversation history', () => {
    const history = [
      { role: 'user', content: 'hi' },
      { role: 'assistant', content: 'hello' },
    ];

    service.sendMessage('what do you have?', history).subscribe();
    const req = httpMock.expectOne(environment.apiUrl + '/api/chat');
    expect(req.request.body.history).toEqual(history);
    req.flush({ reply: 'ok' });
  });

  it('getGreeting should return a greeting containing username', () => {
    vi.useFakeTimers();
    const auth = TestBed.inject(AuthService);
    (auth as any).currentUser.set({ id: 1, username: 'testuser', email: 't@t.com', token: 't' });

    let greeting: string | undefined;
    service.getGreeting().subscribe((g) => {
      greeting = g;
    });

    vi.advanceTimersByTime(600);
    expect(greeting).toBeDefined();
    expect(greeting).toContain('testuser');
    expect(greeting).toContain('terminal shopping assistant');
    vi.useRealTimers();
  });

  it('getGreeting should use guest when not logged in', () => {
    vi.useFakeTimers();
    const auth = TestBed.inject(AuthService);
    auth.logout();

    let greeting: string | undefined;
    service.getGreeting().subscribe((g) => {
      greeting = g;
    });

    vi.advanceTimersByTime(600);
    expect(greeting).toBeDefined();
    expect(greeting).toContain('guest');
    vi.useRealTimers();
  });

  it('getGreeting should say Good morning in the morning', () => {
    vi.useFakeTimers();
    vi.setSystemTime(new Date('2026-07-14T08:00:00'));

    let greeting: string | undefined;
    service.getGreeting().subscribe((g) => {
      greeting = g;
    });

    vi.advanceTimersByTime(600);
    expect(greeting).toContain('Good morning');
    vi.useRealTimers();
  });

  it('getGreeting should say Good afternoon in the afternoon', () => {
    vi.useFakeTimers();
    vi.setSystemTime(new Date('2026-07-14T14:00:00'));

    let greeting: string | undefined;
    service.getGreeting().subscribe((g) => {
      greeting = g;
    });

    vi.advanceTimersByTime(600);
    expect(greeting).toContain('Good afternoon');
    vi.useRealTimers();
  });

  it('getGreeting should say Good evening at night', () => {
    vi.useFakeTimers();
    vi.setSystemTime(new Date('2026-07-14T20:00:00'));

    let greeting: string | undefined;
    service.getGreeting().subscribe((g) => {
      greeting = g;
    });

    vi.advanceTimersByTime(600);
    expect(greeting).toContain('Good evening');
    vi.useRealTimers();
  });
});

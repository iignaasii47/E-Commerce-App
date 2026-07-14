import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, of } from 'rxjs';
import { delay } from 'rxjs/operators';
import { map } from 'rxjs/operators';
import { environment } from '../../environments/environment';
import { AuthService } from './auth.service';

interface ChatRequest {
  message: string;
  history: { role: string; content: string }[];
}

interface ChatResponse {
  reply: string;
}

@Injectable({ providedIn: 'root' })
export class ChatbotService {
  private readonly http = inject(HttpClient);
  private readonly auth = inject(AuthService);

  private readonly apiUrl = environment.apiUrl + '/api/chat';

  sendMessage(message: string, history: { role: string; content: string }[]): Observable<string> {
    const body: ChatRequest = { message, history };
    return this.http.post<ChatResponse>(this.apiUrl, body).pipe(
      map((res) => res.reply),
    );
  }

  getGreeting(): Observable<string> {
    const hour = new Date().getHours();
    let timeGreeting: string;
    if (hour < 12) timeGreeting = 'Good morning';
    else if (hour < 18) timeGreeting = 'Good afternoon';
    else timeGreeting = 'Good evening';

    const username = this.auth.username();
    return of(
      `${timeGreeting}, ${username}! I'm your terminal shopping assistant.\n` +
        'Ask me about the application features, tech stack, the developer\'s CV, or type `help` to see what I can do.',
    ).pipe(delay(600));
  }
}

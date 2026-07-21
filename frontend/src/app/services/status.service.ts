import { Injectable, inject, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../environments/environment';

export interface TechStackItem {
  usage: string;
  framework: string;
}

interface StatusResponse {
  api: string;
  database: string;
  timestamp: string;
}

@Injectable({ providedIn: 'root' })
export class StatusService {
  private readonly http = inject(HttpClient);

  readonly apiStatus = signal<'online' | 'offline' | 'checking'>('checking');
  readonly dbStatus = signal<'online' | 'offline' | 'unknown'>('unknown');

  readonly techStack: TechStackItem[] = [
    { usage: 'frontend framework', framework: 'Angular 22 / Typescript 6' },
    { usage: 'styling', framework: 'SCSS' },
    { usage: 'state management', framework: 'Angular Signals' },
    { usage: 'reactive programming', framework: 'RxJS 7.8' },
    { usage: 'frontend testing', framework: 'Vitest 4.0' },
    { usage: 'backend framework', framework: 'Spring Boot 4.1/Java 26' },
    { usage: 'ORM', framework: 'Spring Data JPA / Hibernate' },
    { usage: 'database', framework: 'PostgreSQL 16' },
    { usage: 'DB migrations', framework: 'Flyway' },
    { usage: 'authentication', framework: 'Spring Security + JWT 0.12' },
    { usage: 'API docs', framework: 'SpringDoc OpenAPI 2.8' },
    { usage: 'AI integration', framework: 'OpenRouter API' },
    { usage: 'build tool', framework: 'Maven 3.9' },
    { usage: 'code generation', framework: 'Lombok' },
    { usage: 'containerization', framework: 'Docker' },
    { usage: 'orchestration', framework: 'Docker Compose' },
    { usage: 'CI/CD', framework: 'GitHub Actions' },
  ];

  constructor() {
    this.checkStatus();
  }

  checkStatus(): void {
    this.apiStatus.set('checking');
    this.http.get<StatusResponse>(`${environment.apiUrl}/api/status`).subscribe({
      next: (res) => {
        this.apiStatus.set(res.api === 'UP' ? 'online' : 'offline');
        this.dbStatus.set(res.database === 'UP' ? 'online' : 'offline');
      },
      error: () => {
        this.apiStatus.set('offline');
        this.dbStatus.set('offline');
      },
    });
  }
}

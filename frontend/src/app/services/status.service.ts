import { Injectable, inject, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../environments/environment';

export interface TechStackItem {
  name: string;
  version?: string;
  description: string;
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
    { name: 'Angular', version: '22', description: 'frontend framework' },
    { name: 'TypeScript', version: '6', description: 'language' },
    { name: 'Spring Boot', version: '4.1', description: 'backend framework' },
    { name: 'Java', version: '26', description: 'language' },
    { name: 'PostgreSQL', description: 'database' },
    { name: 'JWT', version: '0.12', description: 'authentication' },
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

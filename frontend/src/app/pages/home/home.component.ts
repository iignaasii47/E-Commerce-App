import { Component, inject } from '@angular/core';
import { RouterLink } from '@angular/router';
import { AuthService } from '../../services';
import { StatusService } from '../../services/status.service';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [RouterLink],
  template: `
    <div class="page-container home">
      <pre class="ascii-banner">{{ asciiArt }}</pre>

      <p class="tagline">> terminal-based commerce interface v1.0.0</p>

      <div class="quick-actions">
        <p class="section-label">// quick actions</p>
        <div class="action-grid">
          <a routerLink="/cart" class="action-item">
            <span class="action-cmd">$ cat cart</span>
            <span class="action-desc">view cart</span>
          </a>
          <a routerLink="/products" class="action-item">
            <span class="action-cmd">$ grep .</span>
            <span class="action-desc">search catalog</span>
          </a>
          <a routerLink="/chatbot" class="action-item">
            <span class="action-cmd">$ ./assistant</span>
            <span class="action-desc">chatbot assistant</span>
          </a>
        </div>
      </div>

      <div class="system-info">
        <p class="section-label">// tech stack</p>
        <div class="info-grid">
          @for (item of status.techStack; track item.name) {
            <div class="info-row">
              <span class="info-key">{{ item.name }}:</span>
              <span class="info-val">{{ item.version ? 'v' + item.version + ' — ' : '' }}{{ item.description }}</span>
            </div>
          }
        </div>
      </div>

      <div class="system-info">
        <p class="section-label">// service status</p>
        <div class="info-grid">
          <div class="info-row">
            <span class="info-key">api:</span>
            <span class="info-val">
              <span class="status-dot" [class.online]="status.apiStatus() === 'online'" [class.offline]="status.apiStatus() === 'offline'"></span>
              {{ status.apiStatus() }}
            </span>
          </div>
          <div class="info-row">
            <span class="info-key">database:</span>
            <span class="info-val">
              <span class="status-dot" [class.online]="status.dbStatus() === 'online'" [class.offline]="status.dbStatus() === 'offline'"></span>
              {{ status.dbStatus() }}
            </span>
          </div>
          <div class="info-row">
            <span class="info-key">session:</span>
            <span class="info-val">{{ auth.username() }}&#64;term-shop</span>
          </div>
        </div>
      </div>
    </div>
  `,
  styles: `
    .home {
      display: flex;
      flex-direction: column;
      gap: 24px;
    }

    .ascii-banner {
      color: var(--accent-green);
      font-size: 10px;
      line-height: 1.2;
      text-align: center;
      opacity: 0.8;
      overflow-x: auto;
    }

    .tagline {
      text-align: center;
      color: var(--text-muted);
      font-size: 12px;
    }

    .action-grid {
      display: grid;
      grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
      gap: 8px;
    }

    .action-item {
      display: flex;
      flex-direction: column;
      gap: 4px;
      padding: 10px 12px;
      border: 1px solid var(--border);
      background: var(--bg-secondary);
      color: inherit;
      text-decoration: none;
      transition: all 0.15s;

      &:hover {
        border-color: var(--accent-green);
        background: var(--bg-hover);

        .action-cmd {
          color: var(--accent-green);
        }
      }
    }

    .action-cmd {
      color: var(--text-bright);
      font-size: 13px;
      font-weight: 600;
      transition: color 0.15s;
    }

    .action-desc {
      color: var(--text-muted);
      font-size: 11px;
    }

    .info-grid {
      display: flex;
      flex-direction: column;
      gap: 4px;
      padding: 10px 12px;
      border: 1px solid var(--border);
      background: var(--bg-secondary);
    }

    .info-row {
      display: flex;
      gap: 10px;
      font-size: 12px;
    }

    .info-key {
      color: var(--text-muted);
      min-width: 100px;
    }

    .info-val {
      color: var(--text-primary);
      display: flex;
      align-items: center;
      gap: 6px;
    }

    .status-dot {
      display: inline-block;
      width: 8px;
      height: 8px;
      border-radius: 50%;
      background: var(--text-muted);

      &.online {
        background: var(--accent-green);
        box-shadow: 0 0 6px var(--accent-green);
      }

      &.offline {
        background: var(--accent-red);
      }
    }
  `,
})
export class HomeComponent {
  readonly auth = inject(AuthService);
  readonly status = inject(StatusService);

  readonly asciiArt = String.raw`
 __        _______ ____ ____  _   _  ___  ____    _____ _   _ ___ 
 \ \      / / ____| __ ) ___|| | | |/ _ \|  _ \  |_   _| | | |_ _|
  \ \ /\ / /|  _| |  _ \___ \| |_| | | | | |_) |   | | | | | || | 
   \ V  V / | |___| |_) |__) |  _  | |_| |  __/    | | | |_| || | 
    \_/\_/  |_____|____/____/|_| |_|\___/|_|       |_|  \___/|___|`;
}

import { Component, inject } from '@angular/core';
import { RouterLink } from '@angular/router';
import { AuthService } from '../../services';

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
        </div>
      </div>

      <div class="system-info">
        <p class="section-label">// system info</p>
        <div class="info-grid">
          <div class="info-row">
            <span class="info-key">products:</span>
            <span class="info-val">10 items in stock</span>
          </div>
          <div class="info-row">
            <span class="info-key">categories:</span>
            <span class="info-val">peripherals, displays, audio, accessories, storage</span>
          </div>
          <div class="info-row">
            <span class="info-key">status:</span>
            <span class="info-val info-val--green">operational</span>
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

    .section-label {
      color: var(--text-muted);
      font-size: 11px;
      margin-bottom: 10px;
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
    }

    .info-val--green {
      color: var(--accent-green);
    }
  `,
})
export class HomeComponent {
  readonly auth = inject(AuthService);

  readonly asciiArt = String.raw`
 __        _______ ____ ____  _   _  ___  ____    _____ _   _ ___ 
 \ \      / / ____| __ ) ___|| | | |/ _ \|  _ \  |_   _| | | |_ _|
  \ \ /\ / /|  _| |  _ \___ \| |_| | | | | |_) |   | | | | | || | 
   \ V  V / | |___| |_) |__) |  _  | |_| |  __/    | | | |_| || | 
    \_/\_/  |_____|____/____/|_| |_|\___/|_|       |_|  \___/|___|`;
}

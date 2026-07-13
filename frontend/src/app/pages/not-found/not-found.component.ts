import { Component } from '@angular/core';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'app-not-found',
  standalone: true,
  imports: [RouterLink],
  template: `
    <div class="page-container not-found">
      <pre class="error-ascii">{{ asciiArt }}</pre>
      <p class="error-code">404</p>
      <p class="error-msg">$ command not found: {{ currentPath }}</p>
      <a routerLink="/" class="back-link">cd ~</a>
    </div>
  `,
  styles: `
    .not-found {
      display: flex;
      flex-direction: column;
      align-items: center;
      justify-content: center;
      text-align: center;
      gap: 8px;
    }

    .error-ascii {
      color: var(--accent-red);
      font-size: 10px;
      line-height: 1.2;
    }

    .error-code {
      font-size: 48px;
      font-weight: 800;
      color: var(--accent-red);
      opacity: 0.3;
    }

    .error-msg {
      color: var(--text-muted);
      font-size: 13px;
    }

    .back-link {
      margin-top: 12px;
      color: var(--accent-cyan);
      font-size: 12px;

      &:hover {
        color: var(--accent-green);
      }
    }
  `,
})
export class NotFoundComponent {
  currentPath = globalThis.location.pathname;

  readonly asciiArt = String.raw`
 _____ ____  ____   ___  ____
| ____|  _ \|  _ \ / _ \|  _ \
|  _| | |_) | |_) | | | | | | |
| |___|  _ <|  _ <| |_| | |_| |
|_____|_| \_\_| \_\\___/|____/`;
}

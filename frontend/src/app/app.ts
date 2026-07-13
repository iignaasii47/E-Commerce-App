import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { TerminalTitlebarComponent } from './components/layout/terminal-titlebar/terminal-titlebar.component';
import { TerminalStatusbarComponent } from './components/layout/terminal-statusbar/terminal-statusbar.component';
import { NotificationComponent } from './components/notification/notification.component';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet, TerminalTitlebarComponent, TerminalStatusbarComponent, NotificationComponent],
  templateUrl: './app.html',
  styleUrl: './app.scss',
})
export class App {}

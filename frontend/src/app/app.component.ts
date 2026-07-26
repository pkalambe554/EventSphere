import { Component } from '@angular/core';
import { RouterLink, RouterOutlet } from '@angular/router';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet,RouterLink],
  template: `  <nav style="padding: 1rem 1.5rem; display: flex; gap: 1.5rem; font-family: sans-serif;">
      <a routerLink="/events">Events</a>
      <a routerLink="/login">Login</a>
      <a routerLink="/register">Register</a>
    </nav>
    <router-outlet></router-outlet>`
})
export class AppComponent {}

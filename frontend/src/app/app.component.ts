import { Component } from '@angular/core';
import { RouterLink, RouterOutlet } from '@angular/router';
import { HeaderComponent } from './shared/header/header.component';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet,RouterLink,HeaderComponent],
  template: ` 
  <app-header></app-header>
  <router-outlet></router-outlet>
  `
})
export class AppComponent {}

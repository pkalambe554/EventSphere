import { Component } from '@angular/core';
import { RouterLink, RouterOutlet } from '@angular/router';
import { HeaderComponent } from './shared/header/header.component';
import { ToastContainerComponent } from './shared/toast/toast.component';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet,RouterLink,HeaderComponent,ToastContainerComponent],
  template: ` 
  <app-header></app-header>
  <app-toast-container></app-toast-container>
  <router-outlet></router-outlet>
  `
})
export class AppComponent {}

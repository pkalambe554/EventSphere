import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';

// TODO: inject ApiService, call an endpoint, render the result.
@Component({
  selector: 'app-home',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './home.component.html'
})
export class HomeComponent {}
